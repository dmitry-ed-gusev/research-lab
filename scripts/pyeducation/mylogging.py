import logging as log

# log message format (see https://docs.python.org/2/library/logging.html#logging.LogRecord)
# %d{dd/MM/yyyy HH:mm:ss} %-5p [%c{1}:%L] %m%n
fmt = '%(asctime)s %(levelname)-8s [%(filename)s:%(lineno)s] %(message)s'

# do some logging config
# log.basicConfig(level=log.DEBUG, filename="mylog.log", format=fmt)
log.basicConfig(level=log.DEBUG, format=fmt, datefmt='%d/%m/%Y %H:%M:%S')

# create loggeing object
log = log.getLogger(__name__)

# write some messages
log.debug("zzzzz")
log.info("My info message!")
log.warn("Warn message")
log.error("Error message")
log.critical("Critical!!!")
