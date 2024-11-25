<!-- cspell:ignore flaskenv, Habr, loguru -->

# Flask Application - Research Module

[TOC]

## Useful Tech Articles/Links

This application is based on the guides:

- [Flask Framework Docs](https://flask.palletsprojects.com/en/stable/)
- [Habr :: Flask for beginners I](https://habr.com/ru/articles/783574/)
- [Habr :: Flask for beginners II](https://habr.com/ru/articles/784770/)
- [Habr :: Mega Flask Book (2018)](https://habr.com/ru/articles/346306/)
- [Habr :: Git for Mega Flask Book (2018)](https://github.com/miguelgrinberg/microblog-2018)
- [Habr :: Mega Flask Book (2024)](https://habr.com/ru/articles/804245/)
- [Habr :: Flask + Prometheus](https://habr.com/ru/articles/518122/)
- [Flask Tutorial](https://www.geeksforgeeks.org/flask-tutorial/)
- [Flask Tutorial](https://otus.ru/journal/flask-vvedenie/)
- [Flask Tutorial](https://proglib.io/p/samouchitel-po-python-dlya-nachinayushchih-chast-23-osnovy-veb-razrabotki-na-flask-2023-06-27?ysclid=lxyaghvvrm969774221)

- [REST API and Flask](https://habr.com/ru/articles/246699/)

- [Dockerize flask app](https://testdriven.io/blog/dockerizing-flask-with-postgres-gunicorn-and-nginx/)

## Environment Setup

```bash
    # 1. - create virtual environment and activate it
    python -m venv .venv --prompt .venv-flask-app
    source .venv/Scripts/activate
    # 2. - optional - after creating virtual environment - you may upgrade pip
    (.venv-flask) $ python -m pip install --upgrade pip
    # 3. - install dependencies from file - preferred way
    (.venv-flask) $ pip install -r requirements.txt
    # 4. - optional - reinstall dependencies from requirements.txt file
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
