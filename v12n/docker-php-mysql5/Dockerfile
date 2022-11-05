###############################################################################
#
#     Docker build file for local PHP dev/test farm - building customized
#     image of PHP-FPM. Based on [php:7-fpm] image. Added extensions:
#       - mysqli
#
#     Created:  Dmitrii Gusev, 09.04.2019
#     Modified:
#
###############################################################################

FROM php:7-fpm
RUN apt-get update && docker-php-ext-install mysqli
