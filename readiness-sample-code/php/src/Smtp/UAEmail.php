<?php

namespace UAReadiness\Smtp;

use Egulias\EmailValidator\EmailValidator;
use Egulias\EmailValidator\Validation\EmailValidation;
use Egulias\EmailValidator\Validation\RFCValidation;
use ReflectionClass;
use UAReadiness\IDNConverter;

abstract class UAEmail
{
    protected string $to;
    protected string $host = "localhost";
    protected int $port = 25;
    protected string $from = "ua@test.org";
    protected string $subject = "UA Compliant Email";
    protected string $body = "This email was sent to test UA compliance.";
    private EmailValidator $emailValidator;
    private EmailValidation $emailValidation;


    public function __construct()
    {
        $cls = new ReflectionClass($this);
        $help = basename($cls->getFileName()) . " --to <Email Recipient> [--host <SMTP Server Address>] [--port <SMTP Server port>]\n";
        $longopts = array(
            "help",
            "to:",
            "host::",
            "port::"
        );
        $options = getopt("h", $longopts);
        if (key_exists("h", $options) || key_exists("help", $options)) {
            echo $help;
            exit(0);
        }

        if (!key_exists("to", $options)) {
            echo "Missing --to option\n";
            echo $help;
            exit(1);
        }
        $this->to = $options["to"];
        if (key_exists("host", $options)) {
            $this->host = $options["host"];
        }
        if (key_exists("port", $options)) {
            $this->port = $options["port"];
        }

        // create the email validator with RFC validation that is the most UA compliant
        $this->emailValidator = new EmailValidator();
        $this->emailValidation = new RFCValidation();
    }

    /**
     * Send a UA compliant email.
     *
     * @return int  0 on success, 1 (or any positive integer) otherwise
     */
    abstract protected function sendEmail(): int;

    public function send(): int
    {
        exit($this->sendEmail());
    }

    /**
     * Validate the email address.
     */
    protected function validateEmailAddress(): bool {
         if (!$this->emailValidator->isValid($this->to, $this->emailValidation)) {
             echo "Email address '$this->to' is invalid";
             return false;
         }
         return true;
    }

    /**
     * Convert an email address domain part for IDNA 2008 compatibility.
     */
    protected function convertEmailDomain($fail_if_local_part_is_utf8): ?string {
        $idnConverter = new IDNConverter();
        $local_part = substr($this->to, 0, strrpos($this->to, '@'));
        if ($fail_if_local_part_is_utf8 && !mb_check_encoding($local_part, 'ASCII')) {
            echo "Local part is UTF-8, which is unsupported\n";
            return null;
        }
        $domain = $idnConverter->convert_to_alabel(substr($this->to, strrpos($this->to, '@') + 1));
        if (!$domain) {
            return null;
        }
        return $local_part . '@' . $domain;
    }
}