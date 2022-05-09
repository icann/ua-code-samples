// importing the dependencies
var express = require('express');
var path = require('path');
var logger = require('morgan');
var cors = require('cors');

// set up routes
var uaRouter = require('./routes/ua');

// defining the Express app
var app = express();

// adding morgan to log HTTP requests
app.use(logger('combined'));
// parse JSON bodies into JS objects
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
// enabling CORS for all requests
app.use(cors());


app.use('/ua', uaRouter);

module.exports = app;

// // handle unknown routes
// app.use((req, res) => {
//     res.status(404);
//     // respond with json
//     if (req.accepts('json')) {
//         res.json({ error: 'Not found' });
//         return;
//     }
//
//     // default to plain-text. send()
//     res.type('txt').send('Not found');
// });