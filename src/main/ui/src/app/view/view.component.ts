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
import { EmptyLicense, License, LicenseService } from "../license.service";
import { ConfirmationService } from "../confirmation.service";
import { Location } from "@angular/common";
import { AuthService } from "../auth.service";
import { LoaderService } from '../loader/loader.service';

@Component({
    selector: 'app-view',
    templateUrl: './view.component.html',
    styleUrls: ['./view.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ViewComponent implements OnInit {

    license: License = new EmptyLicense();

    constructor(private route: ActivatedRoute,
        private location: Location,
        private confirmationService: ConfirmationService,
        private licenseService: LicenseService,
        private authService: AuthService,
        private router: Router,
        private loaderService: LoaderService) {
    }

    ngOnInit() {

        this.showLoader();
        this.route.url.subscribe((segments: UrlSegment[]) => {
            if (segments[1]) {
                let id = Number(segments[1]);
                console.log("a license id was passed for viewing: ", id);
                this.licenseService.getLicense(id).subscribe(
                    license => this.license = license
                );
            }
            this.hideLoader();
        });
    }

    remove = id => {

        this.showLoader();
        this.authService.assureLoggedIn();
        let path = this.location.path();

        this.licenseService.getLicense(id)
            .subscribe(license => {
                this.confirmationService.init(
                    "Remove license [" + license.id + "] '" + license.fedoraName + "'?",
                    () =>
                        this.licenseService.removeLicense(id).subscribe(
                            success => console.log("successful removal", success),
                            error => console.log("erroneous removal", error)
                        ),
                    () => {
                        console.log("canceled removing license : " + license.id);
                        this.router.navigate([path]);
                    },
                );
                this.hideLoader();
            }
            );
    };

    edit = id => {
        this.showLoader();
        this.router.navigate(["/edit", id])
        this.hideLoader();
    }

    private showLoader(): void {
        this.loaderService.show();
    }

    private hideLoader(): void {
        this.loaderService.hide();
    }

}
