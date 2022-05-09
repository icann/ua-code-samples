#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from email_validator import EmailNotValidError
from flask import current_app
from ua.eai.send_email import send_email_with_smtplib
from ua.eai.validate_email import validate_email_address_with_email_validator

from .base import FieldInfo, unknown_lib_result

EMAIL_CONTENT_TPL = """Dear {username},
Your registration is successful.
Your information:
 - username: {username}
 - email: {email}
 - website: {website}
Best regards,
The UA Sample team
"""


def eai_validate(email, lib):
    """
    Validate email address

    :param lib: The library to use for validation
    :param email: The email address to validate
    """
    if lib != 'email-validator':
        return unknown_lib_result(email, 'email', lib)

    try:
        result = validate_email_address_with_email_validator(email, dns_query=True)
        messages = [f"[email-validator] Email address {email} is valid",
                    f"[email-validator] Domain name {result.domain} exists"]
        if email != result.email:
            messages.append(f"[email-validator] Suggested normalized form is {result.email}")
        return FieldInfo(value=email,
                         messages=messages,
                         error=False)
    except EmailNotValidError as e:
        messages = [f"[email-validator] Email address {email} is invalid: {e}"]
        if email.startswith('"') or email.startswith("'"):
            messages.append('[Note] As per their documentation, email-validator does not accept '
                            'some valid yet obsolete cases such as quoted local-part')
        return FieldInfo(value=email,
                         messages=messages,
                         error=True)
    except Exception as e:
        return FieldInfo(value=email,
                         messages=[f"Failed to validate email field {email}: {e}"],
                         error=True)


def eai_smtp(email, lib, data):
    """
    Send an email with EAI support

    :param lib: The library to use to send the email
    :param email: The email recipient
    :param data: Form data
    """
    if lib != 'smtplib':
        return unknown_lib_result(email, 'email', lib)

    try:
        result = send_email_with_smtplib(email,
                                         current_app.config['SMTP_HOST'],
                                         current_app.config['SMTP_PORT'],
                                         subject='Registration successful',
                                         content=EMAIL_CONTENT_TPL.format(**data),
                                         validate=False)
        messages = []
        error = False
        if not result['error']:
            if result['email_downgraded']:
                messages.append(f"[smtplib] Server did not support SMTPUTF8 options. As local-part "
                                f"is plain ascii, the domain was converted to A-Label and the "
                                f"email sent to {result['email_downgraded']}")
            message = "[smtplib] Email has been sent successfully"
            if current_app.config.get('MAILHOG'):
                message += f", see it <a href=\"{current_app.config.get('MAILHOG')}\"" \
                           f" target=\"_blank\">here</a>!"
            messages.append(message)
        else:
            error = True
            messages.append(f"[smtplib] Cannot send email to {email}: {result['error']}")
        return FieldInfo(value=email, messages=messages, error=error)
    except Exception as e:
        return FieldInfo(value=email,
                         messages=[f"Failed to send email to {email}: {e}"],
                         error=True)
