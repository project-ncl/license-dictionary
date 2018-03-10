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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { AuthService } from './auth.service';
import { ListComponent } from './list/list.component';
import { EditComponent } from './edit/edit.component';
import { LicenseService } from "./license.service";
import { ConfirmationComponent } from './confirmation/confirmation.component';
import { ConfirmationService } from "./confirmation.service";
import { HttpHeadersInterceptor } from "./http-config.service";
import { ImportComponent } from './import/import.component';
import { ViewComponent } from './view/view.component';

import { HttpErrorInterceptor } from './http-error.interceptor';


import { ExistingLicenseCodeValidatorDirective } from './custom-validators/existing-licensecode-validator';
import { ExistingLicenseFedoraNameValidatorDirective } from './custom-validators/existing-licensefedoraname-validator';
import { ExistingLicenseSpdxNameValidatorDirective } from './custom-validators/existing-licensesdpxname-validator';
import { AutofocusDirective } from './custom-validators/autofocus';

import { LoaderService } from './loader/loader.service';
import { LoaderComponent } from './loader/loader.component';

// Required for https://material.angular.io/guide/getting-started
// USING THE MODULES BELOW, ALTHOUGH RECOMMENDED BY MATERIAL, WILL BREAK THE ANIMATION ON MAT-CHIPS
// AND WILL CAUSE THE REMOVED CHIPS NOT TO BE REMOVED IN THE UI (ONLY IN THE BACKING MODEL)
//import {NoopAnimationsModule, BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { MatChipsModule, MatIconModule, MatInputModule, MatProgressBarModule, MatProgressSpinnerModule } from '@angular/material';

const appRoutes: Routes = [
    { path: '', component: ListComponent },
    { path: 'edit', component: EditComponent },
    { path: 'edit/:id', component: EditComponent },
    { path: 'view/:id', component: ViewComponent },
    { path: 'confirm', component: ConfirmationComponent },
    { path: 'import', component: ImportComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        ListComponent,
        EditComponent,
        ConfirmationComponent,
        ImportComponent,
        ViewComponent,
        ExistingLicenseCodeValidatorDirective,
        ExistingLicenseFedoraNameValidatorDirective,
        ExistingLicenseSpdxNameValidatorDirective,
        AutofocusDirective,
        LoaderComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        RouterModule.forRoot(appRoutes),
        MatChipsModule,
        MatIconModule,
        MatInputModule,
        MatProgressBarModule,
        MatProgressSpinnerModule
    ],
    exports: [
        LoaderComponent
    ],
    providers: [
        AuthService,
        LicenseService,
        ConfirmationService,
        LoaderService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpHeadersInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpErrorInterceptor,
            multi: true,
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
