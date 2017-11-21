import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {License, LicenseService} from "../license.service";
import {ConfirmationService} from "../confirmation.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ListComponent implements OnInit {

  licenses: License[] = [];

  searchTerm: string;

  constructor(private licenseService: LicenseService,
              private confirmationService: ConfirmationService,
              private router: Router) {
    licenseService.getLicenses()
      .subscribe(licenses => this.licenses = licenses);
  }

  ngOnInit() {
  }

  searchForLicenses() {
    console.log("search term: ", this.searchTerm);
    this.licenseService.findLicenses(this.searchTerm)
      .subscribe(licenses => this.licenses = licenses);
  }

  view = id => {
    this.router.navigate(["/view", "id", id])
  }

}
