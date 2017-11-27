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
