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
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ImportComponent implements OnInit {
  successMessage;
  errorMessage;
  fileToUpload = null;
  aliasFileToUpload = null;

  constructor(private http: HttpClient,
              private authService: AuthService) {
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
    this.successMessage = null;
    this.errorMessage = null;

    let reader = new FileReader();
    let upload = this.upload;
    let component = this;

    reader.onloadend = function (file: any) {
      let contents = file.target.result;
      upload(contents, component);
    };

    reader.readAsText(this.fileToUpload);
  }
  importLicensAliases() {
    this.successMessage = null;
    this.errorMessage = null;

    let reader = new FileReader();
    let uploadAliases = this.uploadAliases;
    let component = this;

    reader.onloadend = function (file: any) {
      let contents = file.target.result;
      uploadAliases(contents, component);
    };

    reader.readAsText(this.aliasFileToUpload);
  }
  
  upload(content, component) {
    component.http.post('/rest/import/licenses', content).subscribe(
      () => {
        component.successMessage = "Successfully imported licenses";
      },
      error => {
        console.log("error", error);
        component.errorMessage = `Failed to import licenses. ${error.message}, ${error.error}`
      }
    )
  }
  uploadAliases(content, component) {
    component.http.post('/rest/import/licenses-alias', content).subscribe(
      () => {
        component.successMessage = "Successfully imported license aliases";
      },
      error => {
        console.log("error", error);
        component.errorMessage = `Failed to import license aliases. ${error.message}, ${error.error}`
      }
    )
  }

}
