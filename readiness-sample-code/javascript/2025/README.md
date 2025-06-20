# JavaScript Universal Compliance Code Samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for JavaScript libraries.

## Preparing The Environment

Make sure you have NodeJS installed. Development has been made for NodeJS >= 24.

## JavaScript Backend for UA Compliance Demo Website

todo

### Setup

Install dependencies with npm:

    /javascript/2025 $ npm install

Then start the backend typing:

    npm start

### Create docker image

To create the backend docker image rune:

    $ docker build . -t ua-javascript-libraries

To run the backend and listen to port 3000 do:

    $ docker run -d --name ua-javascript-libraries -p 3000:3000 ua-javascript-libraries

### IDNA related samples

TODO

### Testing

Once the server is started (and a fake SMTP server like mailhog is listening on port 1025), you can issue HTTP queries:

#### Category A - Linkification

    curl -X POST localhost:3000/api/linkify -H 'Content-Type: application/json' -d '{"text": "This is an email: atdesrochers@cofomo.com"}'

This should print:

    This is an email: <a href="mailto:atdesrochers@cofomo.com" target="_blank" rel="noopener noreferrer">atdesrochers@cofomo.com</a>

#### Generating HTML from Templating Languages

    curl -X POST localhost:3000/api/html -H 'Content-Type: application/json' -d '{"username":"Alexandre", "contact":"atdesrochers@cofomo.com"}'

This should print:

    <html>
        <head>
            <title>JavaScript Libraries UA Test 2025</title>
        </head>
    
        <body>
            <div>
                <p>I am Alex, this is my email: <a href="mailto:atdesrochers@cofomo.com" target="_blank" rel="noopener noreferrer">atdesrochers@cofomo.com</a>.</p>
            </div>
        </body>
    </html>

#### Accessing Inbound Emails on a Mail Server

    TODO

This should print:

    TODO

#### Processing Emails

    TODO

This should print:

    TODO

#### Composing Emails for Sending

    TODO

This should print:

    TODO

#### Sending Emails

    TODO

This should print:

    TODO
