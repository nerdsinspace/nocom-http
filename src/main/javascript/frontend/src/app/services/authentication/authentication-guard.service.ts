import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanLoad,
  Route,
  Router,
  RouterStateSnapshot,
  UrlSegment
} from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuardService implements CanActivate, CanLoad {
  constructor(private auth: AuthenticationService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const authorized = this.auth.isLoggedIn();

    if (!authorized) {
      this.router.navigateByUrl('/login');
      return false;
    }

    return authorized;
  }

  canLoad(route: Route, segments: UrlSegment[]) {
    return undefined;
  }
}
