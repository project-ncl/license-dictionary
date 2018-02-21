///
/// JBoss, Home of Professional Open Source.
/// Copyright 2017 Red Hat, Inc., and individual contributors
/// as indicated by the @author tags.
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
/// http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

import {RestConfigService} from "./rest-config.service";


@Injectable()
export class LicenseService {

    constructor(private http: HttpClient) {
    }

    getLicense(id): Observable<License> {
        return this.http.get<License>(RestConfigService.LICENSE_ENDPOINT + `/${id}`)
    }

    findLicenses(searchTerm: string, maxCount: number, offset: number): Observable<LicenseList> {
        let params = new HttpParams()
            .set('searchTerm', searchTerm)
            .set('offset', offset.toString())
            .set('count', maxCount.toString());

        let licenseListObservable =
            this.http.get<License[]>(RestConfigService.LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(this.responseToLicenseList);
    }

    findLicensesByCode(code: string, maxCount: number, offset: number): Observable<LicenseList> {
        let params = new HttpParams()
            .set('code', code)
            .set('offset', offset.toString())
            .set('count', maxCount.toString());

        let licenseListObservable =
            this.http.get<License[]>(RestConfigService.LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(this.responseToLicenseList);
    }

    findLicensesByFedoraName(fedoraName: string, maxCount: number, offset: number): Observable<LicenseList> {
        let params = new HttpParams()
            .set('fedoraName', fedoraName)
            .set('offset', offset.toString())
            .set('count', maxCount.toString());

        let licenseListObservable =
            this.http.get<License[]>(RestConfigService.LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(this.responseToLicenseList);
    }

    findLicensesBySpdxName(spdxName: string, maxCount: number, offset: number): Observable<LicenseList> {
        let params = new HttpParams()
            .set('spdxName', spdxName)
            .set('offset', offset.toString())
            .set('count', maxCount.toString());

        let licenseListObservable =
            this.http.get<License[]>(RestConfigService.LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(this.responseToLicenseList);
    }

    removeLicense(id: number): Observable<any> {
        console.log("removing license ", id);
        return this.http.delete(RestConfigService.LICENSE_ENDPOINT + `/${id}`);
    }

    updateLicense(id: number, license: License): Observable<License> {
        return this.http.put<License>(RestConfigService.LICENSE_ENDPOINT + `/${id}`, license);
    }

    addLicense(license: License): Observable<License> {
        return this.http.post<License>(RestConfigService.LICENSE_ENDPOINT, license);
    }

    getLicenses(maxCount: number, offset: number): Observable<LicenseList> {
        let params = new HttpParams()
            .set('count', maxCount.toString())
            .set('offset', offset.toString());

        let licenseListObservable =
            this.http.get<License[]>(RestConfigService.LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<License[]>, LicenseList>(
            this.responseToLicenseList
        );
    }

    getLicensesApprovalStatus(): Observable<LicenseApprovalStatusList> {
        let params = new HttpParams();

        let licenseApprovalStatusListObservable =
            this.http.get<LicenseApprovalStatus[]>(RestConfigService.LICENSE_STATUS_ENDPOINT, { params: params, observe: 'response' });

        return licenseApprovalStatusListObservable.map<HttpResponse<LicenseApprovalStatus[]>, LicenseApprovalStatusList>(
            this.responseToLicenseStatusList
        );
    }

    getLicenseApprovalStatus(id): Observable<LicenseApprovalStatus> {
        return this.http.get<LicenseApprovalStatus>(RestConfigService.LICENSE_STATUS_ENDPOINT + `/${id}`)
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

    private responseToLicenseStatusList = (response, _) => {
        let totalCount: number = +response.headers.get("totalCount");
        let offset: number = +response.headers.get("offset");
        let licensesStatus = response.body;
        return {
            entries: licensesStatus,
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

export interface LicenseApprovalStatusList {
    entries: LicenseApprovalStatus[],
    totalCount: number,
    offset: number
}

export interface LicenseApprovalStatus {
    id: number,
    name: string
}

export class EmptyLicenseApprovalStatus implements LicenseApprovalStatus {
    id: number;
    name: string;
}

export interface LicenseAlias {
    id: number,
    aliasName: string,
    licenseId: number
}

export class EmptyLicenseAlias implements LicenseAlias {
    id: number;
    aliasName: string;
    licenseId: number
}

export interface License {
    id: number,
    fedoraAbbreviation: string,
    fedoraName: string,
    spdxAbbreviation: string,
    spdxName: string,
    url: string,
    textUrl: string,
    code: string,
    licenseApprovalStatus: LicenseApprovalStatus,
    aliases: LicenseAlias[]
}

export class EmptyLicense implements License {
    id: number;
    fedoraAbbreviation: string;
    fedoraName: string;
    spdxAbbreviation: string;
    spdxName: string;
    url: string;
    textUrl: string;
    code: string;
    licenseApprovalStatus: EmptyLicenseApprovalStatus;
    aliases: LicenseAlias[] = []
}
