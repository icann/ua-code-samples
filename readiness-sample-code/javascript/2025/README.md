# JavaScript Universal Compliance Code Samples

The following folder contains code samples to provide guidelines for Universal Acceptance (UA) for JavaScript libraries.

## Preparing The Environment

Make sure you have NodeJS installed. Development has been made for NodeJS >= 24.

## JavaScript Libraries for UA Compliance Demo Website

The backend uses the Express web framework.

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
                <p>I am Alexandre, this is my email: <a href="mailto:atdesrochers@cofomo.com" target="_blank" rel="noopener noreferrer">atdesrochers@cofomo.com</a>.</p>
            </div>
        </body>
    </html>

#### Accessing Inbound Emails on a Mail Server & Processing Emails

    curl -X POST localhost:3000/api/email/fetch -H 'Content-Type: application/json' -d '{"sender": "atdesrochers@cofomo.com"}'

This should print:

    {
        "from": "atdesrochers@cofomo.com",
        "to": "test@localhost",
        "subject": "Test UA for Sending Emails",
        "body": <The email content>
    }

#### Composing Emails for Sending

    curl -X POST localhost:3000/api/email/create -H 'Content-Type: application/json' -d '{"text": "atdesrochers@cofomo.com"}'

This should output the HTML content for the email.

#### Sending Emails

    curl -X POST localhost:3000/api/email/send -H 'Content-Type: application/json' -d '{"sender": "atdesrochers@cofomo.com"}'

This should print:

    Email has been sent successfully.
