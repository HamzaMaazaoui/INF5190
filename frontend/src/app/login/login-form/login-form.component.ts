import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css'],
})
export class LoginFormComponent implements OnInit {
  loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  showError: boolean = false;

  @Output()
  login = new EventEmitter<{ username: string; password: string }>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {}

  onLogin() {
    if (
      this.loginForm.valid &&
      this.loginForm.value.username &&
      this.loginForm.value.password
    ) {
      this.showError = false;
      this.login.emit({
        username: this.loginForm.value.username,
        password: this.loginForm.value.password,
      });
    } else {
      this.showError = true;
    }
  }
}
