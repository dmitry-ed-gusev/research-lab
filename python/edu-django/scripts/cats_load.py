"""
    Script to load data into DB from CSV file.
    See additional resources:
      - CSV: https://docs.python.org/3/library/csv.html
      - Django runscript: https://django-extensions.readthedocs.io/en/latest/runscript.html

    Usage of this script:
        `python3 manage.py runscript cats_load`

    Created:  Dmitrii Gusev (by example script), 03.08.2022
    Modified: Dmitrii Gusev, 03.08.2022
"""

import csv
import logging
from cats.models import Cat, Breed

log = logging.getLogger(__name__)

CATS_CSV_FILE = 'csv/cats.csv'


def run():
    log.info(f'Executing csv load script [{CATS_CSV_FILE}] for Cats/Breeds.')

    fhand = open(CATS_CSV_FILE)
    reader = csv.reader(fhand)
    next(reader)  # Advance past the header (skip it)

    # clean up the database - ???
    Cat.objects.all().delete()
    Breed.objects.all().delete()

    for counter, row in enumerate(reader):  # iterate and add cats/breeds row by row
        log.debug(f'Loading: #{counter}/{row}')
        b, created = Breed.objects.get_or_create(name=row[1])
        c = Cat(nickname=row[0], breed=b, weight=row[2])
        c.save()

    counter += 1
    log.info(f'Loading for {CATS_CSV_FILE} is done. Loaded #{counter} cat(s).')
