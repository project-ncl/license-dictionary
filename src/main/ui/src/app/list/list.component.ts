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

import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {License, LicenseList, LicenseService} from "../license.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ListComponent implements OnInit {

    pageSizes = [5, 10, 25, 50];
    itemsPerPage = 25;
    currentPage = 0;
    totalPages = 0;
    totalResultsCount = 0;
    currentResultsCount = 0;
    allLoaded = false;

    licenses: License[] = [];
    searchTerm: string;

    constructor(private licenseService: LicenseService,
        private router: Router) {
    }

    ngOnInit() {
        this.currentPage = 0;
        this.licenseService.getLicenses(this.itemsPerPage, this.itemsPerPage * this.currentPage).subscribe(l => this.loadLicenseList(l));
    }

    searchForLicenses() {
        if (this.searchTerm === undefined || this.searchTerm == null || this.searchTerm.length <= 0) {
            this.licenseService.getLicenses(this.itemsPerPage, this.itemsPerPage * this.currentPage).subscribe(l => this.loadLicenseList(l));
        }
        else {
            this.licenseService.findLicenses(this.searchTerm, this.itemsPerPage, this.itemsPerPage * this.currentPage)
                .subscribe(l => this.loadLicenseList(l));
        }
    }

    private loadLicenseList(licenses: LicenseList) {
        this.licenses = licenses.entries;

        this.totalResultsCount = licenses.totalCount;
        this.currentResultsCount = this.licenses.length + this.itemsPerPage * this.currentPage;
        this.totalPages = Math.round(Math.ceil((this.totalResultsCount / this.itemsPerPage)));

        this.allLoaded = this.currentPage >= (this.totalPages - 1);
        //console.log("allLoaded: ", this.allLoaded, "currentResultsCount:", this.currentResultsCount, "totalResultsCount: ", this.totalResultsCount, this.licenses);
    };

    // show items per page
    setPagination() {
        this.currentPage = 0;
        this.searchForLicenses();
    };

    prevPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }
        this.searchForLicenses();
    };

    nextPage() {
        if (!this.allLoaded) {
            this.currentPage++;
        }
        this.searchForLicenses();
    };

    setPage(page) {
        this.currentPage = page;
        this.searchForLicenses();
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

    view = id => {
        this.router.navigate(["/view", id]);
    };

}
