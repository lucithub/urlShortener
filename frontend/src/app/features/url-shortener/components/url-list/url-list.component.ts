import { Component, inject, OnInit } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { UrlService } from '../../../../services/url.service';
import { ToastService } from '../../../../services/toast.service';
import { UrlResponse } from '../../../../models/url.model';

@Component({
  selector: 'app-url-list',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './url-list.component.html',
  styleUrl: './url-list.component.css'
})
export class UrlListComponent implements OnInit {
  private urlService = inject(UrlService);
  private toastService = inject(ToastService);
  
  urls: UrlResponse[] = [];
  loading = false;
  
  ngOnInit(): void {
    this.loadUrls();
  }
  
  loadUrls(): void {
    this.loading = true;
    this.urlService.getAllUrls().subscribe({
      next: (urls) => {
        this.urls = urls;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
  
  copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      this.toastService.success('Copied to clipboard!');
    });
  }
  
  deleteUrl(code: string): void {
    if (!confirm('Are you sure you want to delete this URL?')) {
      return;
    }
    
    this.urlService.deleteUrl(code).subscribe({
      next: () => {
        this.urls = this.urls.filter(u => u.code !== code);
        this.toastService.success('URL deleted');
      }
    });
  }
  
  isExpired(expiresAt: string | undefined): boolean {
    if (!expiresAt) return false;
    return new Date(expiresAt) < new Date();
  }
}
