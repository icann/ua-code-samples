<?php

namespace UAReadiness\Http\Samples;

require_once __DIR__ . '/../../../vendor/autoload.php';

use Throwable;
use Symfony\Component\HttpClient\HttpClient;
use UAReadiness\Http\UAHttpRequest;

class SymfonyHttpClientUAReadiness extends UAHttpRequest
{
    protected function makeRequest(): string
    {
        // convert URL to A-Label as Symfony performs a conversion that is not IDNA 2008 compliant
        $converted_url = $this->convertUrlToALabel();
        if (!$converted_url) {
            return "";
        }

        $output = '';
        $client = HttpClient::create();
        try {
            $response = $client->request('GET', $converted_url);
            $output = $response->getContent();
        } catch (Throwable $e) {
            echo "ERROR: " . $e->getMessage() . "\n";
        }

        return $output;
    }
}


$request = new SymfonyHttpClientUAReadiness();
$request->run();