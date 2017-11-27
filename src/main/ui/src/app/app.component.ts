import {Component, OnInit} from '@angular/core';
import {AuthService} from "./auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {
  }

  ngOnInit() {
  }

  logIn() {
    this.auth.logIn();
  }

  logOut() {
    this.auth.logOut();
  }

  isAuthenticated() {
    return this.auth.isAuthenticated();
  }
}
