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


@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    
    notificationService: NotificationService;

    constructor(inj: Injector) {
        this.notificationService = inj.get(NotificationService);
    }
    
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        console.log('Intercepting Request:', request);

        return next.handle(request)
            .catch((err: HttpErrorResponse) => {

                if (err.error instanceof Error) {
                    // A client-side or network error occurred. Handle it accordingly.
                    console.error('An error occurred:', err.error.message);
                    this.notificationService.error('An error occurred: '+ err.error.message);
                } else {
                    // The backend returned an unsuccessful response code.
                    // The response body may contain clues as to what went wrong,
                    console.error(`Backend returned code ${err.status}, body was: ${err.error}`);
                    this.notificationService.error(`Backend returned code ${err.status}, body was: ${err.error}`);
                }

                // return an empty observable
                return Observable.empty<HttpEvent<any>>();
            });
    }
}