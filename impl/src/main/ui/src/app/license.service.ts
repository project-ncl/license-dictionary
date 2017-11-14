import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class LicenseService {

  constructor(private http: HttpClient) {
  }

  getLicenses(): Observable<License[]> {
    return this.http.get<License[]>('/rest/licenses');
  }

  getLicense(id): Observable<License> {
    return this.http.get<License>(`/rest/licenses/${id}`)
  }

  findLicenses(searchTerm): Observable<License[]> {
    console.log('searching for ', searchTerm);
    let params = new HttpParams().set('searchTerm', searchTerm);
    return this.http.get<License[]>('/rest/licenses', {params: params});
  }

  removeLicense(id: number) {
    return this.http.delete(`rest/licenses/${id}`);
  }

  updateLicense(id: number, license: License): Observable<License> {
    return this.http.put<License>(`rest/licenses/${id}`, license);
  }

  addLicense(license: License): Observable<License> {
    return this.http.post<License>('/rest/licenses', license);
  }

}

export interface License {
  id: number,
  name: string,
  abbreviation: string,
  url: string,
  textUrl: string,
  content: string,
  urlAliases: string[],
  nameAliases: string[]
}
