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

import { Injectable } from '@angular/core';
import { Dir } from '@angular/cdk/bidi';

import {
    MatSnackBar,
    MatSnackBarConfig,
    MatSnackBarHorizontalPosition,
    MatSnackBarVerticalPosition,
} from '@angular/material';

@Injectable()
export class NotificationService {

    actionButtonLabel: string = 'CLOSE';
    action: boolean = true;
    setAutoHide: boolean = true;
    autoHide: number = 10000;
    addExtraClass: boolean = false;

    horizontalPosition: MatSnackBarHorizontalPosition = 'end'; // start | end | left | right| center 
    verticalPosition: MatSnackBarVerticalPosition = 'top';  // top | bottom

    constructor(public snackBar: MatSnackBar, private dir: Dir) {
    }

    success(message: string) {
        const config = this._createConfig(true, 5000, true, 'custom-success');
        this.snackBar.open(message, this.action ? this.actionButtonLabel : undefined, config);
    }

    error(message: string) {
        const config = this._createConfig(false, 0, true, 'custom-error');
        this.snackBar.open(message, this.action ? this.actionButtonLabel : undefined, config);
    }

    info(message: string) {
        const config = this._createConfig(false, 0, true, 'custom-info');
        this.snackBar.open(message, this.action ? this.actionButtonLabel : undefined, config);
    }

    warning(message: string) {
        const config = this._createConfig(false, 0, true, 'custom-warning');
        this.snackBar.open(message, this.action ? this.actionButtonLabel : undefined, config);
    }

    private _createConfig(autoHide: boolean, duration: number, extraClass: boolean, cssClass: string) {
        const config = new MatSnackBarConfig();
        config.verticalPosition = this.verticalPosition;
        config.horizontalPosition = this.horizontalPosition;
        config.duration = autoHide ? duration : 0;
        config.panelClass = extraClass ? [cssClass] : undefined;
        config.direction = this.dir.value;
        return config;
    }
}