import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {EmptyLicense, License, LicenseService} from "../license.service";
import {ConfirmationService} from "../confirmation.service";
import {Location} from "@angular/common";

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ViewComponent implements OnInit {

  license: License = new EmptyLicense();

  constructor(private route: ActivatedRoute,
              private location: Location,
              private confirmationService: ConfirmationService,
              private licenseService: LicenseService,
              private router: Router) {
  }

  ngOnInit() {
    this.route.url.subscribe((segments: UrlSegment[]) => {
      if (segments[1]) {
        console.log("an id was passed");
        let id = Number(segments[1]);
        this.licenseService.getLicense(id).subscribe(license => this.license = license);
      }
    });
  }

  remove = id => {
    let path = this.location.path();
    this.licenseService.getLicense(id)
      .subscribe(license => {
          this.confirmationService.init(
            "Remove license [" + license.id + "] '" + license.name + "'?",
            () =>
              this.licenseService.removeLicense(id).subscribe(
                success => console.log("successful removal", success),
                error => console.log("erroneous removal", error)
              ),
            () => {
              console.log("canceled removing license : " + license.id);
              this.router.navigate([path]);
            },
          );
        }
      );
  };

  edit = id => {
    this.router.navigate(["/edit", id])
  }

}
