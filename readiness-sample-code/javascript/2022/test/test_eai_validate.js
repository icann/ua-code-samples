const chai = require('chai');
const {Results} = require("../results");
const assert = chai.assert;
const expect = chai.expect;
const eai_validate = require('../eai_validate').eai_validate;

describe('eai_validate', () => {
    it('should return success', () => {
        expect(eai_validate('localpart@domain.tld', new Results())).to.deep.equal({
            error: false,
            messages: [
                "[validator] Email address localpart@domain.tld is valid (no DNS verifications)"
            ],
            value: "localpart@domain.tld"
        });
    });

    it('should return an error', () => {
        expect(eai_validate('two@at@sign.tld', new Results())).to.deep.equal({
            error: true,
            messages: [
                "[validator] Email address two@at@sign.tld is invalid"
            ],
            value: "two@at@sign.tld"
        });
    });
});