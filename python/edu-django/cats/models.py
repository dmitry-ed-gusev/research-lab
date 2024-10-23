import logging
from django.db import models
from django.db.models.signals import post_init, pre_init
from django.core.validators import MinLengthValidator

from utils.utils import log_model_init

log = logging.getLogger(__name__)


class Breed(models.Model):
    name = models.CharField(
            max_length=200,
            validators=[MinLengthValidator(2, "Breed must be greater than 1 character")]
    )

    def __str__(self):
        return self.name


class Cat(models.Model):
    nickname = models.CharField(
            max_length=200,
            validators=[MinLengthValidator(2, "Nickname must be greater than 1 character")]
    )
    # weight = models.PositiveIntegerField()  # this type is not suitable for the values from CSV
    weight = models.FloatField()
    foods = models.CharField(max_length=300)
    breed = models.ForeignKey('Breed', on_delete=models.CASCADE, null=False)

    def __str__(self):
        return self.nickname


post_init.connect(log_model_init, Breed)
post_init.connect(log_model_init, Cat)
