"""
    Script for init Unesco application DB with the sample data.
    Script erases all previous data.

    Usage of this script:
        `python3 manage.py runscript many_load`

    Created:  Dmitrii Gusev, 24.08.2022
    Modified:
"""

import csv
import logging
from nis import cat
from django.db.models.query import EmptyQuerySet

from unesco.models import Category, State, Iso, Region, Site

log = logging.getLogger(__name__)
log.info('Initializing Unesco application DB with sample data...')

# CSV file wit hdata
CSV_FILE = 'csv/whc-sites-2018-clean.csv'


def run():
    log.info(f'Loading data for Unesco app DB from [{CSV_FILE}] file.')

    fhand = open(CSV_FILE)
    reader = csv.reader(fhand)
    next(reader)  # Advance past the header
    log.debug(f'CSV file [{CSV_FILE}] opened OK.')

    # cleanup database - remove all data
    Category.objects.all().delete()  # dict table
    State.objects.all().delete()  # dict table
    Iso.objects.all().delete()  # dict table
    Region.objects.all().delete()  # dict table
    Site.objects.all().delete()  # main table
    log.debug('Unesco DB cleaned OK.')

    for row in reader:
        print(row)

        # create or get existing records for the lookup tables
        category, created = Category.objects.get_or_create(name=row[7])
        if created:
            log.debug(f'Created new Category: {category}.')

        state, created = State.objects.get_or_create(name=row[8])
        if created:
            log.debug(f'Created new State: {state}.')

        region, created = Region.objects.get_or_create(name=row[9])
        if created:
            log.debug(f'Created new Region: {region}.')

        iso, created = Iso.objects.get_or_create(name=row[10])
        if created:
            log.debug(f'Created new ISO: {iso}.')

        # create record for the site itself (main record)
        try:
            area = float(row[6])
        except ValueError:
            area = None
        
        site = Site(name=row[0], year=row[3], latitude=row[5], longitude=row[4], description=row[1],
                    justification=row[2], area_hectares=area, category=category, state=state,
                    region=region, iso=iso)
        site.save()

    # test loaded data
    states_count = len(State.objects.all())
    log.info(f'Unesco States count check (163): {states_count == 163}')

    sites_count = len(Site.objects.all())
    log.info(f'Unesco Sites count check (1044): {sites_count == 1044}')

    # filter returns the QuerySet, empty QuerySet evaluates to False
    india_state = State.objects.filter(name='India')
    log.info((f'India State exists: {bool(india_state)}. '
             f'Count: {len(india_state)}; {"OK" if len(india_state) == 1 else "Not OK"}.'))

# sqlite> SELECT count(id) FROM unesco_site WHERE name="Hawaii Volcanoes National Park" 
#          AND year=1987 AND area_hectares = 87940.0; -> 1
# sqlite> SELECT COUNT(*) FROM unesco_site JOIN unesco_iso ON iso_id=unesco_iso.id 
#          WHERE unesco_site.name="Maritime Greenwich" 
#           AND unesco_iso.name = "gb"; -> 1
