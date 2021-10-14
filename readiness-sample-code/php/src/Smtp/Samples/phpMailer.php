<?php

namespace UAReadiness\Smtp\Samples;

require_once __DIR__ . '/../../../vendor/autoload.php';

use Exception;
use PHPMailer\PHPMailer\PHPMailer;
use UAReadiness\Smtp\UAEmail;

class PhpMailerUAReadiness extends UAEmail
{
    protected function sendEmail(): int
    {
        // PHP Mailer does it own validation but is not EAI compliant therefore we need to validate
        // with emailValidator that is fully compliant
        if (!$this->validateEmailAddress()) {
            return 1;
        }

        // PHP Mailer is not EAI compliant and will reject email addresses that contains unicode
        // characters in local-part. Moreover it is not IDNA 2008 compliant for domain as it does
        // not use the right IDN flags, so convert domain and hope for an ASCII local-part
        $converted_to = $this->convertEmailDomain(true);
        if ($converted_to === null) {
            return 1;
        }

        $mail = new PHPMailer(true);

        try {
            //Server settings
            //$mail->SMTPDebug = SMTP::DEBUG_SERVER;
            $mail->isSMTP();
            $mail->SMTPAuth = false;
            $mail->Host = $this->host;
            $mail->Port = $this->port;
            // need to explicitly specify charset to correctly handle recipient
            $mail->CharSet = 'UTF-8';

            $mail->setFrom($this->from);
            $mail->addAddress($converted_to);

            $mail->isHTML(false);
            $mail->Subject = $this->subject;
            $mail->Body = $this->body;

            $mail->send();
        } catch (Exception $e) {
            echo "ERROR: " . $mail->ErrorInfo . "\n";
            return 1;
        }

        return 0;
    }
}


$email = new PhpMailerUAReadiness();
$email->send();