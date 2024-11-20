from django.urls import path

from . import views

app_name = 'polls'

urlpatterns = [
    path('owner', views.owner, name='owner'),  # path for the course autograder

    # path('', views.index, name='index'),  # sample: /polls/
    path('', views.IndexView.as_view(), name='index'),  # sample: /polls/

    # path('details/<int:question_id>/', views.detail, name='detail'),  # sample: /polls/details/5/
    path('<int:pk>/', views.DetailView.as_view(), name='detail'),  # sample: /polls/details/5/

    # path('<int:question_id>/results/', views.results, name='results'),  # sample: /polls/5/results/
    path('<int:pk>/results/', views.ResultsView.as_view(), name='results'),  # sample: /polls/5/results

    path('<int:question_id>/vote/', views.vote, name='vote'),  # sample: /polls/5/vote/
    path('getform/', views.getform, name='getform'),  # sample: /polls/getform/
    path('postform/', views.postform, name='postform'),  # sample: /polls/postform/
]
