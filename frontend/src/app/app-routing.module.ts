import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChatPageComponent } from './chat/chat-page/chat-page.component';
import { ChatPageGuard } from './guards/chat-page.guard';
import { LoginPageGuard } from './guards/login-page.guard';
import { LoginPageComponent } from './login/login-page/login-page.component';

const routes: Routes = [
  { path: 'chat', component: ChatPageComponent, canActivate: [ChatPageGuard] },
  { path: '**', component: LoginPageComponent, canActivate: [LoginPageGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
