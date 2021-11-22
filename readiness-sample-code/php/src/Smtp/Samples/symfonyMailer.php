<?php

namespace UAReadiness\Smtp\Samples;

require_once __DIR__ . '/../../../vendor/autoload.php';

use Symfony\Component\Mailer\Mailer;
use Symfony\Component\Mailer\Transport\Smtp\EsmtpTransport;
use Symfony\Component\Mime\Email;
use Throwable;
use UAReadiness\Smtp\UAEmail;

class SymfonyMailerUAReadiness extends UAEmail
{
    protected function sendEmail(): int
    {
        // Symfony uses emailValidator in Symfony\Component\Mime\Address but with MessageIDValidation
        // that is too permissive compared to RFCValidation (it accepts @ in non-quoted local-part)
        if(!$this->validateEmailAddress()) {
            return 1;
        }

        // Symfony converts domain in A-label with Symfony\Component\Mime\Encoder\IdnAddressEncoder
        // but it does not use the right flags to get IDN 2008 compliance therefore we convert it
        // ourselves
        $converted_to = $this->convertEmailDomain(false);

        // Symfony accepts non-ASCII local part (from version 5.1) but does not implement SMTPUTF8
        // extension, therefore any email address with non-ASCII local-part should lead to a server
        // error

        $transport = new EsmtpTransport($this->host, $this->port);
        $mailer = new Mailer($transport);

        try {
            $email = (new Email())
                ->from($this->from)
                ->to($converted_to)
                ->subject($this->subject)
                ->text($this->body);
            $mailer->send($email);
        } catch (Throwable $e) {
            echo "ERROR: " . $e->getMessage() . "\n";
            return 1;
        }
        return 0;
    }
}


$email = new SymfonyMailerUAReadiness();
$email->send();