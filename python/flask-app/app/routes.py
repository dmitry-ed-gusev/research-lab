# -*- coding: utf-8 -*-

from flask import render_template
from app import app


@app.route('/')
def root():
    return "Hello, World!<br>ROOT PAGE!<br>NAME=" + app.config['NAME']


@app.route('/about')
def about():
    return 'This is about page!'


@app.route('/index')
def index():
    user = {'username': 'Miguel'}

    posts = [
        {
            'author': {'username': 'John'},
            'body': 'Beautiful day in Portland!'
        },
        {
            'author': {'username': 'Susan'},
            'body': 'The Avengers movie was so cool!'
        }
    ]

    return render_template('index.html', title='Home', user=user, posts=posts)


@app.route('/api/v1/alert', methods=['POST'])
def alert_v1():
    return "Alert received!"


@app.route('/user/<username>')
def show_user_name(username):
    return f"User: {username}"
