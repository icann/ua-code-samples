<?php

namespace UAReadiness\Http\Samples;

require_once __DIR__ . '/../../../vendor/autoload.php';

use GuzzleHttp\Client;
use Throwable;
use UAReadiness\Http\UAHttpRequest;

class GuzzleUAReadiness extends UAHttpRequest
{
    protected function makeRequest(): string
    {
        $output = '';
        try {
            $client = new Client([
                'base_uri' => $this->url,
                // provide IDN flags for IDNA 2008 support
                'idn_conversion' => IDNA_DEFAULT
                    | IDNA_USE_STD3_RULES
                    | IDNA_CHECK_BIDI
                    | IDNA_CHECK_CONTEXTJ
                    | IDNA_NONTRANSITIONAL_TO_ASCII,
            ]);
            $response = $client->request('GET');
            $output = $response->getBody();
        } catch (Throwable $e) {
            echo "ERROR: " . $e->getMessage() . "\n";
        }

        return $output;
    }
}


$request = new GuzzleUAReadiness();
$request->run();