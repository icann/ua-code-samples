# Python Universal Compliance code samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for
Python libraries.

The sources are in the `src` folder.

The following paragraphs lists the libraries that are used, how to launch the Python scripts to make
your own tests and some additional information as remaining issues.

## Preparing your environment

We suggest using pip to install your Python dependencies but other methods could be used.

Install Python and `pip`.

The next steps should be executed from the python root folder. 

Create a virtual environment:

    $ virtualenv venv

Source your virtual environment and install the required ua lib and its dependencies:

    $ source venv/bin/activate
    $ pip install .


## Python Backend for UA compliance demo website

### Setup

The Python backend is using [Flask framework](https://palletsprojects.com/p/flask/).
First install it in your environment with:

    $ pip install -r src/backend/requirements.txt

Then you can run the backend with:

    $ export FLASK_APP=src/backend/ua_samples
    $ export FLASK_ENV=development
    $ flask run

The backend can be configured by editing the `config.py` file.

### Create docker image

To create the backend docker image rune:

    $ docker build . -t ua-backend-python

To run the backend and listen to port 1234 do:

    $ docker run -d --name ua-py -p 1234:5000 ua-backend-python


## CLI tools

### IDNA related samples

#### idna

`idna` can be used to convert U-Label to A-Label and conversely.
The library offers different ways to do it, as explained in their documentation. One of these
ways is used in the `idna` code samples.

The `idna` code samples, converts an U-Label provided as input to A-Label or returns an error
if the domain is invalid.

As `idna` does not perform normalization, the Python standard `unicodedata` module is used for
normalization of the input domain to NFC.

To launch the `idna` sample do:

    $ (venv) python src/bin/idna/idna_main.py --domain <domain>


### EAI related samples

SMTP examples are kept the most simple as possible and do not include authentication mechanisms.
You can use Mailhog SMTP server for your tests, main implementation does not support SMTPUTF8
extension but a [fork](https://github.com/dcormier/smtp) supports it.


#### email_validator

The `email_validator` sample will check the validity of the email address provided as input or return
an error is the address is invalid.

The email_validator documentation explicitly says
"this library does NOT permit obsolete forms of email addresses"
therefore it does what we may call a "pragmatic" validation.

For instance, it does not accept '@' in quoted local-part, neither quoted local-part which should
be permitted.
RFC 5321 recommends against using quoted string to ensure email deliverance so this is
acceptable for most usages.

To launch the `email_validator` sample do:

    $ (venv) python src/bin/eai/email_validator_main.py --e <email-address>

### smtplib

The `smtplib` sample will sends an email to an email address provided as input, providing the relevant
flags if the email address contains UTF-8 characters.

`smtplib` performs a partial email validation and correctly handles EAI.
In order to better validate the email address (e.g. domain part IDNA 2008 compliance),
the `email_validator` is used for email validation.

In case of an SMTP server that does not support the `SMTPUTF8` option, an example of downgrading
is provided with the conversion of the domain part to ASCII with the `idna` library
(N.B. `email_validator` does this when validating, also using `idna` library so the validation
result should be used as much as possible to avoid doing this twice).

To launch the `smtplib` sample do:

    $ (venv) python src/bin/eai/smtplib_main.py --to="<email-address>" --host=<smtp-sever> --port=<port>
