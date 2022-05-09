import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {BackendService} from "./backend.service";
import {RegisterResponse} from "./registerResponse";
import {ValidateUrl} from "./url.validator";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  output: string [] = [];
  emailBackendError: string[] | undefined;
  websiteBackendError: string[] | undefined;
  selectedLanguage: string = 'Java';
  languages: string[] = ['Java', 'Python', 'Javascript'];
  selectedWebsiteValidator: string | undefined;
  websiteValidators: { [id: string]: string[]; } = {
    Java: ['commons-validator', 'guava'],
    Python: [],
    Javascript: []
  };

  selectedWebsiteConverter: string | undefined;
  websiteConverters: { [id: string]: string[]; } = {
    Java: ['icu'],
    Python: ['idna'],
    Javascript: ['idna-uts46']
  };

  selectedEmailValidators: string | undefined;
  emailValidators: { [id: string]: string[]; } = {
    Java: ['commons-validator'],
    Python: ['email-validator'],
    Javascript: ['validator']
  }

  selectedEmailSender: string | undefined;
  emailSenders: { [id: string]: string[]; } = {
    Java: ['jakarta-mail'],
    Python: ['smtplib'],
    Javascript: ['nodemailer']
  }

  profileForm = new FormGroup({
    username: new FormControl(''),
    email: new FormControl('', [Validators.required]),
    website: new FormControl('', [Validators.required, ValidateUrl])
  });
  loading: boolean = false;

  get username() {
    return this.profileForm.get('username');
  }

  get email() {
    return this.profileForm.get('email');
  }

  get website() {
    return this.profileForm.get('website');
  }

  resetForm() {
    const formCopy = {...this.profileForm.value};
    // reset
    this.profileForm.reset();
    // but keep the data in form
    this.profileForm.setValue(formCopy)
  }

  reset() {
    this.selectedWebsiteValidator = undefined;
    this.selectedWebsiteConverter = undefined;
    this.selectedEmailValidators = undefined;
    this.selectedEmailSender = undefined;
    this.emailBackendError = undefined;
    this.websiteBackendError = undefined;

    this.resetForm();

    if (this.websiteValidators[this.selectedLanguage].length > 0) {
      this.selectedWebsiteValidator = this.websiteValidators[this.selectedLanguage][0];
    }
    if (this.websiteConverters[this.selectedLanguage].length > 0) {
      this.selectedWebsiteConverter = this.websiteConverters[this.selectedLanguage][0];
    }
    if (this.emailValidators[this.selectedLanguage].length > 0) {
      this.selectedEmailValidators = this.emailValidators[this.selectedLanguage][0];
    }
    if (this.emailSenders[this.selectedLanguage].length > 0) {
      this.selectedEmailSender = this.emailSenders[this.selectedLanguage][0];
    }
  }

  ngOnInit(): void {
    this.reset();
  }

  onRegister() {
    const website: { [id: string]: string; } = {};
    if (this.selectedWebsiteValidator) {
      website['validate'] = this.selectedWebsiteValidator;
    }
    if (this.selectedWebsiteConverter) {
      website['convert'] = this.selectedWebsiteConverter;
    }
    const email: { [id: string]: string; } = {};
    if (this.selectedEmailValidators) {
      email['validate'] = this.selectedEmailValidators;
    }
    if (this.selectedEmailSender) {
      email['smtp'] = this.selectedEmailSender;
    }

    let payload = {
      ...this.profileForm.value,
      libs: {
        idna: {...website},
        eai: {...email}
      }
    };
    this.loading = true;
    this.backend.register(this.selectedLanguage, payload).subscribe((r: RegisterResponse) => {
      this.loading = false;
      if (r.field.email.error) {
        this.emailBackendError = r.field.email.messages.filter(m => !m.startsWith('[Note'));
        this.email?.setErrors({emailBackendError: true})
      }
      if (r.field.website.error) {
        this.websiteBackendError = r.field.website.messages.filter(m => !m.startsWith('[Note'));
        this.website?.setErrors({websiteBackendError: true})
      }

      this.output = [...r.field.email.messages, ...r.field.website.messages];
      this.profileForm.markAllAsTouched();
    }, error => this.loading = false);
  }

  constructor(private backend: BackendService) {
  }
}
