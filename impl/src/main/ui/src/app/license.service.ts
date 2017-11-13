import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class LicenseService {

  licenses: License[] = [
    {
      id: 5, name: 'ASL 1.0', url: 'http://example.com', textUrl: 'http://example.com/text',
      content: "Mock contentMock \ncontentMock contentMock contentMock contentMock contentMock contentMock" +
      " contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock " +
      "contentMock contentMock contentMock contentMock contentMock content", urlAliases: [], nameAliases: []
    },
    {
      id: 4, name: 'ASgL 2.0', url: 'http://example.com', textUrl: 'http://example.com/text',
      content: "Mock contentMock \ncontentMock contentMock contentMock contentMock contentMock contentMock" +
      " contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock " +
      "contentMock contentMock contentMock contentMock contentMock content", urlAliases: [], nameAliases: []
    },
    {
      id: 1, name: 'AfSL 4.0', url: 'http://example.com', textUrl: 'http://example.com/text',
      content: "Mock contentMock \ncontentMock contentMock contentMock contentMock contentMock contentMock" +
      " contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock " +
      "contentMock contentMock contentMock contentMock contentMock content", urlAliases: [], nameAliases: []
    },
    {
      id: 3, name: 'AbSL 3.0', url: 'http://example.com', textUrl: 'http://example.com/text',
      content: "Mock contentMock \ncontentMock contentMock contentMock contentMock contentMock contentMock" +
      " contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock contentMock " +
      "contentMock contentMock contentMock contentMock contentMock content", urlAliases: [], nameAliases: []
    }
  ];

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
    this.licenses = this.licenses.filter(l => l.id != id);
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
  url: string,
  textUrl: string,
  content: string,
  urlAliases: string[],
  nameAliases: string[]
}
