import logging
from django.urls import reverse_lazy
from django.views import View
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse
from django.contrib.auth.mixins import LoginRequiredMixin
from django.views.generic.edit import CreateView, UpdateView, DeleteView

from cats.forms import MakeForm
from cats.models import Breed, Cat

log = logging.getLogger(__name__)

CATS_LIST_VIEW_URL = 'cats:all_cats_list'


def autoview(request):
    log.debug('autoview() is working.')
    log.debug(f'HTTP request: {request}')
    response: HttpResponse = HttpResponse("You're in the Cats CRUD app!")
    return response


class MainCatsView(LoginRequiredMixin, View):  # all cats main view

    def get(self, request):
        log.debug('MainCatsView: get() is working.')

        # search for breeds and cats
        breeds_count = Breed.objects.all().count()
        cats = Cat.objects.all()
        log.debug(f'Found #{breeds_count} breed(s) and #{cats.count()} cat(s).')

        # put objects into the 'context' and render+return the request
        ctx = {'breeds_count': breeds_count, 'cats_list': cats}
        return render(request, 'cats/cat_list.html', ctx)


# Take the easy way out on the main table. These views do not need a form (see it for breeds)
# because CreateView, etc. Build a form object dynamically based on the fields value in the
# constructor attributes.
# ---
# We use reverse_lazy() rather than reverse in the class attributes because views.py is loaded by urls.py
# and in urls.py as_view() causes the constructor for the view class to run before urls.py has been
# completely loaded and urlpatterns has been processed.
# See: https://docs.djangoproject.com/en/3.0/ref/class-based-views/generic-editing/#createview

class CatCreate(LoginRequiredMixin, CreateView):
    model = Cat
    fields = '__all__'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)


class CatUpdate(LoginRequiredMixin, UpdateView):
    model = Cat
    fields = '__all__'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)


class CatDelete(LoginRequiredMixin, DeleteView):
    model = Cat
    fields = '__all__'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)


class BreedsView(LoginRequiredMixin, View):  # all breeds view

    def get(self, request):
        log.debug('BreedsView: get() is working.')
        breeds = Breed.objects.all()
        ctx = {'breeds_list': breeds}
        return render(request, 'cats/breeds_list.html', ctx)


# We use reverse_lazy() because we are in "constructor attribute" code that is run before urls.py
# is completely loaded

class BreedCreate(LoginRequiredMixin, View):

    template = 'cats/breed_form.html'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)

    def get(self, request):
        log.debug('BreedCreate: get() is working.')
        form = MakeForm()
        ctx = {'form': form}
        return render(request, self.template, ctx)

    def post(self, request):
        log.debug('BreedCreate: post() is working.')
        form = MakeForm(request.POST)
        if not form.is_valid():
            ctx = {'form': form}
            return render(request, self.template, ctx)

        breed = form.save()
        log.debug(f'BreedCreate: form save() is done. Saved: {breed}.')

        return redirect(self.success_url)


# BreedUpdate has code to implement the get/post/validate/store flow. CatUpdate (see above) is doing the
# same thing with no code and no form by extending UpdateView.

class BreedUpdate(LoginRequiredMixin, View):

    model = Breed
    template = 'cats/breed_form.html'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)

    def get(self, request, pk):
        log.debug('BreedUpdate: get() is working.')
        breed = get_object_or_404(self.model, pk=pk)
        form = MakeForm(instance=breed)
        ctx = {'form': form}
        return render(request, self.template, ctx)

    def post(self, request, pk):
        log.debug('BreedUpdate: post() is working.')
        breed = get_object_or_404(self.model, pk=pk)
        form = MakeForm(request.POST, instance=breed)
        if not form.is_valid():
            ctx = {'form': form}
            return render(request, self.template, ctx)
        breed = form.save()
        log.debug(f'BreedUpdate: form save() is done. Saved: {breed}.')
        return redirect(self.success_url)


class BreedDelete(LoginRequiredMixin, View):

    model = Breed
    template = 'cats/breed_confirm_delete.html'
    success_url = reverse_lazy(CATS_LIST_VIEW_URL)

    def get(self, request, pk):
        log.debug('BreedDelete: get() is working.')
        breed = get_object_or_404(self.model, pk=pk)
        form = MakeForm(instance=breed)
        ctx = {'breed': breed}
        return render(request, self.template, ctx)

    def post(self, request, pk):
        log.debug('BreedDelete: post() is working.')
        breed = get_object_or_404(self.model, pk=pk)
        breed.delete()
        return redirect(self.success_url)
