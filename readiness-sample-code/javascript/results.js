class Results {
    constructor() {
        this.messages = [];
        this.error = true;
        this.value = undefined;
    }

    addResult(error, message, value) {
        this.messages.push(message);
        this.error = error;
        if (value.endsWith('/')) {
            this.value = value.slice(0, value.lastIndexOf('/'));
        } else {
            this.value = value;
        }
        return this;
    }
}

module.exports = { Results };