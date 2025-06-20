#!/usr/bin/env node

const express = require('express');
const path = require('path');
const Autolinker = require('autolinker');
const {engine} = require('express-handlebars');
const Handlebars = require("handlebars");
const mjml = require('mjml');
const nodemailer = require('nodemailer');
const imapflow = require("imapflow");
const SimpleParser = require('mailparser');

const app = express();
const PORT = 3000;

app.use(express.json());
app.use(express.urlencoded({extended: false}));


app.engine('hbs', engine());
app.set('view engine', 'hbs');
app.set('views', path.join(__dirname, 'views'));

app.get('/', (req, res) => {
    res.send('Test JavaScript Libraries 2025');
});

app.post('/api/linkify', (req, res) => {
    const text = req.body.text || '';
    res.send(getAutolinkText(text));
});

app.post('/api/html', (req, res) => {
    const username = req.body.username || '';
    const contact = req.body.contact || '';
    var context = {layout: false, username: username, contact: contact};
    res.render('main', context);
});


app.post('/api/email/fetch', async (req, res) => {
    const sender = req.body.sender || '';
    res.json(await getEmail(sender));
});


app.post('/api/email/create', (req, res) => {
    const text = req.body.text || '';
    res.send(createEmailBody(text));
});

app.post('/api/email/send', async (req, res) => {
    const sender = req.body.sender || '';
    let results = null
    let message = null

    const transporter = nodemailer.createTransport({host: "localhost", port: 3025, secure: false});
    try {
        await transporter.sendMail({
            from: sender,
            to: "test@localhost",
            subject: "Test UA for Sending Emails",
            text: createEmailBody(sender)
        });
        results = true
        message = "Email has been sent successfully.";
    } catch (err) {
        results = false;
        message = err.message;
    }

    res.send({result: results, message: message});
});


function getAutolinkText(text) {
    const autolinker = new Autolinker({stripPrefix: false, stripTrailingSlash: false});
    return autolinker.link(text);
}

Handlebars.registerHelper('contact', function () {
    const formattedContact = getAutolinkText(this.contact);
    if (formattedContact.includes('mailto:')) {
        return "this is my email: " + formattedContact;
    } else {
        return "this is my url: " + formattedContact;
    }
})

function createEmailBody(text) {
    const formattedText = getAutolinkText(text)
    const mjmlTemplate = `
    <mjml>
      <mj-body>
        <mj-section>
          <mj-column>
			<mj-text>
              JavaScript Libraries UA Test 2025
            </mj-text>
          </mj-column>
        </mj-section>
        <mj-section>
          <mj-column>
            <mj-divider border-color="#F45E43"></mj-divider>
            <mj-text>
              You may click on ${formattedText} anytime.
            </mj-text>
          </mj-column>
        </mj-section>
      </mj-body>
    </mjml>
`
    const {html, errors} = mjml(mjmlTemplate);
    return html
}

async function getEmail(sender) {
    let result = null
    const client = new imapflow.ImapFlow({
        host: 'localhost',
        port: 3143,
        secure: false,
        auth: {user: 'test@localhost', pass: 'password'}
    });

    await client.connect();
    await client.mailboxOpen('INBOX');
    let lock = await client.getMailboxLock('INBOX');
    try {
        const uids = await client.search({from: sender});
        if (uids.length === 0) {
            result = `No message found`
        } else {
            const latestUid = uids[uids.length - 1]
            let message = await client.fetchOne(latestUid, {uid: true, source: true});
            let parsed_message = await SimpleParser.simpleParser(message.source)
            result = {
                from: parsed_message.from.text,
                to: parsed_message.to.text,
                subject: parsed_message.subject,
                body: parsed_message.text
            }
        }
    } catch (err) {
        result = {message: `Failed to search for messages: ${err.toString()}`}
    } finally {
        lock.release();
    }
    await client.logout();
    return result
}

app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});
