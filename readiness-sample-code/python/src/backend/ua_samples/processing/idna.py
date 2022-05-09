#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from urllib.parse import urlparse

from ua.idna.convert_domain import convert_domain_to_alabel_with_idna

from .base import FieldInfo, unknown_lib_result


def idna_validate(lib, website):
    return unknown_lib_result(website, 'website', lib)


def idna_convert(website, lib):
    """
    Convert domain from U-Label to A-Label when appropriate on a website URL

    :param lib: The library to use for conversion
    :param website: The website URL to convert
    """
    if lib != 'idna':
        return unknown_lib_result(website, 'website', lib)

    try:
        result = convert_domain(website, convert_domain_to_alabel_with_idna)
        messages = []
        error = False
        if not result['error']:
            messages.append(f"[idna] {website} is a valid URL")
            if result['normalized']:
                messages.append(f"[idna] Website domain has been normalized in NFC form: "
                                f"{result['normalized']}")
            if website != result['converted_website']:
                messages.append("[idna] The website domain name has been transformed to "
                                "A-Label to ensure IDNA 2008 compliant queries: "
                                f"<a href=\"{result['converted_website']}\">"
                                f"{result['converted_website']}"
                                f"</a>")
        else:
            error = True
            messages.append(f"[idna] URL {website} is invalid: {result['error']}")
        return FieldInfo(value=website, messages=messages, error=error)
    except Exception as e:
        return FieldInfo(value=website,
                         messages=[f"Failed to convert website field {website}: {e}"],
                         error=True)


def convert_domain(website, converter):
    if '//' not in website:
        # urlparse will only recognize netloc if it is introduced by //,
        # see https://docs.python.org/3/library/urllib.parse.html#module-urllib.parse
        website = '//' + website

    # parse the URL to extract the domain
    parsed_url = urlparse(website)

    # convert the domain to A-Label
    info = converter(parsed_url.hostname)
    if info['error']:
        return info

    # domain cannot be updated in parsed_url, the cleanest way is to rebuild netloc then update it
    netloc_converted = info['converted']
    creds = parsed_url.username
    if parsed_url.port:
        netloc_converted += f':{parsed_url.port}'
    if parsed_url.password:
        creds = f'{creds}:{parsed_url.password}'
    if creds:
        creds += '@'
        netloc_converted = f'{creds}{netloc_converted}'

    parsed_url = parsed_url._replace(netloc=netloc_converted)
    info['converted_website'] = parsed_url.geturl()
    return info
