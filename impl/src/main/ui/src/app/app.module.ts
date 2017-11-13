import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';


import {AppComponent} from './app.component';
import {ListComponent} from './list/list.component';
import {EditComponent} from './edit/edit.component';
import {LicenseService} from "./license.service";
import {ConfirmationComponent} from './confirmation/confirmation.component';
import {ConfirmationService} from "./confirmation.service";
import {HttpHeadersInterceptor} from "./http-config.service";

const appRoutes: Routes = [
  {path: '', component: ListComponent},
  {path: 'edit', component: EditComponent},
  {path: 'edit/id/:id', component: EditComponent},
  {path: 'confirm', component: ConfirmationComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    ListComponent,
    EditComponent,
    ConfirmationComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    LicenseService,
    ConfirmationService,
    HttpHeadersInterceptor
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
