import logging as log

# log message format
# %d{dd/MM/yyyy HH:mm:ss} %-5p [%c{1}:%L] %m%n
fmt = '%(asctime)s %(levelname)s %(lineno)s %(message)s'

# do some logging config
log.basicConfig(level=log.DEBUG, filename="mylog.log", format=fmt)

# create loggeing object
log = log.getLogger("dmitrii")

# write some messages
log.debug("zzzzz")
log.info("My info message!")
log.warn("Warn message")
log.error("Error message")
log.critical("Critical!!!")
