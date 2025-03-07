FROM php:8.2-apache

# Instala Xdebug y las dependencias necesarias
RUN pecl uninstall xdebug || true\
    && pecl install -o -f xdebug \
    && docker-php-ext-enable xdebug
RUN docker-php-ext-install pdo pdo_mysql

# Instalar herramientas necesarias para Composer
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libzip-dev \
    && docker-php-ext-install zip


