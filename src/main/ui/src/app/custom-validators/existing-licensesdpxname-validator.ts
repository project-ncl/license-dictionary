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

export function existingLicenseSpdxNameValidator(licenseService: LicenseService): AsyncValidatorFn {
    return (control: AbstractControl): Promise<ValidationErrors | null> | Observable < ValidationErrors | null > => {

        if (control.value == null || control.value.trim() == '') {
            return of({});
        }

        return Observable.timer(RestConfigService.DEBOUNCE_TIME_MS).switchMap(() => {
            return licenseService.findLicensesBySpdxName(control.value.trim(), 1, 0).map(
                licenses => {
                    return (licenses.totalCount > 0) ? { "licenseSpdxNameExists": true } : null;
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
    selector: '[licenseSpdxNameExists][formControlName],[licenseSpdxNameExists][formControl],[licenseSpdxNameExists][ngModel]',
    providers: [{ provide: NG_ASYNC_VALIDATORS, useExisting: ExistingLicenseSpdxNameValidatorDirective, multi: true }]
})
export class ExistingLicenseSpdxNameValidatorDirective implements AsyncValidator {
    constructor(private licenseService: LicenseService) { }

    validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable < ValidationErrors | null > {
        return existingLicenseSpdxNameValidator(this.licenseService)(control);
    }
} 