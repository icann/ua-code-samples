import {AbstractControl} from '@angular/forms';
import * as URI from "uri-js";
// @ts-ignore
import * as idna from 'idna-uts46';

export function ValidateUrl(control: AbstractControl) {
  const website = control.value;
  let url = undefined;
  try {
    url = URI.parse(website, {unicodeSupport: true});
  } catch (err: any) {
    console.log(`[uri-js] parsing URL ${website} failed: ${err?.message}`);
    return {
      invalidUrl: true
    };
  }
  const domain = url.host;
  console.log(`[uri-js] successfully parsed the domain ${domain} from URL ${website}`)
  try {
    const domain_ascii = idna.toAscii(domain, {
      transitional: false,
      useStd3ASCII: true,
      verifyDnsLength: true
    });
    if (domain !== domain_ascii) {
      console.log(`[idna-uts46] successfully convert ${domain} to ascii ${domain_ascii}`);
    }
    return null;
  } catch (err) {
    return {
      invalidUrl: true
    }
  }

  return null;
}
