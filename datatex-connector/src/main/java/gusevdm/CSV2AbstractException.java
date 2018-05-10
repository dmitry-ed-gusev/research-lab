package gusevdm;

/** Abstract export exception to be thrown in predictable error scenarios. */

class CSV2AbstractException extends RuntimeException {
    CSV2AbstractException(String message) {
        super(message);
    }

    CSV2AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    CSV2AbstractException(Throwable cause) {
        super(cause);
    }
}
