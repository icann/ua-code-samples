const validator = require('validator');

function eai_validate(email, results) {
    if (!validator.isEmail(email)) {
        return results.addResult(true, `[validator] Email address ${email} is invalid`, email);
    }
    return results.addResult(false, `[validator] Email address ${email} is valid (no DNS verifications)`, email);
}

module.exports = { eai_validate };