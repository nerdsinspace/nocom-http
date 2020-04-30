import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { User } from '../../models/user';
import { HttpError } from '../../models/http-error';
import { Router } from '@angular/router';
import { JsUtils } from '../../models/js-utils';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private jwtHelper = new JwtHelperService();
  private authenticatedUser: User;
  private checkedStorage = false;

  constructor(private http: HttpClient, private router: Router) {
  }

  public login(user: string, pass: string) {
    return this.http.post(`${environment.apiUrl}/login`, {}, {
      params: {
        username: user,
        password: pass
      }
    }).pipe(map(res => this.parseSuccessResponse(res)));
  }

  public logout() {
    this.deleteSession();
    this.authenticatedUser = null;
  }

  public get user(): User {
    return this.authenticatedUser;
  }

  public isAuthorized(): boolean {
    return this.user != null && this.user.isAuthorized();
  }

  private getBearerAuthorizationHeader(token: string): string {
    return 'Bearer ' + token;
  }

  public getHeaderToken(): string {
    return this.getBearerAuthorizationHeader(this.user.accessToken);
  }

  public isLoggedIn(): boolean {
    return this.user != null;
  }

  private parseSuccessResponse(response: any): User {
    this.authenticatedUser = User.import(response);
    this.saveSession();
    return this.authenticatedUser;
  }

  private parseFailureResponse(response: any): HttpError {
    return new HttpError(response.error.message, response.error.statusCode);
  }

  private saveSession() {
    if (this.isLoggedIn()) {
      localStorage.setItem('JWT_TOKEN', this.user.accessToken);
    } else {
      this.deleteSession();
    }
  }

  private deleteSession() {
    localStorage.removeItem('JWT_TOKEN');
  }

  public loadSession(authenticated?: any, unauthenticated?: any) {
    // don't need to load session if already logged in
    if (this.isLoggedIn()) {
      JsUtils.call(authenticated);
      return;
    }

    // if this is triggered, the user hasn't logged in yet
    // but reloaded the page
    if (this.checkedStorage) {
      JsUtils.call(unauthenticated);
      return;
    }
    // only try loading session once
    this.checkedStorage = true;

    // load the authenticated user
    const token = localStorage.getItem('JWT_TOKEN');
    if (token == null) {
      JsUtils.call(unauthenticated);
      return;
    }

    const decoded = this.jwtHelper.decodeToken(token);

    this.authenticatedUser = User.import({
      username: decoded.sub,
      level: decoded.lvl,
      accessToken: token
    });

    // validate that this token is still valid
    this.http.post(`${environment.apiUrl}/validate`, {}, {
      headers: new HttpHeaders({
        Authorization: this.getHeaderToken()
      })
    }).subscribe(
      success => {
        this.parseSuccessResponse(success);
        JsUtils.call(authenticated);
      },
      error => {
        this.deleteSession();
        JsUtils.call(unauthenticated);
      }
    );
  }

  // public register(user: string, pass: string, lvl: number) {
  //   return this.http.post(`${environment.apiUrl}/user/register`, {}, {
  //     params: {
  //       username: user,
  //       password: pass,
  //       level: lvl.toString()
  //     }
  //   });
  // }
  //
  // public unregister(user: string) {
  //   return this.http.get(`${environment.apiUrl}/user/unregister/${user}`);
  // }
  //
  // public getUsernames() {
  //   return this.http.get(`${environment.apiUrl}/user/registered`);
  // }
  //
  // public setPassword(user: string, pass: string) {
  //   return this.http.post(`${environment.apiUrl}/user/set/password/${user}`, null, {
  //     params: {
  //       password: pass
  //     }
  //   });
  // }
  //
  // public setEnabled(user: string, enable: boolean) {
  //   return this.http.post(`${environment.apiUrl}/user/set/enabled/${user}`, null, {
  //     params: {
  //       enabled: enable ? 'true' : 'false',
  //     }
  //   });
  // }
  //
  // public setLevel(user: string, lvl: number) {
  //   return this.http.post(`${environment.apiUrl}/user/set/level/${user}`, null, {
  //     params: {
  //       level: lvl.toString(),
  //     }
  //   });
  // }
}
