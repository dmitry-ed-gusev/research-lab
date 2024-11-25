import logging

log = logging.getLogger(__name__)


def log_model_init(**kwargs):
    instance = kwargs.get('instance')
    log.debug(f'Model instance created: {instance.__class__.__name__}.')
