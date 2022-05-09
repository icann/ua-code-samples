# PHP Universal Compliance code samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for
PHP libraries.

The sources are in the `src` folder and are split in two use cases: HTTP and SMTP requests.

The following paragraphs lists the libraries that are tested, how to launch the PHP scripts to make
your own tests and some additional information as remaining issues.

## Preparing your environment

Install PHP, `php-intl` module and `composer` on your environment.
To install dependencies run:

    $ composer install

To get the newest libraries' versions you can do

    $ composer update

In `php.ini` uncomment both lines:

    extension=curl
    extension=intl

## HTTP Clients

### php-curl

To test the cURL extension for HTTP request:

    $ php -f src/Http/Samples/curl.php -- --url="http://<test-url>:80"


### Guzzle

To test the Guzzle HTTP client:

    $ php -f src/Http/Samples/guzzle.php -- --url="http://<test-url>:80"


### Symfony HttpClient

To test the Symfony HttpClient:

    $ php -f src/Http/Samples/symphonyHttpClient.php -- --url="http://<test-url>:80"


## SMTP Clients

SMTP examples are kept the most simple as possible and do not include authentication mechanisms.
You can use Mailhog SMTP server for your tests, main implementation does not support SMTPUTF8
extension but a [fork](https://github.com/dcormier/smtp) supports it.

## PHP Mailer

To test PHP Mailer:

    $ php -f src/Smtp/Samples/phpMailer.php -- --host=<smtp-sever> --port=25 --to="<email-address>"

PHP Mailer does not support SMTPUTF8 extension, therefore you won't be able to send EAI with
an email address containing unicode characters in local-part.

## Symfony Mailer

To test Symfony Mailer:

    $ php -f src/Smtp/Samples/symphonyMailer.php -- --host=<smtp-sever> --port=25 --to="<email-address>"
