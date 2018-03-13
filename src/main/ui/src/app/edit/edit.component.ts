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
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {EmptyLicense, License, LicenseService} from "../license.service";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class EditComponent implements OnInit {
  license: License;
  newAlias: string;
  newUrlAlias: string;
  id: number;

  errorMessage: string;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private licenseService: LicenseService,
              private authService: AuthService) {
  }

  // loadToEdit()

  ngOnInit() {
    this.route.url.subscribe((segments: UrlSegment[]) => {
      this.license = new EmptyLicense();
      let idAsObject = segments[1];
      if (idAsObject) {
        console.log("an id was passed", idAsObject);
        this.id = Number(idAsObject);
        this.licenseService.getLicense(this.id).subscribe(license => this.license = license);
      }
    });

    this.authService.assureLoggedIn();
  }

  addAlias() {
    if (this.newAlias) {
      this.license.nameAliases.push(this.newAlias);
      this.newAlias = '';
    }
  }

  removeAlias(alias: string) {
    this.license.nameAliases = this.license.nameAliases.filter(a => a != alias)
  }

  addUrlAlias() {
    if (this.newUrlAlias) {
      this.license.urlAliases.push(this.newUrlAlias);
      this.newUrlAlias = '';
    }
  }

  removeUrlAlias(urlAlias: string) {
    this.license.urlAliases = this.license.urlAliases.filter(a => a != urlAlias)
  }

  saveLicense() {
    if (this.id) {
      this.licenseService.updateLicense(this.id, this.license).subscribe(
        license =>
          this.router.navigate(["/"]),
        error => {
          console.log("error", error);
          this.errorMessage = error
        }
      );
    } else {
      this.licenseService.addLicense(this.license).subscribe(
        license =>
          this.router.navigate(["/"]),
        error => this.errorMessage = error
      );
    }
  }
}
