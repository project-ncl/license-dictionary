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
import { ActivatedRoute, Router, UrlSegment } from "@angular/router";

import { MatChipInputEvent } from '@angular/material';

import { EmptyLicense, License, LicenseAlias, LicenseApprovalStatus, LicenseApprovalStatusList,
    EmptyLicenseApprovalStatus, LicenseService } from "../license.service";

import { AuthService } from "../auth.service";
import { LoaderService } from '../loader/loader.service';
import { NotificationService } from '../notification/notification.service';


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
    licenseAliases: LicenseAlias[] = [];


    licenseCodeSafeCopy: string;
    licenseFedoraNameSafeCopy: string;
    licenseSpdxNameSafeCopy: string;

    errorMessage: string;

    constructor(private route: ActivatedRoute,
        private router: Router,
        private licenseService: LicenseService,
        private authService: AuthService,
        private loaderService: LoaderService,
        private notificationService: NotificationService) {
    }

    ngOnInit() {
        this.showLoader();
        this.route.url.subscribe((segments: UrlSegment[]) => {

            this.licenseService.getLicensesApprovalStatus().subscribe(
                statusList => this.licensesStatusList = statusList.entries
            );

            if (segments[1]) {
                this.id = Number(segments[1]);
                console.log("a license id was passed for editing: ", this.id);
                this.licenseService.getLicense(this.id).subscribe(
                    license => this.initializeLicense(license)
                );
            }
            else {
                this.license = new EmptyLicense();
                this.licenseCodeSafeCopy = this.license.code;
                this.licenseFedoraNameSafeCopy = this.license.fedoraName;
                this.licenseSpdxNameSafeCopy = this.license.spdxName;
                this.selectedLicensesStatus = -1;
                this.licenseAliases = [];
            }
            this.hideLoader();
        });

        this.authService.assureLoggedIn();
    }

    initializeLicense(license: License) {
        this.license = license;
        this.selectedLicensesStatus = license.licenseApprovalStatus.id;
        this.licenseCodeSafeCopy = license.code;
        this.licenseFedoraNameSafeCopy = license.fedoraName;
        this.licenseSpdxNameSafeCopy = license.spdxName;
        this.licenseAliases = license.aliases;
    }

    saveLicense() {
        this.showLoader();
        for (var j = 0; j < this.licensesStatusList.length; j++) {
            if (this.licensesStatusList[j].id == this.selectedLicensesStatus) {
                this.license.licenseApprovalStatus = this.licensesStatusList[j];
            }
        }

        if (this.id) {
            this.licenseService.updateLicense(this.id, this.license).subscribe(
                license => {
                    this.router.navigate(["/"]);
                    this.hideLoader();
                    this.notificationService.success('License updated !');
                },
                error => {
                    console.log("error", error);
                    this.errorMessage = error;
                    this.hideLoader();
                }
            );
        } else {
            this.licenseService.addLicense(this.license).subscribe(
                license => {
                    this.router.navigate(["/"]);
                    this.hideLoader();
                    this.notificationService.success('License created !');
                },
                error => {
                    console.log("error", error);
                    this.errorMessage = error;
                    this.hideLoader();
                }
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
        this.licenseAliases = [];
    };

    removeAlias(alias: LicenseAlias) {
        console.log('BEFORE removeAlias: ' + JSON.stringify(this.licenseAliases));
        if (alias) {
            if (alias.id !== -1) {
                for (var j = 0; j < this.licenseAliases.length; j++) {
                    if (this.licenseAliases[j].id == alias.id) {
                        this.licenseAliases.splice(j, 1);
                    }
                }
            }
            else {
                for (var j = 0; j < this.licenseAliases.length; j++) {
                    if (this.licenseAliases[j].aliasName == alias.aliasName) {
                        this.licenseAliases.splice(j, 1);
                    }
                }
            }
        }
        console.log('AFTER removeAlias: ' + JSON.stringify(this.licenseAliases));
    }

    addAlias(event: MatChipInputEvent): void {
        console.log('BEFORE addAlias: ' + JSON.stringify(this.licenseAliases));

        let input = event.input;
        let value = event.value;

        // Add new alias
        if ((value || '').trim()) {
            if (!this.isDuplicatedValue(value)) {
                this.licenseAliases.push({ id: -1, aliasName: value.trim(), licenseId: this.license.id });
            }
        }

        // Reset the input value
        if (input) {
            input.value = '';
        }
        console.log('AFTER addAlias: ' + JSON.stringify(this.licenseAliases));
    }

    isDuplicatedValue(value) {
        for (var j = 0; j < this.licenseAliases.length; j++) {
            if (this.licenseAliases[j].aliasName == value.trim()) {
                return true;
            }
        }
        return false;
    }

    sortLicenseAlias(licenseAliases) {
        var sortedAliases: LicenseAlias[];
        sortedAliases = licenseAliases.slice(0);
        sortedAliases.sort((alias1, alias2): number => {
            return alias1.aliasName.localeCompare(alias2.aliasName);
        });

        return sortedAliases;
    }

    private showLoader(): void {
        this.loaderService.show();
    }

    private hideLoader(): void {
        this.loaderService.hide();
    }

}


