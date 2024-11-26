# -*- coding: utf-8 -*-

from flask import render_template, flash, redirect
from app import app  # from module(!) app import variable(!) app
from app.forms import LoginForm


@app.route('/status')
def status():
    return "Hello, World!<br>STATUS PAGE!<br>NAME=" + app.config['NAME']


@app.route('/about')
def about():
    return 'This is about page!'


@app.route('/')
@app.route('/index')
def index():
    user = {'username': 'Miguel'}

    posts = [
        {
            'author': {'username': 'John'},
            'body': 'Beautiful day in Portland!'
        },
        {
            'author': {'username': 'John'},
            'body': 'Winter is coming!'
        },
        {
            'author': {'username': 'Susan'},
            'body': 'The Avengers movie was so cool!'
        },
    ]

    return render_template('index.html', title='Home', user=user, posts=posts)


@app.route('/api/v1/alert', methods=['POST'])
def alert_v1():
    return "Alert received!"


@app.route('/user/<username>')
def show_user_name(username):
    return f"User: {username}"


@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()

    # we got a filled form - get the data
    if form.validate_on_submit():
        flash(f"Login requested for user {form.username.data}, remember_me={form.remember_me.data}")
        return redirect('/index')

    # show login form for the first time
    return render_template('login.html', title='Sign In', form=form)
