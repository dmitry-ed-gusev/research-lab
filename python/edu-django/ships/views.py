from django.http import HttpResponse


def shipview(request):
    response: HttpResponse = HttpResponse("You're in ships CRUD app!")

    return response
