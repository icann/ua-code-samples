const chai = require('chai');
const {Results} = require("../results");
const assert = chai.assert;
const expect = chai.expect;

const { idna_convert } = require('../idna_convert');

describe('website_validator', () => {
    it('should return success', () => {
        expect(idna_convert('http://test.com', new Results())).to.deep.equal({
            error: false,
            messages: [
                "[nodejs] successfully parsed the domain test.com from URL http://test.com",
                "[idna-uts46] http://test.com is a valid URL"
            ],
            value: "http://test.com"
        });
    });

    it('should return url error', () => {
        expect(idna_convert('http://e+.test', new Results())).to.deep.equal({
            error: true,
            messages: [
                "[nodejs] successfully parsed the domain e+.test from URL http://e+.test",
                "[idna-uts46] URL http://e+.test is invalid: Illegal char +"
            ],
            value: "http://e+.test"
        });
    });

    it('should convert U-LABEL', () => {
        expect(idna_convert('http://www.fußball.top', new Results())).to.deep.equal({
            error: false,
            messages: [
                "[nodejs] successfully parsed the domain www.fußball.top from URL http://www.fußball.top",
                "[idna-uts46] The website domain name has been transformed to A-Label to ensure IDNA 2008 compliant queries: <a href=www.xn--fuball-cta.top>www.xn--fuball-cta.top</a>",
                "[idna-uts46] http://www.fußball.top is a valid URL"
            ],
            value: "http://www.xn--fuball-cta.top"
        });
    });

    it('should convert normalize the U-LABEL', () => {
        expect(idna_convert('http://test\u0065\u0301.com', new Results())).to.deep.equal({
            error: false,
            messages: [
                "[nodejs] successfully parsed the domain testé.com from URL http://test\u0065\u0301.com",
                "[nodejs] Website domain has been normalized in NFC form: testé.com",
                "[idna-uts46] The website domain name has been transformed to A-Label to ensure IDNA 2008 compliant queries: <a href=xn--test-epa.com>xn--test-epa.com</a>",
                "[idna-uts46] http://test\u0065\u0301.com is a valid URL"
            ],
            value: "http://xn--test-epa.com"
        });
    });
});