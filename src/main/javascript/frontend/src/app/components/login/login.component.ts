import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../services/authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username: string;
  password: string;

  errorMessage: string;
  isErrored = false;

  constructor(private auth: AuthenticationService, private router: Router) {
    auth.loadSession(() => router.navigateByUrl('/'));
  }

  ngOnInit(): void {
  }

  login() {
    this.auth.login(this.username, this.password).subscribe(
      success => this.router.navigateByUrl('/'),
      error => {}
    );
  }
}
