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

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/map';

import { RestConfigService } from "./rest-config.service";

@Injectable()
export class ProjectLicenseService {

    constructor(private http: HttpClient) {
    }

    getProjectLicenses(maxCount: number, offset: number): Observable<ProjectVersionLicenseList> {
        let params = new HttpParams()
            .set('count', maxCount.toString())
            .set('offset', offset.toString());

        let licenseListObservable =
            this.http.get<ProjectVersionLicense[]>(RestConfigService.PROJECT_LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return licenseListObservable.map<HttpResponse<ProjectVersionLicense[]>, ProjectVersionLicenseList>(
            this.responseToProjectLicenseList
        );
    }

    private responseToProjectLicenseList = (response, _) => {
        let totalCount: number = +response.headers.get("totalCount");
        let offset: number = +response.headers.get("offset");
        let project_licenses = response.body;
        return {
            entries: project_licenses,
            totalCount: totalCount,
            offset: offset
        }
    }
}

export interface ProjectVersionLicenseList {
    entries: ProjectVersionLicense[],
    totalCount: number,
    offset: number
}

export interface ProjectVersionLicense {
    id: number,
    fedoraAbbreviation: string,
    fedoraName: string,
    spdxAbbreviation: string,
    spdxName: string,
    url: string,
    textUrl: string,
    code: string
}

