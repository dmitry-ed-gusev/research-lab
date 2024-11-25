# pull official base image for python 3.10
FROM python:3.10.14-bookworm

# set work directory
WORKDIR /usr/src/app

# set python environment variables:
#  - prevents python from writing pyc files to disc
ENV PYTHONDONTWRITEBYTECODE 1
#  - prevents python from buffering stdout and stderr
ENV PYTHONUNBUFFERED 1

# upgrade pip
RUN pip install --upgrade pip

# install dependencies
COPY ./requirements.txt /usr/src/app/requirements.txt
RUN pip install -r requirements.txt

# copy project
COPY . /usr/src/app/

#EXPOSE 5000
EXPOSE 9099
