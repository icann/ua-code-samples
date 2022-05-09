# Javascript Universal Compliance code samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for javascript libraries.

## Preparing your environment

Make sure you have NodeJS installed. Development has been made for NodeJS >= 17.5.

## Javascript Backend for UA compliance demo website

The backend use the web [Express framework](https://expressjs.com/).

### Setup

Install dependencies with npm:

    /javascript $ npm install

Then start the backend typing:

    npm start

### Create docker image

To create the backend docker image rune:

    $ docker build . -t ua-backend-javascript

To run the backend and listen to port 3000 do:

    $ docker run -d --name ua-javascript -p 3000:3000 ua-backend-javascript

### IDNA related samples

#### idna-uts46 (idna_convert.js)

Implements IDNA2008 protocol (without BIDI & contextual rules) for converting domain between A-LABEL & U-LABEL. The
option transitional: false allows to discard IDNA2003 processing and use only IDNA2008.

### EAI related samples

SMTP examples are kept the most simple as possible and do not include authentication mechanisms. You can use Mailhog
SMTP server for your tests, main implementation does not support SMTPUTF8 extension but
a [fork](https://github.com/dcormier/smtp) supports it.

#### validator.js (eai-validate.js)

validator.js provides a isEmail method to check validity of the email address (internationalized or not).

#### nodemailer (eai-smtp.js)

nodemailer transforms automatically the domain part into an A-LABEL if it is a U-LABEL (using punnycode.js). It also checks the SMTPUTF8
extension internally but does not provide a public API for it. The code sample shows how to retrieve it nonetheless
since nodemailer does not raise an exception when the smtp server does not support the extension,
see [the bug report here](https://github.com/nodemailer/nodemailer/issues/1378)

### Testing

Once the server is started (and a fake SMTP server like mailhog is listening on port 1025), you can issue HTTP queries:

    curl -X POST localhost:4567/ua/register -H 'Content-Type: application/json' -d '{"email":"test@test.com","username":"test", "libs": {"idna": {"convert": "idna"}, "eai": {"validate": "email-validator", "smtp": "smtplib"}}, "website": "http://test>.com"}'

This should print:

    {
      "field": {
        "website": {
          "messages": [
            "[nodejs] parsing URL http://test>.com failed: Invalid URL"
          ],
          "error": true,
          "value": "http://test>.com"
        },
        "email": {
          "messages": [
            "[validator] Email address test@test.com is valid (no DNS verifications)",
            "[nodemailer] Email has been sent successfully. See it <a href="http://localhost:8025" target="_blank">here</a>!"
          ],
          "error": true,
          "value": "test@test.com"
        }
      }
    }

