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
    selector: 'app-import',
    templateUrl: './import.component.html',
    styleUrls: ['./import.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ImportComponent implements OnInit {
    fileToUpload = null;
    aliasFileToUpload = null;

    constructor(
        private http: HttpClient,
        private authService: AuthService,
        private loaderService: LoaderService,
        private notificationService: NotificationService) {
    }

    ngOnInit() {
        this.authService.assureLoggedIn();
    }

    updateFile($event) {
        console.log("update file event: ", $event);
        this.fileToUpload = $event.target.files[0];
    }
    updateAliasFile($event) {
        console.log("update alias file event: ", $event);
        this.aliasFileToUpload = $event.target.files[0];
    }

    importLicenses() {
        this.loaderService.show();

        let reader = new FileReader();
        let upload = this.upload;
        let component = this;
        let loaderService = this.loaderService;
        let notificationService = this.notificationService;

        reader.onloadend = function(file: any) {
            let contents = file.target.result;
            upload(contents, component, loaderService, notificationService);
        };

        reader.readAsText(this.fileToUpload);
    }

    importLicensAliases() {
        this.loaderService.show();

        let reader = new FileReader();
        let uploadAliases = this.uploadAliases;
        let component = this;
        let loaderService = this.loaderService;
        let notificationService = this.notificationService;

        reader.onloadend = function(file: any) {
            let contents = file.target.result;
            uploadAliases(contents, component, loaderService, notificationService);
        };

        reader.readAsText(this.aliasFileToUpload);
    }

    upload(content, component, loaderService, notificationService) {
        component.http.post(RestConfigService.IMPORT_ENDPOINT_IMPORT_LICENSE_API, content).subscribe(
            () => {
                notificationService.success('Successfully imported licenses !');
                loaderService.hide();
            },
            error => {
                console.log("error", error);
                notificationService.success(`Failed to import licenses. ${error.message}, ${error.error}`);
                loaderService.hide();
            }
        )
    }

    uploadAliases(content, component, loaderService, notificationService) {
        component.http.post(RestConfigService.IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API, content).subscribe(
            () => {
                notificationService.success('Successfully imported license aliases !');
                loaderService.hide();
            },
            error => {
                console.log("error", error);
                notificationService.success(`Failed to import license aliases. ${error.message}, ${error.error}`);
                loaderService.hide();
            }
        )
    }

}
