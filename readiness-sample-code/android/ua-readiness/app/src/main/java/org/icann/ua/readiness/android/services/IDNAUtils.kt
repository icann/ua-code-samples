package org.icann.ua.readiness.android.services

import android.icu.text.IDNA
import java.net.URL

object IDNAUtils {

    private const val flags =
        IDNA.CHECK_BIDI or IDNA.CHECK_CONTEXTJ or IDNA.CHECK_CONTEXTO or IDNA.NONTRANSITIONAL_TO_ASCII or IDNA.USE_STD3_RULES
    private val idna: IDNA = IDNA.getUTS46Instance(flags)
    private val info = IDNA.Info()

    /**
     * Convert URL host in ASCII with IDNA 2008 compliant
     */
    fun hostToAscii(url: String): String {
        val inUrl = URL(url)
        val hostAscii = domainToAscii(inUrl.host)
        return URL(inUrl.protocol, hostAscii, inUrl.port, inUrl.file).toString()
    }

    fun domainToAscii(domain: String): String {
        val sb = StringBuilder()
        val domainAscii = idna.nameToASCII(domain, sb, info).toString()
        if (info.hasErrors()) {
            throw IDNAException(info.errors.toString())
        }
        return domainAscii
    }
}

class IDNAException(message: String) : Exception(message)