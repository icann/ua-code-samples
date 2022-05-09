#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os

# SMTP server hostname
SMTP_HOST = os.environ.get('SMTP_HOST', 'localhost')
# SMTP server port
SMTP_PORT = os.environ.get('SMTP_PORT', 1025)
# Mailhog configuration (set to False to disable)
MAILHOG = os.environ.get('MAILHOG', f'http://{SMTP_HOST}:8025')