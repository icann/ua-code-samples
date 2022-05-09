import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {RegisterResponse} from "./registerResponse";

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http: HttpClient) {
  }

  register(language: string, payload: any): Observable<RegisterResponse> {
    const url = (<any>environment)[language.toLowerCase()]
    return this.http.post<RegisterResponse>(url + '/ua/register', payload);
  }
}
