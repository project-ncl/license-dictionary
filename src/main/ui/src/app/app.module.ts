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

import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';


import {AppComponent} from './app.component';
import {AuthService} from './auth.service';
import {ListComponent} from './list/list.component';
import {EditComponent} from './edit/edit.component';
import {LicenseService} from "./license.service";
import {ConfirmationComponent} from './confirmation/confirmation.component';
import {ConfirmationService} from "./confirmation.service";
import {HttpHeadersInterceptor} from "./http-config.service";
import {ImportComponent} from './import/import.component';
import {ViewComponent} from './view/view.component';

const appRoutes: Routes = [
  {path: '', component: ListComponent},
  {path: 'edit', component: EditComponent},
  {path: 'edit/:id', component: EditComponent},
  {path: 'view/:id', component: ViewComponent},
  {path: 'confirm', component: ConfirmationComponent},
  {path: 'import', component: ImportComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    ListComponent,
    EditComponent,
    ConfirmationComponent,
    ImportComponent,
    ViewComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    AuthService,
    LicenseService,
    ConfirmationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpHeadersInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
