"""mysite URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

import os
from django.contrib import admin
from django.urls import include, path, re_path
from django.conf import settings
from django.contrib.auth import views as auth_views
from django.views.static import serve
from django.views.generic import TemplateView

# Up two folders to serve "site" content
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE_ROOT = os.path.join(BASE_DIR, 'site')

urlpatterns = [
    # main app page - for the URI -> /
    # path('', TemplateView.as_view(template_name='home/main.html')),
    path('', include('home.urls')),  # Change to ads.urls - for one of the assignments

    # django embedded admin application (see more: django doc)
    path('admin/', admin.site.urls),  # /admin - application (django internal)

    # django embedded login/logout capability
    # see more: https://docs.djangoproject.com/en/3.2/topics/auth/default/#module-django.contrib.auth.views
    path('accounts/', include('django.contrib.auth.urls')),  # django usual login/logout urls

    # processing the static content
    re_path(r'^site/(?P<path>.*)$', serve,
            {'document_root': SITE_ROOT, 'show_indexes': True},
            name='site_path'
            ),  # serve static content by address /site
    re_path(r'^oauth/', include('social_django.urls', namespace='social')),

    # simple sample applications
    path('polls/', include('polls.urls')),  # /polls - application
    path('hello/', include('hello.urls')),  # /hello - application
    path('ships/', include('ships.urls')),  # /ships - application with CRUD implemented
    path('autos/', include('autos.urls')),  # /autos - CRUD app for dj4e course assessment (week #3)
    path('cats/', include('cats.urls')),  # /cats - CRUD app for dj4e course assessment (week #4)
    path('ads/', include('ads.urls')),  # /ads - app for dj4e course assessment (week #5)
]

# Serve the favicon - Keep for later
urlpatterns += [
    path('favicon.ico', serve, {
            'path': 'favicon.ico',
            'document_root': os.path.join(BASE_DIR, 'home/static'),
        }
    ),
]

# Switch to social login if it is configured - Keep for later
try:
    from . import github_settings
    social_login = 'registration/login_social.html'
    urlpatterns.insert(0, path('accounts/login/',
                       auth_views.LoginView.as_view(template_name=social_login)))
    print('Using', social_login, 'as the login template')
except:
    print('Using registration/login.html as the login template')

# References: https://docs.djangoproject.com/en/3.0/ref/urls/#include
