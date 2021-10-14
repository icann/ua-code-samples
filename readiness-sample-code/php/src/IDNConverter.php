<?php

namespace UAReadiness;

class IDNConverter
{
    private function print_errors($errors)
    {
        $error_values = array(
            IDNA_ERROR_EMPTY_LABEL => "IDNA_ERROR_EMPTY_LABEL",
            IDNA_ERROR_LABEL_TOO_LONG => "IDNA_ERROR_LABEL_TOO_LONG",
            IDNA_ERROR_DOMAIN_NAME_TOO_LONG => "IDNA_ERROR_DOMAIN_NAME_TOO_LONG",
            IDNA_ERROR_LEADING_HYPHEN => "IDNA_ERROR_LEADING_HYPHEN",
            IDNA_ERROR_TRAILING_HYPHEN => "IDNA_ERROR_TRAILING_HYPHEN",
            IDNA_ERROR_HYPHEN_3_4 => "IDNA_ERROR_HYPHEN_3_4",
            IDNA_ERROR_LEADING_COMBINING_MARK => "IDNA_ERROR_LEADING_COMBINING_MARK",
            IDNA_ERROR_DISALLOWED => "IDNA_ERROR_DISALLOWED",
            IDNA_ERROR_PUNYCODE => "IDNA_ERROR_PUNYCODE",
            IDNA_ERROR_LABEL_HAS_DOT => "IDNA_ERROR_LABEL_HAS_DOT",
            IDNA_ERROR_INVALID_ACE_LABEL => "IDNA_ERROR_INVALID_ACE_LABEL",
            IDNA_ERROR_BIDI => "IDNA_ERROR_BIDI",
            IDNA_ERROR_CONTEXTJ => "IDNA_ERROR_CONTEXTJ"
        );
        foreach ($error_values as $err => $val)
            if ($errors & $err) {
                echo $val . "\n";
            }
    }

    /**
     * Convert UTF-8 string to IDN 2008 compliant A-Label.
     */
    public function convert_to_alabel($ulabel): string
    {
        $result = idn_to_ascii($ulabel,
            IDNA_DEFAULT
            | IDNA_USE_STD3_RULES
            | IDNA_CHECK_BIDI
            | IDNA_CHECK_CONTEXTJ
            | IDNA_NONTRANSITIONAL_TO_ASCII,
            INTL_IDNA_VARIANT_UTS46, $idnaInfo);
        if (!$result) {
            echo "Cannot convert '$ulabel' to A-label\n";
            $this->print_errors($idnaInfo['errors']);
        }
        return $result;
    }

}