import { TestBed, inject } from '@angular/core/testing';

import { HttpHeadersInterceptor } from './http-config.service';

describe('HttpConfigService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [HttpHeadersInterceptor]
    });
  });

  it('should be created', inject([HttpHeadersInterceptor], (service: HttpHeadersInterceptor) => {
    expect(service).toBeTruthy();
  }));
});
