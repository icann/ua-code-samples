const chai = require('chai');
const {Results} = require("../results");
const {getAutolinkText} = require("../app")

const expect = chai.expect;


describe('linkification', () => {
    it('should make an email clickable', async () => {
        expect(getAutolinkText('This is a clickable email: localpart@domain.com.'), new Results()).to.deep.equal({
            error: false,
            messages: [
                'This is a clickable email: <a href="mailto:localpart@domain.com" target="_blank" rel="noopener noreferrer">localpart@domain.com</a>.'
            ],
            value: "localpart@domain.com"
        });
    });

    it('should make an url clickable', async () => {
        expect(getAutolinkText('This is a clickable link: google.com.'), new Results()).to.deep.equal({
            error: false,
            messages: [
                'This is a clickable link: <a href="http://google.com" target="_blank" rel="noopener noreferrer">google.com</a>.'
            ],
            value: "google.com"
        });
    });

    it('should not make an invalid email clickable', async () => {
        expect(getAutolinkText('This is not a clickable email: local@part@domain@tld.'), new Results()).to.deep.equal({
            error: true,
            messages: [
                'This is not a clickable email: local@part@domain@tld.'
            ],
            value: "local@part@domain@com"
        });
    });

    it('should not make an invalid url clickable', async () => {
        expect(getAutolinkText('This is a clickable link: www.some.nonsense.invalid.'), new Results()).to.deep.equal({
            error: false,
            messages: [
                'This is a clickable link: www.some.nonsense.invalid.'
            ],
            value: "www.some.nonsense.invalid"
        });
    });
});
