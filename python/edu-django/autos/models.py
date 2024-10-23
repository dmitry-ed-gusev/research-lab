import logging
from django.db import models
from django.db.models.signals import post_init, pre_init
from django.core.validators import MinLengthValidator

from utils.utils import log_model_init

log = logging.getLogger(__name__)


class Make(models.Model):
    # log.debug('Models: class Make (static call)')

    name = models.CharField(
            max_length=200,
            help_text='Enter a make (e.g. Dodge)',
            validators=[MinLengthValidator(2, "Make must be greater than 1 character")]
    )

    def __str__(self):
        """String for representing the Model object."""
        return self.name


class Auto(models.Model):
    # log.debug('Models: class Auto (static call)')

    nickname = models.CharField(
            max_length=200,
            validators=[MinLengthValidator(2, "Nickname must be greater than 1 character")]
    )
    mileage = models.PositiveIntegerField()
    comments = models.CharField(max_length=300)
    make = models.ForeignKey('Make', on_delete=models.CASCADE, null=False)

    # Shows up in the admin list
    def __str__(self):
        return self.nickname


post_init.connect(log_model_init, Make)
post_init.connect(log_model_init, Auto)
