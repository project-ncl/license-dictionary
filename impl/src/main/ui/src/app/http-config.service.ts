import { Injectable } from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import {Observable} from "rxjs/Observable";

@Injectable()
export class HttpHeadersInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("enhancing req: ", req);
    const jsonReq = req.clone({headers: req.headers.set('Accept', 'application/json').set('Content-Type', 'application/json')});
    console.log("enhanced req: ", jsonReq);
    return next.handle(jsonReq);
  }
}
