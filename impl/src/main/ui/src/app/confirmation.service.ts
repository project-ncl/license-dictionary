import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable()
export class ConfirmationService {
  question: string;
  confirmAction: () => void;
  cancelAction: () => void;
  returnView: any[];

  constructor(private router: Router) {
  }

  init(question: string,
       confirmAction: () => void,
       cancelAction: () => void,
       returnView: any[]) {
    this.question = question;
    this.confirmAction = confirmAction;
    this.cancelAction = cancelAction;
    this.returnView = returnView;
    this.router.navigate(["/confirm"])
  }

  getQuestion() {
    return this.question;
  }

  confirm() {
    this.confirmAction();
    this.router.navigate(this.returnView);
  }

  cancel() {
    this.cancelAction();
    this.router.navigate(this.returnView);
  }
}
