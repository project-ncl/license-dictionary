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

import {Injectable, OnInit} from '@angular/core';
import * as Keycloak from "keycloak-js";
import {Router} from "@angular/router";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/observable/fromPromise';

import {RestConfigService} from "./rest-config.service";

@Injectable()
export class AuthService implements OnInit {
    private keycloak;
    private initializationObservable: Observable<Boolean>;

    constructor(private router: Router) {
        console.log("creating auth service");
        this.keycloak = Keycloak(RestConfigService.CONFIG_ENDPOINT + `/keycloak-config`);
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
