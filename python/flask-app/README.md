<!-- cspell:ignore flaskenv -->

# Simple Flask Application

[TOC]

## Useful Tech Articles/Links

This application is based on the guides:

- [Flask 3.0 docs](https://flask.palletsprojects.com/en/3.0.x/)
- [Flask for beginners I](https://habr.com/ru/articles/783574/)
- [Flask for beginners II](https://habr.com/ru/articles/784770/)
- [Mega Flask Book (2018)](https://habr.com/ru/articles/346306/)
- [Mega Flask Book (2024)](https://habr.com/ru/articles/804245/)
- [Flask + Prometheus](https://habr.com/ru/articles/518122/)
- [Flask Tutorial](https://www.geeksforgeeks.org/flask-tutorial/)
- [Flask Tutorial](https://otus.ru/journal/flask-vvedenie/)
- [Flask Tutorial](https://proglib.io/p/samouchitel-po-python-dlya-nachinayushchih-chast-23-osnovy-veb-razrabotki-na-flask-2023-06-27?ysclid=lxyaghvvrm969774221)

- [REST API and Flask](https://habr.com/ru/articles/246699/)

- [Dockerize flask app](https://testdriven.io/blog/dockerizing-flask-with-postgres-gunicorn-and-nginx/)

## Environment Setup

```bash
    # 1. create virtual environment
    python -m venv .venv --prompt .venv-flask
    source .venv/Scripts/activate

    # 2. optional - after creating virtual environment - you may update pip
    (.venv-flask) $ python -m pip install --upgrade pip

    # 3a. optional - if dependencies are not installed - install them and
    #     save dependencies list to file
    (.venv-flask) $ pip install flask
    (.venv-flask) $ pip install python-dotenv
    (.venv-flask) $ pip freeze > requirements.txt

    # 3b. install dependencies from file
    (.venv-flask) $ pip install -r requirements.txt

    # 3c. update/upgrade dependencies from requirements.txt file
    (.venv-flask) pip install --upgrade --force-reinstall --no-cache-dir -r requirements.txt
```

## Run Development Server

```bash
    # you don't need the following line in case you have .flaskenv file and
    #  module python-dotenv installed
    (.venv-flask) $ export FLASK_APP=flask-app.py

    # run development server
    (.venv-flask) $ flask run

    # run dev server with specified .env file (package python-dotenv should be installed)
    (.venv-flask) $ flask --env-file .env.dev run
```

## Dockerize Application

```bash
    # build docker image with the specified name
    docker build -t flask-app .

    # view docker image history
    docker image history flask-app

    # run new container with docker (interactive mode)
    docker run --name flask-app-container flask-app
    # run new container with docker (background mode)
    docker run -d  --name flask-app-container flask-app

    # start existing stopped(!) container
    docker start flask-app-container
    # stop existing started(!) container
    docker stop flask-app-container

    # open a shell inside a running(!) container
    docker exec -it flask-app-container sh

    # remove stopped(!) container
    docker rm flask-app-container

    # run the service via docker-compose
    docker-compose up
    # run the service in the background
    docker compose up -d
    # run the service with rebuild
    docker compose up --build
```
