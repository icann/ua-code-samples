const chai = require('chai');
const expect = chai.expect;
const { eai_smtp, SmtpClient } = require('../eai_smtp');
const sinon = require('sinon');
const nodemailer = require('nodemailer');
const {Results} = require("../results");
const punycode = require('punycode');

describe('eai_smtp', () => {
    it('should send email', async() => {
        const stub = {
            client: { sendMail: sinon.stub().resolves(undefined) },
            support_SMTPUTF8: sinon.stub().resolves(true)
        };
        const result = await eai_smtp('localpart@domain.tld', stub, new Results(), "");
        expect(result).to.deep.equal({
            error: false,
            messages: [
                `[nodemailer] Email has been sent successfully. See it <a href="undefined" target="_blank">here</a>!`
            ],
            value: "localpart@domain.tld"
        });
    });

    it('should return an error', async () => {
        const stub = {
            support_SMTPUTF8: sinon.stub().resolves(false)
        };
        const result = await eai_smtp('testé@domain.tld', stub, new Results(), "");
        expect(result).to.deep.equal({
            error: true,
            messages: [
                "[Note] SMTP server does not support SMTPUTF8"
            ],
            value: "testé@domain.tld"
        });
    });

    it('should not return an error on SMTPUTF8', async () => {
        const stub = {
            client: { sendMail: sinon.stub().resolves(undefined) },
            support_SMTPUTF8: sinon.stub().resolves(false)
        };
        const result = await eai_smtp('localpart@domain.tld', stub, new Results(), "");
        expect(result).to.deep.equal({
            error: false,
            messages: [
                `[nodemailer] Email has been sent successfully. See it <a href="undefined" target="_blank">here</a>!`
            ],
            value: "localpart@domain.tld"
        });
    });

    it('should return an error when email address does not contain a dot', async () => {
        const stub = {
            support_SMTPUTF8: sinon.stub().resolves(true)
        };
        const result = await eai_smtp('localpart@localhost', stub, new Results(), "");
        expect(result).to.deep.equal({
            error: true,
            messages: [
                "[Note] email does not contains the '@' AND '.' characters in this order"
            ],
            value: "localpart@localhost"
        });
    });

    it('should return an error when wrong email address', async () => {
        const stub = {
            client: { sendMail: sinon.stub().rejects(new Error('No recipients defined')) },
            support_SMTPUTF8: sinon.stub().resolves(true)
        };
        const result = await eai_smtp('te^st@test.com', stub, new Results(), "");
        expect(result).to.deep.equal({
            error: true,
            messages: [
                '[nodemailer] Failed to send email: No recipients defined'
            ],
            value: "te^st@test.com"
        });
    });

    // uncomment to make real tests with mailhog for instance:
    // it('realtest', async () => {
    //     const stub = new SmtpClient(nodemailer.createTransport({
    //         host: 'localhost',
    //         port: 1025,
    //         secure: false
    //     }))
    //     const result = await eai_smtp('missingdomain', stub, new Results());
    //     expect(result).to.deep.equal({
    //         error: true,
    //         messages: [
    //             '[nodemailer] Failed to send email: No recipients defined'
    //         ],
    //         value: "missingdomain"
    //     });
    // });
});