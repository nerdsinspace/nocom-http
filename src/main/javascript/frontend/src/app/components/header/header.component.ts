import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(public auth: AuthenticationService, private router: Router) { }

  ngOnInit(): void {
  }

  doLogout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
