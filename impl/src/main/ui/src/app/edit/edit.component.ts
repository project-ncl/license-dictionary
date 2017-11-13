import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {License, LicenseService} from "../license.service";

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class EditComponent implements OnInit {
  license: License;
  newAlias: string;
  newUrlAlias: string;
  id: number;

  errorMessage: string;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private licenseService: LicenseService) {
  }

  // loadToEdit()

  ngOnInit() {
    this.route.url.subscribe((segments: UrlSegment[]) => {
      this.license = {
        id: null,
        name: '',
        url: '',
        textUrl: '',
        content: '',
        urlAliases: [],
        nameAliases: []
      };
      if (segments[1] && segments[1].toString() === 'id' && segments[2]) {
        console.log("an id was passed");
        this.id = Number(segments[2]);
        this.licenseService.getLicense(this.id).subscribe(license => this.license = license);
      }
    });
  }

  addAlias() {
    if (this.newAlias) {
      this.license.nameAliases.push(this.newAlias);
      this.newAlias = '';
    }
  }

  removeAlias(alias: string) {
    this.license.nameAliases = this.license.nameAliases.filter(a => a != alias)
  }

  addUrlAlias() {
    if (this.newUrlAlias) {
      this.license.urlAliases.push(this.newUrlAlias);
      this.newUrlAlias = '';
    }
  }

  removeUrlAlias(urlAlias: string) {
    this.license.urlAliases = this.license.urlAliases.filter(a => a != urlAlias)
  }

  saveLicense() {
    if (this.id) {
      this.licenseService.updateLicense(this.id, this.license).subscribe(
        license =>
          this.router.navigate(["/"]),
        error => {
          console.log("error", error);
          this.errorMessage = error
        }
      );
    } else {
      this.licenseService.addLicense(this.license).subscribe(
        license =>
          this.router.navigate(["/"]),
        error => this.errorMessage = error
      );
    }
  }
}
