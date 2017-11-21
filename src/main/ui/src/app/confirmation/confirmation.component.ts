import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ConfirmationService} from "../confirmation.service";

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ConfirmationComponent implements OnInit {
  question: string;

  constructor(private confirmationService: ConfirmationService) {
    this.question = confirmationService.getQuestion();
  }

  ngOnInit() {
  }

  confirm() {
    this.confirmationService.confirm();
  }

  cancel() {
    this.confirmationService.cancel();
  }

}
