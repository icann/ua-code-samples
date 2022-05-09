#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
email_validator_main.py - UA Sample CLI for Python email-validator package
"""
import argparse
import logging
import sys

from ua.eai.validate_email import validate_email_address_with_email_validator

if __name__ == '__main__':
    logging.basicConfig()
    logging.getLogger().setLevel(logging.DEBUG)

    # Parse arguments
    arg_parser = argparse.ArgumentParser(description='email-validator UA Sample Code')
    arg_parser.add_argument('--email-address', '-e', help='The Email address to validate',
                            required=True)

    args = arg_parser.parse_args()
    try:
        validate_email_address_with_email_validator(args.email_address)
    except Exception:
        sys.exit(1)

    sys.exit(0)
