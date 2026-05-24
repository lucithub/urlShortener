import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'shorten', pathMatch: 'full' },
  {
    path: 'shorten',
    loadComponent: () =>
      import('./features/url-shortener/url-shortener.component').then(
        (m) => m.UrlShortenerComponent
      ),
  },
  { path: '**', redirectTo: 'shorten' },
];
