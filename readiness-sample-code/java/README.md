# Java Universal Compliance code samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for
Java libraries.

## Preparing your environment

Make sure you have Java installed. Development has been made for Java >= 11.

## Java Backend for UA compliance demo website

### Setup

The Java backend is using [SparJava framework](https://sparkjava.com/). Sources are built with
Gradle.

In the `ua-sample-code-web` directory run:

    $ ./gradlew build install

Then the backend executable and libraries will be in `build/install/ua-sample-code-web`, it can be
launched with:

    $ ./build/install/ua-sample-code-web/bin/ua-sample-code-web

### Create docker image

To create the backend docker image rune:

    $ docker build . -t ua-backend-java

To run the backend and listen to port 1234 do:

    $ docker run -d --name ua-java -p 1234:4567 ua-backend-java

## CLI Tools

To build the CLI tools, in the `ua-sample-code-bin` directory run:

    $ ./gradlew build install

For each tool, the executable and libraries will be in `<tool-name>/build/install/<tool-name>` and
can be launched with:

    $ <tool-name>/build/install/<tool-name>/bin/<tool-name> <OPTIONS>

### IDNA related samples

#### guava

Guava validates domain name against a public suffix list. For UA compliance validation, another
library need to be used, the sample here uses ICU4j that is our most reliable way to check the IDNA
2008 compliance of a domain.

Therefore, we did not provide a CLI tool for Guava,

#### commons-validator

Commons-validator uses a static list of TLDs that is shipped with the sources, therefore depending
on the version of the library the TLD list can be very obsolete. Anyway, as the list of TLD is
regularly updated, any static list will be quickly obsolete.

To mitigate this, the sample downloads the list of TLDs from ICANN website and then update the
static list from the library. If used in a daemon, as the list cannot be updated once the validator
is instantiated, the list will become obsolete while the daemon is not started again.

To launch the `commons-validator` IDNA sample you can do:

    $ ./common-validator-idn-sample/build/install/common-validator-idn-sample/bin/common-validator-idn-sample --domain <domain>

#### icu

`icu` can be used to convert U-Label to A-Label and conversely.

The `icu` code samples, converts an U-Label provided as input to A-Label or returns an error
if the domain is invalid.

To launch the `icu` sample do:

    $ ./icu4j-sample/build/install/icu4j-sample/bin/icu4j-sample --domain <domain>


### EAI related samples

SMTP examples are kept the most simple as possible and do not include authentication mechanisms. You
can use Mailhog SMTP server for your tests, main implementation does not support SMTPUTF8 extension
but a [fork](https://github.com/dcormier/smtp) supports it.

#### commons-validator

The same problem as for commons-validator for domain name is encoutered here as the domain part is
validated with it.

To launch the `commons-validator` EAI sample you can do:

    $ ./common-validator-eai-sample/build/install/common-validator-eai-sample/bin/common-validator-eai-sample --email <email-address>

#### jakarta-mail

The `jakarta-mail` sample will sends an email to an email address provided as input, with the
relevant flags to support EAI.

`jakarta-mail` performs an email validation and correctly handles EAI but the domain name has to be
normalized and some valid domains are rejected.
Therefore, the sample first normalize the domain name, and if it is still considered invalid,
it converts it to A-Label with ICU in case it should be considered valid.
If ICU fails to convert, then the email address is considered invalid.

In case of an SMTP server that does not support the `SMTPUTF8` option, an example of downgrading
is provided with the conversion of the domain part to ASCII with the `ICU` library.
`jakarta-mail` does not return an error when the server does not support the `SMTPUTF8` options,
therefore it is the responsibility of the code sample to check it.

To launch the `jakarta-mail` sample do:

    $ ./jakarta-mail-sample/build/install/jakarta-mail-sample/bin/jakarta-mail-sample --to="<email-address>" --host=<smtp-sever> --port=<port>
