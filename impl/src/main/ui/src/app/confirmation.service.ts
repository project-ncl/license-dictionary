import {Injectable} from '@angular/core';
import {Router} from "@angular/router";

@Injectable()
export class ConfirmationService {
  question: string;
  confirmAction: () => void;
  cancelAction: () => void;

  constructor(private router: Router) {
  }

  init(question: string,
       confirmAction: () => void,
       cancelAction: () => void) {
    this.question = question;
    this.confirmAction = confirmAction;
    this.cancelAction = cancelAction;
    this.router.navigate(["/confirm"])
  }

  getQuestion() {
    return this.question;
  }

  confirm() {
    this.confirmAction();
  }

  cancel() {
    this.cancelAction();
  }
}
