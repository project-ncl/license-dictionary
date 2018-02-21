import {Injectable, Injector} from '@angular/core';

@Injectable()
export class RestConfigService {

    private static REST_V1: string = "/rest/v1";

    public static CONFIG_ENDPOINT: string = RestConfigService.REST_V1 + "/config";
    public static EXPORT_ENDPOINT: string = RestConfigService.REST_V1 + "/export";
    public static IMPORT_ENDPOINT: string = RestConfigService.REST_V1 + "/import";
    public static LICENSE_STATUS_ENDPOINT: string = RestConfigService.REST_V1 + "/license-status";
    public static LICENSE_ENDPOINT: string = RestConfigService.REST_V1 + "/license";

    public static IMPORT_ENDPOINT_IMPORT_LICENSE_API: string = RestConfigService.IMPORT_ENDPOINT + "/licenses";
    public static IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API: string = RestConfigService.IMPORT_ENDPOINT + "/licenses-alias";

    public static DEBOUNCE_TIME_MS: number = 500;

    public static HTTP_STATUS_CODE_ACCEPTED                : number = 202;
    public static HTTP_STATUS_CODE_BAD_REQUEST             : number = 400;
    public static HTTP_STATUS_CODE_CONFLICT                : number = 409;
    public static HTTP_STATUS_CODE_CREATED                 : number = 201;
    public static HTTP_STATUS_CODE_FORBIDDEN               : number = 403;
    public static HTTP_STATUS_CODE_GONE                    : number = 410;
    public static HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR   : number = 500;
    public static HTTP_STATUS_CODE_MOVED_PERMANENTLY       : number = 301;
    public static HTTP_STATUS_CODE_NO_CONTENT              : number = 204;
    public static HTTP_STATUS_CODE_NOT_ACCEPTABLE          : number = 406;
    public static HTTP_STATUS_CODE_NOT_FOUND               : number = 404;
    public static HTTP_STATUS_CODE_NOT_MODIFIED            : number = 304;
    public static HTTP_STATUS_CODE_OK                      : number = 200;
    public static HTTP_STATUS_CODE_PRECONDITION_FAILED     : number = 412;
    public static HTTP_STATUS_CODE_SEE_OTHER               : number = 303;
    public static HTTP_STATUS_CODE_SERVICE_UNAVAILABLE     : number = 503;
    public static HTTP_STATUS_CODE_TEMPORARY_REDIRECT      : number = 307;
    public static HTTP_STATUS_CODE_UNAUTHORIZED            : number = 401;
    public static HTTP_STATUS_CODE_UNSUPPORTED_MEDIA_TYPE  : number = 415;

}
