#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
convert_domain.py - UA Samples for Python domain conversion
"""
import logging
import unicodedata
from typing import TypedDict

import idna

logger = logging.getLogger(__name__)


class ConversionResult(TypedDict):
    initial: str
    converted: str
    normalized: str
    error: str


def convert_domain_to_alabel_with_idna(domain) -> ConversionResult:
    """
    Convert a domain name from U-Label to A-Label

    :param domain: The domain name
    :return the conversion result
    """
    result = ConversionResult(initial=domain, normalized='', converted='', error='')
    try:
        # First normalize the domain in NFC form as specified in RFC 5891 §4.1
        domain_normalized = unicodedata.normalize('NFC', domain)
        if domain_normalized != domain:
            result['normalized'] = domain_normalized
        # Encode the domain name in A-Label to be used by DNS, HTTP, etc. client.
        # IDNA package is fully IDNA 2008 compliant
        domain_a_label = idna.encode(domain_normalized).decode('ascii')
        logger.info(f"Domain '{domain}' converted in A-Label is '{domain_a_label}'")
        result['converted'] = domain_a_label
    except idna.IDNAError as e:
        # The label is invalid as per IDNA 2008
        logger.error(f"Domain '{domain}' is invalid: {e}")
        result['error'] = str(e)
    except Exception as e:
        # Unexpected exception
        logger.error(f"ERROR: {e}")
        # as this does not seem to be an IDNA related exception, always raise
        raise

    return result
