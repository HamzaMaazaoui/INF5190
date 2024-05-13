import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  static USERNAME_KEY = 'username';
  static TOKEN_KEY = 'token';

  private username = new BehaviorSubject<string | null>(null);
  private token: string | null = null;

  constructor(private httpClient: HttpClient) {
    this.username.next(localStorage.getItem(LoginService.USERNAME_KEY));
    this.token = localStorage.getItem(LoginService.TOKEN_KEY);
  }

  async login(login: { username: string; password: string }) {
    const loginResponse = await firstValueFrom(
      this.httpClient.post<{ token: string }>(
        `${environment.backendUrl}/auth/login`,
        {
          username: login.username,
          password: login.password,
        }
      )
    );

    localStorage.setItem(LoginService.USERNAME_KEY, login.username);
    localStorage.setItem(LoginService.TOKEN_KEY, loginResponse.token);
    this.token = loginResponse.token;
    this.username.next(login.username);
  }

  async logout() {
    if (this.token) {
      try {
        await firstValueFrom(
          this.httpClient.post(`${environment.backendUrl}/auth/logout`, {})
        );
      } catch (error) {
        console.warn('Could not logout.');
      }
      localStorage.removeItem(LoginService.USERNAME_KEY);
      localStorage.removeItem(LoginService.TOKEN_KEY);
      this.username.next(null);
      this.token = null;
    }
  }

  getUsername(): Observable<string | null> {
    return this.username.asObservable();
  }

  getToken(): String | null {
    return this.token;
  }
}
