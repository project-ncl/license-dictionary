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

import { Injectable, Injector } from '@angular/core';
import {
    HttpInterceptor,
    HttpRequest,
    HttpResponse,
    HttpErrorResponse,
    HttpHandler,
    HttpEvent
} from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/empty';

import { NotificationService } from './notification/notification.service';
import { LoaderService } from './loader/loader.service';


@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    
    notificationService: NotificationService;
    loaderService: LoaderService;

    constructor(inj: Injector) {
        this.notificationService = inj.get(NotificationService);
        this.loaderService = inj.get(LoaderService);
    }
    
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        console.log('Intercepting Request:', request);

        return next.handle(request)
            .catch((err: HttpErrorResponse) => {

                if (err.error instanceof Error) {
                    // A client-side or network error occurred. Handle it accordingly.
                    console.error('An error occurred:', err.error.message);
                    this.notificationService.error('An error occurred: '+ err.error.message);
                    this.loaderService.hide();
                } else {
                    // The backend returned an unsuccessful response code.
                    // The response body may contain clues as to what went wrong,
                    console.error(`Backend returned code ${err.status}, body was: ${err.error}`);
                    this.notificationService.error(`Backend returned code ${err.status}, body was: ${err.error}`);
                    this.loaderService.hide();
                }

                // return an empty observable
                return Observable.empty<HttpEvent<any>>();
            });
    }
}
