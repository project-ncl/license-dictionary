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

import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from "@angular/router";
import { License } from "../license.service";
import { ProjectEcosystem, Project, ProjectVersion, LicenseDeterminationType, DeterminationDate,
    ProjectVersionLicenseCheck, ProjectVersionLicense, ProjectVersionLicenseList, ProjectLicenseService } from "../project-license.service";
import { LoaderService } from '../loader/loader.service';

@Component({
    selector: 'app-list',
    templateUrl: './project-license-list.component.html',
    styleUrls: ['./project-license-list.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ProjectLicenseListComponent implements OnInit {

    pageSizes = [5, 10, 25, 50];
    itemsPerPage = 25;
    currentPage = 0;
    totalPages = 0;
    totalResultsCount = 0;
    currentResultsCount = 0;
    allLoaded = false;

    projectversionlicenses: ProjectVersionLicense[] = [];
    searchTerm: string;

    constructor(
        private projectVersionLicenseService: ProjectLicenseService,
        private router: Router,
        private loaderService: LoaderService) {
    }

    ngOnInit() {
        this.currentPage = 0;
        this.showLoader();
        this.projectVersionLicenseService.getProjectVersionLicenses(this.itemsPerPage, this.itemsPerPage * this.currentPage).subscribe(pvl => this.loadProjectVersionLicenseList(pvl));
    }

    searchForProjectVersionLicenses() {
        this.showLoader();
        if (this.searchTerm === undefined || this.searchTerm == null || this.searchTerm.length <= 0) {
            this.projectVersionLicenseService.getProjectVersionLicenses(this.itemsPerPage, this.itemsPerPage * this.currentPage).subscribe(pvl => this.loadProjectVersionLicenseList(pvl));
        }
        else {
            this.projectVersionLicenseService.findProjectVersionLicenses(this.searchTerm, this.itemsPerPage, this.itemsPerPage * this.currentPage)
                .subscribe(pvl => this.loadProjectVersionLicenseList(pvl));
        }
    }

    private loadProjectVersionLicenseList(projectversionlicenses: ProjectVersionLicenseList) {
        this.projectversionlicenses = projectversionlicenses.entries;

        this.totalResultsCount = projectversionlicenses.totalCount;
        this.currentResultsCount = this.projectversionlicenses.length + this.itemsPerPage * this.currentPage;
        this.totalPages = Math.round(Math.ceil((this.totalResultsCount / this.itemsPerPage)));

        this.allLoaded = this.currentPage >= (this.totalPages - 1);
        this.hideLoader();
    };

    // show items per page
    setPagination() {
        this.currentPage = 0;
        this.searchForProjectVersionLicenses();
    };

    prevPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }
        this.searchForProjectVersionLicenses();
    };

    nextPage() {
        if (!this.allLoaded) {
            this.currentPage++;
        }
        this.searchForProjectVersionLicenses();
    };

    setPage(page) {
        this.currentPage = page;
        this.searchForProjectVersionLicenses();
    };

    range(start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    private showLoader(): void {
        this.loaderService.show();
    }

    private hideLoader(): void {
        this.loaderService.hide();
    }

}
