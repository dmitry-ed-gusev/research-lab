from itopy import itopy


class ItopConnection:
    _instance = None
    _url = None
    _version = None
    _user = None
    _password = None
    _search_keys = None

    @staticmethod
    def set_connection_params(search_keys, url, version, user, password):
        ItopConnection._search_keys = search_keys
        ItopConnection._url = url
        ItopConnection._version = version
        ItopConnection._user = user
        ItopConnection._password = password

    @staticmethod
    def connection():
        if ItopConnection._instance is None:
            if not all([ItopConnection._search_keys,ItopConnection._url, ItopConnection._version, ItopConnection._user, ItopConnection._password]):
                raise ValueError("Connection parameters not set")
            try:
                ItopConnection._instance = itopy.Api(ItopConnection._search_keys)
                ItopConnection._instance.connect(ItopConnection._url, ItopConnection._version, ItopConnection._user, ItopConnection._password)
            except Exception as e:
                raise ConnectionError("Failed to establish connection to iTop") from e
        return ItopConnection._instance
