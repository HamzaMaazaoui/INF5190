import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css'],
})
export class LoginPageComponent implements OnInit {
  constructor(private router: Router, private loginService: LoginService) {}

  errorMessage: string | null = null;

  ngOnInit(): void {}

  async onLogin(login: { username: string; password: string }) {
    this.errorMessage = null;
    try {
      await this.loginService.login(login);
      this.router.navigate(['/chat']);
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 403) {
        this.errorMessage = 'Mot de passe invalide.';
      } else {
        this.errorMessage = 'Problème de connexion.';
      }
    }
  }
}
