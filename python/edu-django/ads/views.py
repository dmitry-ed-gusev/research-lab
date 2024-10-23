import logging
from django.urls import reverse_lazy, reverse
from django.views import View
from django.http import HttpResponse
from django.db.models import Q
from django.contrib.auth.mixins import LoginRequiredMixin
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.humanize.templatetags.humanize import naturaltime

from ads.models import Ad, Comment, Fav
from ads.forms import CreateForm, CommentForm
from ads.owner import OwnerListView, OwnerDetailView, OwnerDeleteView, OwnerUpdateView, OwnerCreateView

# todo: References
# todo: https://docs.djangoproject.com/en/3.0/topics/db/queries/#one-to-many-relationships

# todo: Note that the select_related() QuerySet method recursively prepopulates the
# todo: cache of all one-to-many relationships ahead of time.

# todo: sql “LIKE” equivalent in django query
# todo: https://stackoverflow.com/questions/18140838/sql-like-equivalent-in-django-query

# todo: How do I do an OR filter in a Django query?
# todo: https://stackoverflow.com/questions/739776/how-do-i-do-an-or-filter-in-a-django-query

# todo: https://stackoverflow.com/questions/1074212/how-can-i-see-the-raw-sql-queries-django-is-running

log = logging.getLogger(__name__)


def autoview(request):
    log.debug('autoview() is working.')
    log.debug(f'HTTP request: {request}')
    response: HttpResponse = HttpResponse("You're in the ADS app!")
    return response


class AdListView(OwnerListView):
    model = Ad
    # By convention:
    template_name = "ads/ad_list.html"

    def get(self, request):
        log.debug("AD List View: is working.")

        strval = request.GET.get("search", False)
        log.debug(f"Search string: {strval}")

        # get list of Ad objects from DB
        if strval:  # there is non-empty search string
            # Simple title-only search (one field)
            # ad_list = Ad.objects.filter(title__contains=strval) \
            #                     .select_related().order_by('-updated_at')[:10]

            # Multi-field search (several fields)
            # __icontains for case-insensitive search
            query = Q(title__icontains=strval)
            query.add(Q(text__icontains=strval), Q.OR)
            query.add(Q(tags__name__in=[strval]), Q.OR)
            ad_list = Ad.objects.filter(query).select_related().distinct().order_by('-updated_at')[:10]
        else:  # no search string - provide the full list
            ad_list = Ad.objects.all().order_by('-updated_at')[:10]

        # Augment the post_list (update all objects in the list - set natural time)
        for ad in ad_list:
            ad.natural_updated = naturaltime(ad.updated_at)

        favorites = list()
        if request.user.is_authenticated:
            # rows = [{'id': 2}, {'id': 4} ... ]  (A list of rows)
            rows = request.user.favorite_ads.values('id')
            # favorites = [2, 4, ...] using list comprehension
            favorites = [row['id'] for row in rows]

        # creating the context for the page: list of ADs (limited by search), favorites, search string
        ctx = {'ad_list': ad_list, 'favorites': favorites, 'search': strval}

        return render(request, self.template_name, ctx)


class AdDetailView(OwnerDetailView):
    model = Ad
    template_name = "ads/ad_detail.html"

    def get(self, request, pk):
        x = Ad.objects.get(id=pk)
        comments = Comment.objects.filter(ad=x).order_by('-updated_at')
        comment_form = CommentForm()
        context = {'ad': x, 'comments': comments, 'comment_form': comment_form}
        return render(request, self.template_name, context)


class AdCreateView(LoginRequiredMixin, View):
    template_name = 'ads/ad_form.html'
    success_url = reverse_lazy('ads:all')

    def get(self, request, pk=None):
        form = CreateForm()
        ctx = {'form': form}
        return render(request, self.template_name, ctx)

    def post(self, request, pk=None):
        form = CreateForm(request.POST, request.FILES or None)

        if not form.is_valid():
            ctx = {'form': form}
            return render(request, self.template_name, ctx)

        # Add owner to the model before saving
        pic = form.save(commit=False)
        pic.owner = self.request.user
        pic.save()

        # https://django-taggit.readthedocs.io/en/latest/forms.html#commit-false
        form.save_m2m()

        return redirect(self.success_url)


class AdUpdateView(LoginRequiredMixin, View):
    template_name = 'ads/ad_form.html'
    success_url = reverse_lazy('ads:all')

    def get(self, request, pk):
        pic = get_object_or_404(Ad, id=pk, owner=self.request.user)
        form = CreateForm(instance=pic)
        ctx = {'form': form}
        return render(request, self.template_name, ctx)

    def post(self, request, pk=None):
        pic = get_object_or_404(Ad, id=pk, owner=self.request.user)
        form = CreateForm(request.POST, request.FILES or None, instance=pic)

        if not form.is_valid():
            ctx = {'form': form}
            return render(request, self.template_name, ctx)

        pic = form.save(commit=False)
        pic.save()

        # https://django-taggit.readthedocs.io/en/latest/forms.html#commit-false
        form.save_m2m()

        return redirect(self.success_url)


class AdDeleteView(OwnerDeleteView):
    model = Ad


def stream_file(request, pk):
    """View for show picture in full size on the screen."""

    log.debug(f'stream_file(): showing picture for Ad with the id={pk}')
    pic = get_object_or_404(Ad, id=pk)
    response = HttpResponse()
    response['Content-Type'] = pic.content_type
    response['Content-Length'] = len(pic.picture)
    response.write(pic.picture)
    return response


class CommentCreateView(LoginRequiredMixin, View):

    def post(self, request, pk):
        ad = get_object_or_404(Ad, id=pk)
        comment = Comment(text=request.POST['comment'], owner=request.user, ad=ad)
        comment.save()
        return redirect(reverse('ads:ad_detail', args=[pk]))


class CommentDeleteView(OwnerDeleteView):
    model = Comment
    template_name = "ads/comment_delete.html"

    # https://stackoverflow.com/questions/26290415/deleteview-with-a-dynamic-success-url-dependent-on-id
    def get_success_url(self):
        ad = self.object.ad
        return reverse('ads:ad_detail', args=[ad.id])


# csrf exemption in class based views
# https://stackoverflow.com/questions/16458166/how-to-disable-djangos-csrf-validation
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
from django.db.utils import IntegrityError


@method_decorator(csrf_exempt, name='dispatch')
class AddFavoriteView(LoginRequiredMixin, View):
    def post(self, request, pk):
        print("Add PK", pk)
        t = get_object_or_404(Ad, id=pk)
        fav = Fav(user=request.user, ad=t)
        try:
            fav.save()  # In case of duplicate key
        except IntegrityError as e:
            pass
        return HttpResponse()


@method_decorator(csrf_exempt, name='dispatch')
class DeleteFavoriteView(LoginRequiredMixin, View):
    def post(self, request, pk):
        print("Delete PK", pk)
        t = get_object_or_404(Ad, id=pk)
        try:
            fav = Fav.objects.get(user=request.user, ad=t).delete()
        except Fav.DoesNotExist as e:
            pass

        return HttpResponse()
