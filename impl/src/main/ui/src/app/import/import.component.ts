import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ImportComponent implements OnInit {
  successMessage;
  errorMessage;
  fileToUpload;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
  }

  updateFile($event) {
    console.log("update file event: ", $event);
    this.fileToUpload = $event.target.files[0];
  }

  importLicenses() {
    this.successMessage = null;
    this.errorMessage = null;

    let reader = new FileReader();
    let upload = this.upload;
    let component = this;

    reader.onloadend = function (file: any) {
      let contents = file.target.result;
      upload(contents, component);
    };

    reader.readAsText(this.fileToUpload);
  }

  upload(content, component) {
    component.http.post('/rest/import/licenses', content).subscribe(
      result => {
        component.successMessage = "Successfully imported licenses";
      },
      error => {
        console.log("error", error);
        component.errorMessage = `Failed to import licenses. ${error.message}, ${error.error}`
      }
    )
  }

}
