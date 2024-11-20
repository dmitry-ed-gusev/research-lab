from django.urls import path

from . import views

app_name = 'cats'

urlpatterns = [

    # info view - just to proof that /cats appis working
    path('info/', views.autoview),

    # -- Cats urls - CRUD

    # main view for the cats app - show all cats
    path('', views.MainCatsView.as_view(), name='all_cats_list'),
    # create cat url/view
    path('main/create/', views.CatCreate.as_view(), name='cat_create'),
    # update cat url/view
    path('main/<int:pk>/update/', views.CatUpdate.as_view(), name='cat_update'),
    # delete cat
    path('main/<int:pk>/delete/', views.CatDelete.as_view(), name='cat_delete'),


    # -- Breeds urls - CRUD

    # list of breeds
    path('lookup/', views.BreedsView.as_view(), name='breeds_list'),
    # create breed
    path('lookup/create/', views.BreedCreate.as_view(), name='breed_create'),
    # update breed
    path('lookup/<int:pk>/update/', views.BreedUpdate.as_view(), name='breed_update'),
    # delete breed
    path('lookup/<int:pk>/delete/', views.BreedDelete.as_view(), name='breed_delete'),

]
