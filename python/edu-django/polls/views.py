import html
from django.http import HttpResponse, HttpResponseRedirect, Http404
from django.template import loader
from django.shortcuts import render, redirect, get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from django.views import View
from django.urls import reverse
from django.views import generic

from .models import Question, Choice


# def index(request):
#    # return HttpResponse("Hello, world. You're at the polls index. The number: 64126291!")
#    latest_question_list = Question.objects.order_by('-pub_date')[:5]
#    context = {'latest_question_list': latest_question_list}
#    return render(request, 'polls/index.html', context)


class IndexView(generic.ListView):
    template_name = 'polls/index.html'
    context_object_name = 'latest_question_list'

    def get_queryset(self):
        """Return the last five published questions."""
        return Question.objects.order_by('-pub_date')[:5]


# def detail(request, question_id):
#    # try:
#    #     question = Question.objects.get(pk=question_id)
#    # except Question.DoesNotExist:
#    #     raise Http404("Question doesn't exist!")
#
#    question = get_object_or_404(Question, pk=question_id)
#    return render(request, 'polls/detail.html', {'question': question})

class DetailView(generic.DetailView):
    model = Question
    template_name = 'polls/detail.html'


# def results(request, question_id):
#    question = get_object_or_404(Question, pk=question_id)
#    return render(request, 'polls/results.html', {'question': question})
#    # response = "You're looking at the [3c3d2bb7] results of question %s."
#    # return HttpResponse(response % question_id)

class ResultsView(generic.DetailView):
    model = Question
    template_name = 'polls/results.html'


def vote(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    try:
        selected_choice = question.choice_set.get(pk=request.POST['choice'])
    except (KeyError, Choice.DoesNotExist):
        # Redisplay the question voting form.
        return render(request, 'polls/detail.html', {
            'question': question,
            'error_message': "You didn't select a choice.",
        })
    else:
        selected_choice.votes += 1
        selected_choice.save()
        # Always return an HttpResponseRedirect after successfully dealing
        # with POST data. This prevents data from being posted twice if a
        # user hits the Back button.
        return HttpResponseRedirect(reverse('polls:results', args=(question.id,)))


def owner(request):
    # return HttpResponse("Hello, world. 3c3d2bb7 is the polls owner.")
    return HttpResponse("Hello, world. 4293b9e4 is the polls owner.")


# Call as dumpdata('GET', request.GET)
def dumpdata(place, data):
    retval = ""
    if len(data) > 0:
        retval += '<p>Incoming '+place+' data:<br/>\n'
        for key, value in data.items():
            retval += html.escape(key) + '=' + html.escape(value) + '</br>\n'
        retval += '</p>\n'
    return retval


def getform(request):
    response = """<p>Impossible GET guessing game...</p>
        <form>
        <p><label for="guess">Input Guess</label>
        <input type="text" name="guess" size="40" id="guess"/></p>
        <input type="submit"/>
        </form>"""
    response += dumpdata('GET', request.GET)
    return HttpResponse(response)


@csrf_exempt
def postform(request):
    response = """<p>Impossible POST guessing game...</p>
        <form method="POST">
        <p><label for="guess">Input Guess</label>
        <input type="text" name="guess" size="40" id="guess"/></p>
        <input type="submit"/>
        </form>"""
    response += dumpdata('POST', request.POST)
    return HttpResponse(response)
