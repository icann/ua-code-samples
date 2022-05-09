#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
validate_email.py - UA Samples for Python email validation
"""
import logging

from email_validator import validate_email, EmailNotValidError

logger = logging.getLogger(__name__)


def validate_email_address_with_email_validator(address, dns_query=False):
    """
    Validate an email address with email-validator package

    :param address: The email address to validate
    :param dns_query: Whether the library should make a DNS query to validate domain
    :return: The validation result
    """
    try:
        # Validate the email address.
        # If you want to perform DNS resolution of the domain, set dns_query to True,
        # this will increase the time used for the validation of the address
        validated = validate_email(address, check_deliverability=dns_query)
        logger.info(f"'{address}' is a valid email address")
        # Validated object contains useful attributes on the email address
        return validated
    except EmailNotValidError as e:
        # The email address is invalid
        logger.error(f"'{address}' is not a valid email address: {e}")
        raise
    except Exception as e:
        # Unexpected exception
        logger.error(f"ERROR: {e}")
        raise
