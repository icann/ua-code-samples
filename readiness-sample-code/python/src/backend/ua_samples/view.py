#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Blueprint, request, jsonify

from .processing.base import process_website, process_email

bp = Blueprint('ua', __name__, url_prefix='/ua')


@bp.route('/libs')
def libs_view():
    return jsonify({
        'idna': {
            'convert': ['idna'],
            'validate': None
        },
        'eai': {
            'smtp': ['smtplib'],
            'validate': ['email-validator']
        }
    })


@bp.route('/register', methods=('POST',))
def register_view():
    """
    Registration form example to demonstrate URL and email handling in an UA compliant way

    The view is written in a way that different libraries can be selected for the various steps
    """
    data = request.get_json()
    if not data:
        return jsonify({'error': 'Empty body'}), 400
    username = data.get('username')
    libs = data.get('libs')
    email = data.get("email")
    website = data.get("website")

    missing_fields = []
    if not username:
        missing_fields.append('username')
    if not libs:
        missing_fields.append('libs')
    if not email:
        missing_fields.append('email')
    if not website:
        missing_fields.append('website')
    if missing_fields:
        return jsonify(
            {'error': f"The following fields are required: {', '.join(missing_fields)}"}), 400

    # Validate website field and perform IDNA conversion
    result_website = process_website(website, libs.get('idna', {}), data)

    # Validate email field and send a welcome email
    result_email = process_email(email, libs.get('eai', {}), data)

    return jsonify({
        'field': {
            'email': result_email,
            'website': result_website
        }
    })
