from django.http import HttpResponse


def myview(request):

    # read cookie value from the request object
    counter = int(request.COOKIES.get('view_count_cookie', '0'))

    # increase cookie value
    counter += 1

    # create the response object
    response: HttpResponse = HttpResponse(f"OK!\nview count={counter}")
    # set required for assessment cookie
    response.set_cookie('dj4e_cookie', '4293b9e4', max_age=1000)
    # set view counts cookie
    response.set_cookie('view_count_cookie', str(counter))

    return response
