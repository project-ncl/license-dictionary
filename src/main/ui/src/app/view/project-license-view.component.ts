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
import { EmptyProjectVersionLicense, ProjectVersionLicense, ProjectVersionLicenseList, ProjectLicenseService } from "../project-license.service";
import { Location } from "@angular/common";
import { AuthService } from "../auth.service";
import { LoaderService } from '../loader/loader.service';

@Component({
    selector: 'app-view',
    templateUrl: './project-license-view.component.html',
    styleUrls: ['./project-license-view.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ProjectLicenseViewComponent implements OnInit {

    projectversionlicense: ProjectVersionLicense = new EmptyProjectVersionLicense();

    constructor(private route: ActivatedRoute,
        private location: Location,
        private projectlicenseService: ProjectLicenseService,
        private authService: AuthService,
        private router: Router,
        private loaderService: LoaderService) {
    }

    ngOnInit() {

        this.showLoader();
        this.route.url.subscribe((segments: UrlSegment[]) => {
            if (segments[1]) {
                let id = Number(segments[1]);
                console.log("a project version license id was passed for viewing: ", id);
                this.projectlicenseService.getProjectLicense(id).subscribe(
                    projectversionlicense => this.projectversionlicense = projectversionlicense
                );
            }
            this.hideLoader();
        });
    }

    private showLoader(): void {
        this.loaderService.show();
    }

    private hideLoader(): void {
        this.loaderService.hide();
    }

}
