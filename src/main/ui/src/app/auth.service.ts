import {Injectable, OnInit} from '@angular/core';
import * as Keycloak from "keycloak-js";
import {Router} from "@angular/router";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/observable/fromPromise';

@Injectable()
export class AuthService implements OnInit {
  private keycloak;
  private initializationObservable: Observable<Boolean>;

  constructor(private router: Router) {
    console.log("creating auth service");
    this.keycloak = Keycloak("/rest/config/keycloak-config");
    console.log("created keycloak");
    let promise = new Promise<Boolean>((resolve, reject) => {
      this.keycloak.init()
        .success(result => resolve(result))
        .error(error => reject(error))
    });
    this.initializationObservable = Observable.fromPromise(promise);
  }

  ngOnInit(): void {

  }

  logIn() {
    this.keycloak.login()
      .success(() => null)
      .error(() => null);// todo: unauthorized page ?
  }

  logOut() {
    this.keycloak.logout();
  }

  isAuthenticated() {
    return this.keycloak.authenticated;
  }

  getToken() {
    return this.keycloak.token;
  }

  assureLoggedIn() {
    console.log("checking if logged in");
    this.initializationObservable.subscribe(() => {
        if (!this.isAuthenticated()) {
          this.keycloak.login()
            .success(() => null)
            .error(e => {
              console.log("error", e);
              this.router.navigate(["/"])
            })  // todo unauthorized page
        }
      }
    );
  }
}
