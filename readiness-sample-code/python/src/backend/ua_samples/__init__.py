#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import Flask
from flask_cors import CORS
from . import view


def create_app():
    # create and configure the app
    app = Flask(__name__)
    CORS(app)  # This will enable CORS for all routes

    app.config.from_pyfile('config.py', silent=True)

    app.register_blueprint(view.bp)

    return app
