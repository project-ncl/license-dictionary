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

import { Directive } from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import { AsyncValidatorFn, AsyncValidator, NG_ASYNC_VALIDATORS, AbstractControl, ValidationErrors } from '@angular/forms';
import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/map';
import { of } from "rxjs/observable/of";
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/observable/timer';
import 'rxjs/add/operator/catch';

import {LicenseService} from "../license.service";
import {RestConfigService} from "../rest-config.service";


export function existingLicenseCodeValidator(licenseService: LicenseService): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable < ValidationErrors | null > => {

        // Do not run the validation if the control is pristine 
        // (e.g. when editing no initial validation is needed if
        // there are no changes done yet on the form)
        if (control.pristine) {
            return of({});
        }

        let currentValue = control.value;

        // If value is empty, no validation is required  
        if (!currentValue || currentValue.trim() == '') {
            return of({});
        }

        // Get the original value (before the editing); if it's the same
        // no validation is required (or same value would be reported as already existing)
        //console.log('control.parent.controls: ', control.parent.controls);
        //console.log('control.parent.get(licenseCodeSafeCopy): ', control.parent.get('licenseCodeSafeCopy'));
        let originalInput = control.parent.get('licenseCodeSafeCopy');
        if (originalInput && originalInput.value && currentValue === originalInput.value.trim()) {
            return of({});
        }

        return Observable.timer(RestConfigService.DEBOUNCE_TIME_MS).switchMap(() => {
            return licenseService.findLicensesByCode(currentValue.trim(), 1, 0).map(
                licenses => {
                    return (licenses.totalCount > 0) ? { "licenseCodeExists": true } : null;
                }
            ).catch(function(e) {

                // If status is 404 (Not Found), don't consider it as an error or the validator
                // will mark as not valid any non existent code
                if (e instanceof HttpErrorResponse &&
                    e.status == RestConfigService.HTTP_STATUS_CODE_NOT_FOUND) {

                    return of({});
                }
                throw e;
            });
        });
    };
}


@Directive({
    selector: '[licenseCodeExists][formControlName],[licenseCodeExists][ngModel],[licenseCodeExists][formControl]',
    providers: [{ provide: NG_ASYNC_VALIDATORS, useExisting: ExistingLicenseCodeValidatorDirective, multi: true }]
})
export class ExistingLicenseCodeValidatorDirective implements AsyncValidator {
    constructor(private licenseService: LicenseService) { }

    validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable < ValidationErrors | null > {
        return existingLicenseCodeValidator(this.licenseService)(control);
    }
} 