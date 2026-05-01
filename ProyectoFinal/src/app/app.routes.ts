import { Routes } from '@angular/router';
import { Login }   from './login/login';
import { Home }    from './home/home';
import { History } from './history/history';
import { Admin }   from './admin/admin';

export const routes: Routes = [
  { path: '',        redirectTo: 'login', pathMatch: 'full' },
  { path: 'login',   component: Login },
  { path: 'home',    component: Home },
  { path: 'history', component: History },
  { path: 'admin',   component: Admin },
  { path: '**',      redirectTo: 'login' },
];
