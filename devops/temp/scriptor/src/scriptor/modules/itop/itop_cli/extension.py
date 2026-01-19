"""
Official way to extends itop_cli features.
"""

from .search_keys import SEARCH_KEYS


class Extension:
    """
    Official way to extends itop_cli features.
    """
    def __init__(self, search_keys=SEARCH_KEYS, **kwargs) -> None:
        super().__init__()
        self.usage = Extension.__process_args("usage", **kwargs)
        self.arguments = Extension.__process_args("arguments", **kwargs)
        self.options = Extension.__process_args("options", **kwargs)
        self.examples = Extension.__process_args("examples", **kwargs)
        self.search_keys = search_keys

    def __call__(self, *args, **kwargs):
        pass

    @staticmethod
    def __process_args(key, **kwargs):
        if key in kwargs:
            return kwargs.get(key)
        return ''
