import { JsUtils } from './js-utils';

export class User {
  constructor(private _username: string, private _level: number,
              private _group: string, private _accessToken: string) {
  }

  public get username(): string {
    return this.username;
  }

  public get level(): number {
    return this._level;
  }

  public get group(): string {
    return this._group;
  }

  public get accessToken(): string {
    return this._accessToken;
  }

  public static import(json: any): User {
    return new User(
      JsUtils.coalesce(json.username, json._username),
      JsUtils.coalesce(json.level, json._level),
      JsUtils.coalesce(json.group, json._group),
      JsUtils.coalesce(json.token, json._accessToken, json.accessToken)
    );
  }

  public isRootAuthority(): boolean {
    return this.level >= 100;
  }

  public isAdminAuthority(): boolean {
    return this.level >= 90;
  }

  public isDevAuthority(): boolean {
    return this.level >= 50;
  }

  public isAuthorized(): boolean {
    return this.isDevAuthority();
  }
}
