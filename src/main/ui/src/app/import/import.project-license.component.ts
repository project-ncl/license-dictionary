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

import { HttpClient } from "@angular/common/http";

import { AuthService } from "../auth.service";
import { LoaderService } from '../loader/loader.service';
import { RestConfigService } from "../rest-config.service";
import { NotificationService } from '../notification/notification.service';


@Component({
    selector: 'app-import-project-license',
    templateUrl: './import.project-license.component.html',
    styleUrls: ['./import.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ImportProjectLicenseComponent implements OnInit {
    projectLicenseFileToUpload = null;

    constructor(
        private http: HttpClient,
        private authService: AuthService,
        private loaderService: LoaderService,
        private notificationService: NotificationService) {
    }

    ngOnInit() {
        this.authService.assureLoggedIn();
    }

    updateProjectLicenseFile($event) {
        console.log("update project license file event: ", $event);
        this.projectLicenseFileToUpload = $event.target.files[0];
    }

    importProjectLicenses() {


        let reader = new FileReader();
        let upload = this.upload;
        let component = this;
        let loaderService = this.loaderService;
        let notificationService = this.notificationService;

        reader.onloadend = function(file: any) {
            let contents = file.target.result;
            upload(contents, component, loaderService, notificationService);
        };

        if (this.projectLicenseFileToUpload) {
            this.loaderService.show();
            reader.readAsText(this.projectLicenseFileToUpload);
        }
    }

    upload(content, component, loaderService, notificationService) {
        component.http.post(RestConfigService.IMPORT_ENDPOINT_IMPORT_PROJECT_LICENSE_API, content).subscribe(
            () => {
                notificationService.success('Successfully imported project licenses !');
                loaderService.hide();
            },
            error => {
                console.log("error", error);
                notificationService.success(`Failed to import project licenses. ${error.message}, ${error.error}`);
                loaderService.hide();
            }
        )
    }

}