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

import { License, EmptyLicense } from "./license.service";
import { RestConfigService } from "./rest-config.service";

@Injectable()
export class ProjectLicenseService {

    constructor(private http: HttpClient) {
    }

    getProjectLicense(id): Observable<ProjectVersionLicense> {
        return this.http.get<ProjectVersionLicense>(RestConfigService.PROJECT_LICENSE_ENDPOINT + `/${id}`)
    }

    findProjectVersionLicenses(searchTerm: string, maxCount: number, offset: number): Observable<ProjectVersionLicenseList> {
        let params = new HttpParams()
            .set('search', searchTerm)
            .set('offset', offset.toString())
            .set('count', maxCount.toString());

        let projectVersionLicenseListObservable =
            this.http.get<ProjectVersionLicense[]>(RestConfigService.PROJECT_LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return projectVersionLicenseListObservable.map<HttpResponse<ProjectVersionLicense[]>, ProjectVersionLicenseList>(this.responseToProjectLicenseList);
    }

    getProjectVersionLicenses(maxCount: number, offset: number): Observable<ProjectVersionLicenseList> {
        let params = new HttpParams()
            .set('count', maxCount.toString())
            .set('offset', offset.toString());

        let projectVersionLicenseListObservable =
            this.http.get<ProjectVersionLicense[]>(RestConfigService.PROJECT_LICENSE_ENDPOINT, { params: params, observe: 'response' });

        return projectVersionLicenseListObservable.map<HttpResponse<ProjectVersionLicense[]>, ProjectVersionLicenseList>(
            this.responseToProjectLicenseList
        );
    }

    getProjectVersionLicenseHintsByProjectVersionLicense(projectVersionLicense: number, maxCount: number, offset: number): Observable<ProjectVersionLicenseHintList> {
        let params = new HttpParams()
            .set('query', 'projectVersionLicense.id==' + projectVersionLicense.toString())
            .set('count', maxCount.toString())
            .set('offset', offset.toString());

        let projectVersionLicenseHintListObservable =
            this.http.get<ProjectVersionLicenseHint[]>(RestConfigService.PROJECT_LICENSE_HINT_ENDPOINT, { params: params, observe: 'response' });

        return projectVersionLicenseHintListObservable.map<HttpResponse<ProjectVersionLicenseHint[]>, ProjectVersionLicenseHintList>(
            this.responseToProjectLicenseHintList
        );
    }

    private responseToProjectLicenseList = (response, _) => {
        let totalCount: number = +response.headers.get("totalCount");
        let offset: number = +response.headers.get("offset");
        let project_version_licenses = response.body;
        return {
            entries: project_version_licenses,
            totalCount: totalCount,
            offset: offset
        }
    }

    private responseToProjectLicenseHintList = (response, _) => {
        let totalCount: number = +response.headers.get("totalCount");
        let offset: number = +response.headers.get("offset");
        let project_version_license_hints = response.body;
        return {
            entries: project_version_license_hints,
            totalCount: totalCount,
            offset: offset
        }
    }
}

export interface ProjectEcosystem {
    id: number,
    name: string
}
export interface Project {
    id: number,
    key: string,
    projectEcosystem: ProjectEcosystem
}
export interface ProjectVersion {
    id: number,
    scmUrl: string,
    scmRevision: string,
    version: string,
    project: Project
}
export interface LicenseDeterminationType {
    id: number,
    name: string,
    description: string
}
export interface DeterminationDate {
    dayOfYear: number,
    year: number,
    month: string,
    dayOfMonth: number,
    dayOfWeek: string,
    monthValue: number
}
export interface ProjectVersionLicenseCheck {
    id: number,
    determinedByUser: string,
    determinationDate: DeterminationDate,
    notes: string,
    projectVersion: ProjectVersion,
    licenseDeterminationType: LicenseDeterminationType
}
export interface ProjectVersionLicense {
    id: number,
    scope: string,
    license: License,
    projectVersionLicenseCheck: ProjectVersionLicenseCheck
}
export interface LicenseHintType {
    id: number,
    name: string
}
export interface ProjectVersionLicenseHint {
    id: number,
    value: string,
    projectVersionLicense: ProjectVersionLicense,
    licenseHintType: LicenseHintType
}

export interface ProjectVersionLicenseList {
    entries: ProjectVersionLicense[],
    totalCount: number,
    offset: number
}
export interface ProjectVersionLicenseHintList {
    entries: ProjectVersionLicenseHint[],
    totalCount: number,
    offset: number
}

export class EmptyProjectEcosystem implements ProjectEcosystem {
    id: number;
    name: string
}
export class EmptyProject implements Project {
    id: number;
    key: string;
    projectEcosystem: EmptyProjectEcosystem
}
export class EmptyProjectVersion implements ProjectVersion {
    id: number;
    scmUrl: string;
    scmRevision: string;
    version: string;
    project: EmptyProject
}
export class EmptyLicenseDeterminationType implements LicenseDeterminationType {
    id: number;
    name: string;
    description: string
}
export class EmptyDeterminationDate implements DeterminationDate {
    dayOfYear: number;
    year: number;
    month: string;
    dayOfMonth: number;
    dayOfWeek: string;
    monthValue: number
}
export class EmptyProjectVersionLicenseCheck implements ProjectVersionLicenseCheck {
    id: number;
    determinedByUser: string;
    determinationDate: EmptyDeterminationDate;
    notes: string;
    projectVersion: EmptyProjectVersion;
    licenseDeterminationType: EmptyLicenseDeterminationType
}
export class EmptyProjectVersionLicense implements ProjectVersionLicense {
    id: number;
    scope: string;
    license: EmptyLicense;
    projectVersionLicenseCheck: EmptyProjectVersionLicenseCheck
}
export class EmptyLicenseHintType implements LicenseHintType {
    id: number;
    name: string
}
export class EmptyProjectVersionLicenseHint implements ProjectVersionLicenseHint {
    id: number;
    value: string;
    projectVersionLicense: EmptyProjectVersionLicense;
    licenseHintType: EmptyLicenseHintType
}
