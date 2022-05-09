#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
send_email.py - UA Samples for Python email sending
"""
import logging
import smtplib
from email.message import EmailMessage
from typing import TypedDict

import idna
from ua.eai.validate_email import validate_email_address_with_email_validator
from ua.idna.convert_domain import convert_domain_to_alabel_with_idna

logger = logging.getLogger(__name__)


class SmtpResult(TypedDict):
    to: str
    valid: bool
    email_downgraded: str
    error: str


def send_email_with_smtplib(to, host, port,
    sender='ua@test.org',
    subject='UA Compliant Email',
    content='This email was sent to test UA compliance.',
    validate=True) -> SmtpResult:
    """
    Validate recipient and send an email which supports EAI as 'To' recipient.

    :param to: The email recipient
    :param sender: The email from field
    :param host: The SMTP server address
    :param port: The SMTP server port
    :param validate: Whether email should be validated
    """
    if validate:
        # smtplib does perform a basic validation but does not check domain validity for
        # instance while email_validator does it with the idna library
        # (and can also perform DNS query)
        try:
            validate_email_address_with_email_validator(to)
        except Exception as e:
            return SmtpResult(to=to, valid=False, email_downgraded='', error=str(e))

    # send the email
    result = SmtpResult(to=to, valid=True, email_downgraded='', error='')
    try:
        _base_send_email_with_smtplib(to, host, port, sender, subject, content)
    except smtplib.SMTPNotSupportedError as e:
        # transform domain in A-label in case local part only contains ASCII characters
        logger.info(f"Try to convert email address '{to}' to ASCII")
        # email_to_ascii is provided to demonstrate how this could be achieved but
        # validate_email_address returns a validated objects with attributes such as the
        # address local-part, domain and the domain converted to A-Label, so it would be more
        # appropriate to use it
        to = email_to_ascii(to)
        if not to:
            result['error'] = str(e)
            return result

        result['email_downgraded'] = to
        _base_send_email_with_smtplib(to, host, port, sender, subject, content)

    return result


def _base_send_email_with_smtplib(to, host, port, sender, subject, content):
    """
    Send an email which supports EAI as 'To' recipient.

    :param to: The email recipient
    :param host: The SMTP server address
    :param port: The SMTP server port
    """
    try:
        # create the smtp instance
        smtp = smtplib.SMTP(host, port)
        # set debug level to true if you want to see all messages sent to and received from
        # the server
        smtp.set_debuglevel(False)
        # build the email message
        msg = EmailMessage()
        msg.set_content(content)
        msg['Subject'] = subject
        msg['From'] = sender

        # send the email
        # Note: when using send_message, the UTF-8 specific flags are automatically set while using
        # sendmail would need then to be specified, e.g.:
        #     msg = MIMEText('This email was sent to test UA compliance.', 'plain')
        #     msg['Subject'] = 'UA Compliant Email'
        #     sender = 'ua@test.org'
        #     msg['From'] = 'ua@test.org'
        #     server.sendmail(sender, to, msg.as_string(), ["SMTPUTF8"])
        # N.B. send_message also sets the charset to utf-8 and 8bitmime options
        smtp.send_message(msg, sender, to)
        smtp.quit()
        logger.info(f"Email sent to '{to}'")
    except smtplib.SMTPNotSupportedError as e:
        # The server does not support the SMTPUTF8 option, you may want to perform downgrading
        logger.warning(f"The SMTP server {host}:{port} does not support the SMTPUTF8 option")
        raise
    except Exception as e:
        # There was a failure when sending the email
        logger.error(f"ERROR: {e}")
        raise


def email_to_ascii(email):
    """
    Transform the email address to ASCII if possible (i.e. only if local part is ASCII)

    :param email: The email address
    :return: The domain in A-Label form
    """
    # Split the local part and domain
    local_part, domain = email.rsplit('@', 1)
    if not local_part.isascii():
        logger.error(f"Email local part '{email}' contains Unicode characters, "
                     f"cannot transform it to full ASCII")
        return

    result = convert_domain_to_alabel_with_idna(domain)
    if result['error']:
        logger.error(f"Email domain '{domain}' is not valid against IDNA 2008: {result['error']}")
        return
    return '@'.join((local_part, result['converted']))
