"""
    Script for init Ads application with some data. Script erases all previous data.
    This script uses faker library (see: https://github.com/joke2k/faker).

    Usage of this script:
        `python3 manage.py runscript ads_init`

    Created:  Dmitrii Gusev, 22.08.2022
    Modified:
"""

import logging
import random
from faker import Faker
from ads.models import Ad
from django.contrib.auth import get_user_model

log = logging.getLogger(__name__)
log.info('Initializing ADS application with some dummy data...')

# number of ADs to be created
NUMBER_OF_ADS = 15


def run():
    log.debug('Executing the run() method...')

    # clear the ads databes
    Ad.objects.all().delete()

    # get list of existing users
    User = get_user_model()
    users_list = list(User.objects.all())

    fake = Faker()  # init faker engine
    random.seed(15)  # init the random # generator

    for _ in range(NUMBER_OF_ADS):
        ad = Ad()
        ad.title = fake.sentence(nb_words=10)
        ad.price = float(fake.numerify(text='##%#.%#'))
        ad.text = fake.text()
        ad.owner = users_list[random.randrange(len(users_list))]  # pick up random user from existing
        log.debug(f'Generated Ad object: [{ad}]')
        ad.save()
