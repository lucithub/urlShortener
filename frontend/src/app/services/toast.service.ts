import { Injectable, signal } from '@angular/core';

export interface Toast {
  message: string;
  type: 'success' | 'error' | 'info';
  visible: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts = signal<Toast[]>([]);

  show(message: string, type: Toast['type'] = 'info', duration = 3000): void {
    const toast: Toast = { message, type, visible: true };
    this.toasts.update(t => [...t, toast]);
    
    setTimeout(() => this.dismiss(toast), duration);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error', 5000);
  }

  private dismiss(toast: Toast): void {
    this.toasts.update(t => t.filter(x => x !== toast));
  }
}