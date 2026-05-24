import { Component, ViewChild } from '@angular/core';
import { UrlFormComponent } from './components/url-form/url-form.component';
import { UrlListComponent } from './components/url-list/url-list.component';

@Component({
  selector: 'app-url-shortener',
  standalone: true,
  imports: [UrlFormComponent, UrlListComponent],
  template: `
    <div class="max-w-4xl mx-auto p-6 space-y-8">
      <header class="text-center">
        <h1 class="text-3xl font-bold text-gray-900">URL Shortener</h1>
        <p class="mt-2 text-gray-600">Paste a long URL and get a short, shareable link</p>
      </header>
      
      <section class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-semibold mb-4">Create Short URL</h2>
        <app-url-form (created)="onUrlCreated($event)" />
      </section>
      
      <section class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-semibold mb-4">Your URLs</h2>
        <app-url-list />
      </section>
    </div>
  `,
})
export class UrlShortenerComponent {
  @ViewChild(UrlListComponent) urlList!: UrlListComponent;

  onUrlCreated(shortUrl: string): void {
    if (this.urlList) {
      this.urlList.loadUrls();
    }
  }
}
