<?php

namespace UAReadiness\Http;

use ReflectionClass;
use UAReadiness\IDNConverter;

abstract class UAHttpRequest
{
    protected string $url;

    public function __construct()
    {
        // prevent curl call to IDN to return:
        // Failed to convert <domain> to ACE; could not convert string to UTF-8
        // when when all configuration is good
        setlocale(LC_ALL, "en_US.UTF-8");

        $cls = new ReflectionClass($this);
        $help = basename($cls->getFileName()) . " --url <HTTP URL>\n";

        $longopts = array(
            "help",
            "url:",
        );
        $options = getopt("h", $longopts);
        if (key_exists("h", $options) || key_exists("help", $options)) {
            echo $help;
            exit(0);
        }

        if (!key_exists("url", $options)) {
            echo "Missing --url option\n";
            echo $help;
            exit(1);
        }
        $this->url = $options["url"];
    }

    /**
     * Make a UA compliant HTTP GET request.
     *
     * @return string The HTTP server response if not error else an empty string.
     */
    abstract protected function makeRequest(): string;

    public function run()
    {
        $response = $this->makeRequest();
        if (empty($response)) {
            exit(1);
        }
        echo $response;
    }

    protected function convertUrlToALabel(): ?string
    {
        $idnConverter = new IDNConverter();
        // quick hack to transform host to A-label
        $domain = parse_url($this->url, PHP_URL_HOST);
        $convertedDomain = $idnConverter->convert_to_alabel($domain);
        if (!$convertedDomain) {
            return null;
        }
        $pos = strpos($this->url, $domain);
        return substr_replace($this->url, $convertedDomain, $pos, strlen($domain));
    }
}