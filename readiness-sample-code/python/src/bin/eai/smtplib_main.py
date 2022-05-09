#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
smtplib_main.py - UA Sample CLI for Python smtplib module
"""
import argparse
import logging
import sys

from ua.eai.send_email import send_email_with_smtplib

if __name__ == '__main__':
    logging.basicConfig()
    logging.getLogger().setLevel(logging.DEBUG)

    # Parse arguments
    arg_parser = argparse.ArgumentParser(description='smtplib UA Sample Code')
    arg_parser.add_argument('--to', help='The Email recipient', required=True)
    arg_parser.add_argument('--host', help='The SMTP server address',
                            required=False, default="localhost")
    arg_parser.add_argument('--port', help='The SMTP server port',
                            type=int, required=False, default=25)

    args = arg_parser.parse_args()
    try:
        result = send_email_with_smtplib(args.to, args.host, args.port)
        if result['error']:
            sys.exit(1)
    except Exception:
        sys.exit(1)

    sys.exit(0)
