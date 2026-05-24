import { Component, inject, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UrlService } from '../../../../services/url.service';
import { ToastService } from '../../../../services/toast.service';

@Component({
  selector: 'app-url-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './url-form.component.html',
  styleUrl: './url-form.component.css'
})
export class UrlFormComponent {
  private fb = inject(FormBuilder);
  private urlService = inject(UrlService);
  private toastService = inject(ToastService);
  
  created = output<string>(); // Emits the short URL when created

  form = this.fb.group({
    url: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]],
    customAlias: ['', [Validators.pattern(/^[A-Za-z0-9_-]{3,32}$/)]],
    expirationDays: [null as number | null]
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { url, customAlias, expirationDays } = this.form.value;
    
    this.urlService.shortenUrl({
      url: url!,
      customAlias: customAlias || undefined,
      expirationDays: expirationDays || undefined
    }).subscribe({
      next: (response) => {
        this.toastService.success('URL shortened successfully!');
        this.created.emit(response.shortUrl);
        this.form.reset();
      },
      error: () => {
        // Error handled by interceptor, but we can add extra handling here if needed
      }
    });
  }

  getUrlError(): string {
    const control = this.form.get('url');
    if (control?.hasError('required')) return 'URL is required';
    if (control?.hasError('pattern')) return 'URL must start with http:// or https://';
    return '';
  }
}
