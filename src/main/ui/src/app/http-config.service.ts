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

import {Injectable, Injector} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from "rxjs/Observable";
import {AuthService} from "./auth.service";

@Injectable()
export class HttpHeadersInterceptor implements HttpInterceptor {
  authService: AuthService;

  constructor(inj: Injector) {
    this.authService = inj.get(AuthService);
  }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("enhancing req: ", req);
    let newHeaders = req.headers
      .set('Accept', 'application/json')
      .set('Content-Type', 'application/json');
    if (this.authService.isAuthenticated()) {
      newHeaders = newHeaders.set("Authorization", "Bearer " + this.authService.getToken());
    }
    let jsonReq = req.clone(
      {
        headers:
        newHeaders
      }
    );
    console.log("enhanced req: ", jsonReq);
    return next.handle(jsonReq);
  }
}
