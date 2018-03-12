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