#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from typing import TypedDict, List


class FieldInfo(TypedDict):
    value: str
    messages: List[str]
    error: bool


def no_lib_result(value):
    return FieldInfo(value=value,
                     messages=['Field has not been validated as no lib was specified'],
                     error=False)


def unknown_lib_result(value, field, lib):
    return FieldInfo(value=value,
                     messages=[f"Unknown library '{lib}' for {field} validation"],
                     error=False)


def process_website(website, libs, data):
    from .idna import idna_validate, idna_convert

    if not libs:
        return no_lib_result(website)

    result = {}
    validation_result = {}
    if libs.get('validate'):
        validation_result = idna_validate(website, libs.get('validate'))
        result = validation_result
    if not validation_result.get('error') and libs.get('convert'):
        messages = validation_result.get('messages', [])
        result = idna_convert(website, libs.get('convert'))
        messages.extend(result['messages'])
        result['messages'] = messages

    return result


def process_email(email, libs, data):
    from .eai import eai_validate, eai_smtp

    if not libs:
        return no_lib_result(email)

    result = {}
    if libs.get('validate'):
        result = eai_validate(email, libs.get('validate'))
    if not result.get('error') and libs.get('smtp'):
        messages = result.get('messages', [])
        result = eai_smtp(email, libs.get('smtp'), data)
        messages.extend(result['messages'])
        result['messages'] = messages

    return result
