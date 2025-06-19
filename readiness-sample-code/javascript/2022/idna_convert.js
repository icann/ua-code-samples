const domainToUnicode = require('url').domainToUnicode;
idna = require('idna-uts46');

function idna_convert(website, results) {
    let url = undefined;
    try {
        url = new URL(website);
    } catch (err) {
        return results.addResult(
            true,
            `[nodejs] parsing URL ${website} failed: ${err?.message}`,
            website);
    }

    // if node automatically converted the url (when compiled with ICU), convert it back
    // since it doesn't use the proper ICU flags,
    // see: https://github.com/nodejs/node/blob/97826d3d8321f4dd86f2973fa6838101d14e8082/src/node_i18n.cc#L612
    // where USE_STD3_RULES is missing in default mode
    const domain = domainToUnicode(url.hostname);
    url.hostname = domain;
    results.addResult(
        false,
        `[nodejs] successfully parsed the domain ${domain} from URL ${website}`,
        url.toString());
    const domain_normalized = domain.normalize('NFC');
    url.hostname = domain_normalized;
    if (!website.includes(domain_normalized)) {
        results.addResult(false, `[nodejs] Website domain has been normalized in NFC form: ${domain_normalized}`, url.toString())
    }
    try {
        const domain_ascii = idna.toAscii(domain_normalized, {
            transitional: false,
            useStd3ASCII: true,
            verifyDnsLength: true});
        url.hostname = domain_ascii;
        if (domain_ascii !== domain_normalized) {
            results.addResult(
                false,
                `[idna-uts46] The website domain name has been transformed to A-Label to ensure IDNA 2008 compliant queries: <a href=${domain_ascii}>${domain_ascii}</a>`,
                url.toString());
        }
        return results.addResult(false, `[idna-uts46] ${website} is a valid URL`, url.toString());
    } catch (err) {
        return results.addResult(
            true,
            `[idna-uts46] URL ${website} is invalid: ${err?.message}`,
            website);
    }
}

module.exports = { idna_convert };