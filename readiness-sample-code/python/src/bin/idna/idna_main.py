#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
idna_main.py - UA Sample CLI for Python idna package
"""
import argparse
import logging
import sys

from ua.idna.convert_domain import convert_domain_to_alabel_with_idna

if __name__ == '__main__':
    logging.basicConfig()
    logging.getLogger().setLevel(logging.DEBUG)

    # Parse arguments
    arg_parser = argparse.ArgumentParser(description='idna UA Sample Code')
    arg_parser.add_argument('--domain', help='The domain name to convert in A-Label', required=True)

    args = arg_parser.parse_args()
    try:
        result = convert_domain_to_alabel_with_idna(args.domain)
        if result['error']:
            sys.exit(1)
    except Exception:
        sys.exit(1)

    sys.exit(0)
