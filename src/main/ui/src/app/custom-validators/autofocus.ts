import { Directive, AfterViewInit, ElementRef } from '@angular/core';


@Directive({
    selector: '[appAutofocus]'
})
export class AutofocusDirective implements AfterViewInit {

    constructor(private el: ElementRef) {
    }

    ngAfterViewInit() {
        console.log('this.el.nativeElement' + JSON.stringify(this.el.nativeElement));
        console.log('this.el.$animate' + this.el.nativeElement.$animate);
        
        this.el.nativeElement.focus();
    }

}