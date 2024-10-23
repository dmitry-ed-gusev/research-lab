from django.urls import path

from . import views

app_name = 'ships'

urlpatterns = [
    path('', views.shipview),
]
