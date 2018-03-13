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

  licenses: License[] = [];
  searchTerm: string;

  defaultCount = 20;

  loadedUntil = 0;
  allLoaded = false;

  constructor(private licenseService: LicenseService,
              private router: Router) {
  }

  ngOnInit() {
    this.licenseService.getLicenses(this.defaultCount, 0).subscribe(l => this.loadLicenseList(l));
  }

  searchForLicenses() {
    console.log("search term: ", this.searchTerm);
    this.licenseService.findLicenses(this.searchTerm, this.defaultCount, 0)
      .subscribe(l => this.loadLicenseList(l)); //todo
  }

  fetchMore() {
    console.log("fetching more");
    if (this.searchTerm) {
      console.log("with search term");
      this.licenseService.findLicenses(this.searchTerm, this.defaultCount, this.loadedUntil)
        .subscribe(l => this.appendLicenseList(l));
    } else {
      console.log("without search term");
      this.licenseService.getLicenses(this.defaultCount, this.loadedUntil)
        .subscribe(l => this.appendLicenseList(l));
    }
  }

  view = id => {
    this.router.navigate(["/view", id]);
  };

  private loadLicenseList(licenses: LicenseList) {
    this.licenses = licenses.entries;
    this.loadedUntil = this.licenses.length;
    this.allLoaded = this.loadedUntil >= licenses.totalCount;
    console.log("allLoaded: ", this.allLoaded, "loadedUntil:", this.loadedUntil, "totalCount: ", licenses.totalCount, this.licenses);
  };

  private appendLicenseList(licenses: LicenseList) {
    this.licenses = this.licenses.concat(licenses.entries);
    this.loadedUntil = this.licenses.length + licenses.offset;
    this.allLoaded = this.loadedUntil >= licenses.totalCount;
    console.log("appended: ", this.allLoaded, "loadedUntil:", this.loadedUntil, "totalCount: ", licenses.totalCount, this.licenses);
  };
}
