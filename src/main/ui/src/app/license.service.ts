import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

@Injectable()
export class LicenseService {

  constructor(private http: HttpClient) {
  }

  getLicense(id): Observable<License> {
    return this.http.get<License>(`/rest/licenses/${id}`)
  }

  findLicenses(searchTerm: string, maxCount: number, offset: number): Observable<LicenseList> {
    let params = new HttpParams()
      .set('searchTerm', searchTerm)
      .set('offset', offset.toString())
      .set('count', maxCount.toString());

    let licenseListObservable =
      this.http.get<License[]>('/rest/licenses', {params: params, observe: 'response'});
    return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(
      this.responseToLicenseList
    );
  }

  removeLicense(id: number): Observable<any> {
    console.log("removing license ", id);
    return this.http.delete(`rest/licenses/${id}`);
  }

  updateLicense(id: number, license: License): Observable<License> {
    return this.http.put<License>(`rest/licenses/${id}`, license);
  }

  addLicense(license: License): Observable<License> {
    return this.http.post<License>('/rest/licenses', license);
  }

  getLicenses(maxCount: number, offset: number): Observable<LicenseList> {
    let params = new HttpParams()
      .set('count', maxCount.toString())
      .set('offset', offset.toString());
    let licenseListObservable = this.http.get<License[]>('/rest/licenses', {params: params, observe: 'response'});
    return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(
      this.responseToLicenseList
    );
  }

  private responseToLicenseList = (response, _) => {
    let totalCount: number = +response.headers.get("totalCount");
    let offset: number = +response.headers.get("offset");
    let licenses = response.body;
    return {
      entries: licenses,
      totalCount: totalCount,
      offset: offset
    }
  }
}

export interface LicenseList {
  entries: License[],
  totalCount: number,
  offset: number
}

export interface License {
  id: number,
  name: string,
  status: string,
  abbreviation: string,
  url: string,
  textUrl: string,
  content: string,
  urlAliases: string[],
  nameAliases: string[]
}

export class EmptyLicense implements License {
  id: number;
  name: string;
  status: string;
  abbreviation: string;
  url: string;
  textUrl: string;
  content: string;
  urlAliases: string[];
  nameAliases: string[]
}
