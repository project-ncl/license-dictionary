import {Injectable, Injector} from '@angular/core';

@Injectable()
export class RestConfigService {

  private static REST_V1: string = "/rest/v1";

  public static CONFIG_ENDPOINT        : string = RestConfigService.REST_V1 + "/config";
  public static EXPORT_ENDPOINT        : string = RestConfigService.REST_V1 + "/export";
  public static IMPORT_ENDPOINT        : string = RestConfigService.REST_V1 + "/import";
  public static LICENSE_STATUS_ENDPOINT: string = RestConfigService.REST_V1 + "/license-status";
  public static LICENSE_ENDPOINT       : string = RestConfigService.REST_V1 + "/license";
  
  public static IMPORT_ENDPOINT_IMPORT_LICENSE_API: string = RestConfigService.IMPORT_ENDPOINT + "/licenses";
  public static IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API: string = RestConfigService.IMPORT_ENDPOINT + "/licenses-alias";
  
}
