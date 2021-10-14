<?php

namespace UAReadiness\Http\Samples;

require_once __DIR__ . '/../../../vendor/autoload.php';

use UAReadiness\Http\UAHttpRequest;

class CurlUAReadiness extends UAHttpRequest
{
    protected function makeRequest(): string
    {
        // cURL is UA compliant but it is too permissive on some URLs
        // This will ensure that empty label is not allowed
        $converted_url = $this->convertUrlToALabel();
        if (!$converted_url) {
            return "";
        }

        // create curl resource
        $ch = curl_init();

        // set curl options, see https://www.php.net/manual/fr/function.curl-setopt.php
        curl_setopt_array($ch, array(
            // we keep URL here as cURL is compliant with IDNA 2008, the conversion was for validation
            CURLOPT_URL => $this->url,
            //return the transfer as a string
            CURLOPT_RETURNTRANSFER => true,
            /*CURLOPT_VERBOSE => true*/));

        $output = curl_exec($ch);
        if (curl_errno($ch)) {
            $error_msg = curl_error($ch);
            echo "ERROR: " . $error_msg . "\n";
        }

        // close curl resource to free up system resources
        curl_close($ch);
        return $output;
    }
}


$request = new CurlUAReadiness();
$request->run();