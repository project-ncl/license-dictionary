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
import {EmptyLicense, License, LicenseApprovalStatus, LicenseApprovalStatusList, EmptyLicenseApprovalStatus, LicenseService} from "../license.service";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class EditComponent implements OnInit {

  license: License;
  selectedLicensesStatus: number;
  id: number;
  
  licensesStatusList: LicenseApprovalStatus[] = [];
  

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
      
      this.licenseService.getLicensesApprovalStatus().subscribe(statusList => this.licensesStatusList = statusList.entries);
      this.selectedLicensesStatus = -1;
    });

    this.authService.assureLoggedIn();
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
      for (var j = 0; j < this.licensesStatusList.length; j++){
        if (this.licensesStatusList[j].id == this.selectedLicensesStatus) {
          this.license.licenseApprovalStatus = this.licensesStatusList[j];
        }
      }

      this.licenseService.addLicense(this.license).subscribe(
        license =>
          this.router.navigate(["/"]),
        error => this.errorMessage = error
      );
    }
  }
  
  isStatusSelected() {
    return (this.selectedLicensesStatus != -1);
  }
   
  reset = function(form) {
    if (form) {
      form.$setPristine(true);
      form.$setUntouched(true);
    }
    this.license = new EmptyLicense();
    this.selectedLicensesStatus = -1;
  };
  
}
