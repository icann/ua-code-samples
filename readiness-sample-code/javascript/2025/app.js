#!/usr/bin/env node

const express = require('express');
const path = require('path');
const Autolinker = require('autolinker');
const {engine} = require('express-handlebars');
const Handlebars = require("handlebars");

const app = express();
const PORT = 3000;

app.use(express.json());
app.use(express.urlencoded({extended: false}));


app.engine('hbs', engine());
app.set('view engine', 'hbs');
app.set('views', path.join(__dirname, 'views'));

app.post('/', (req, res) => {
    res.json({message: 'Test JavaScript Libraries 2025'});
});

app.post('/api/linkify', (req, res) => {
    const text = req.body.text || '';
    const autolinker = new Autolinker({stripPrefix: false, stripTrailingSlash: false});
    const result = autolinker.link(text);
    res.send(result);
});

app.post('/api/html', (req, res) => {
    const username = req.body.username || '';
    const contact = req.body.contact || '';
    var context = {layout: false, username: username, contact: contact};
    res.render('main', context);
});


Handlebars.registerHelper('contact', function () {
    const autolinker = new Autolinker({stripPrefix: false, stripTrailingSlash: false});
    const formattedContact = autolinker.link(this.contact);
    if (formattedContact.includes('mailto:')) {
        return "this is my email: " + formattedContact;
    } else {
        return "this is my url: " + formattedContact;
    }
})


app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});

