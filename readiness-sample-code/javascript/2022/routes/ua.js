const express = require('express');
const router = express.Router();
const uts46 = require('idna-uts46');
const {Results} = require("../results");
const {idna_convert} = require("../idna_convert");
const {eai_validate} = require("../eai_validate");
const {eai_smtp, SmtpClient} = require("../eai_smtp");
const nodemailer = require("nodemailer");

router.get('/libs', (req, res) => {
    res.send({
        eai: {
            smtp: [
                "nodemailer"
            ],
            validate: [
                "validator"
            ]
        },
        idna: {
            convert: [
                "idna-uts46"
            ],
            validate: null
        }
    })
});

router.post('/register', async (req, res) => {
    const libs = req.body['libs'];
    const email = req.body['email'];
    const website = req.body['website'];
    const username = req.body['username'];

    const websiteResults = new Results();
    const emailResults = new Results();
    if ('idna' in libs) {
        if ('convert' in libs['idna']) {
            idna_convert(website, websiteResults);
        }
    }

    if ('eai' in libs) {
        if ('validate' in libs['eai']) {
            eai_validate(email, emailResults);
        }
        const dotenv = require('dotenv');
        dotenv.config();
        if ('smtp' in libs['eai']) {
            const msg = `Dear ${email},
Your registration is successful.
Your information:
 - username: ${username}
 - email: ${email}
 - website: ${website}
Best regards,
The UA Sample team`;
            await eai_smtp(email, new SmtpClient(nodemailer.createTransport({
                host: process.env.SMTP_HOST,
                port: process.env.SMTP_PORT,
                secure: false
            })), emailResults, msg);
        }
    }

    res.send({
        field: {
            website: websiteResults,
            email: emailResults
        }
    });
});

module.exports = router;
