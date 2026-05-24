import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ToastService} from "../../../services/toast.service";

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent {
  toastService = inject(ToastService);

  getIcon(type: 'success' | 'error' | 'info'): string {
    switch (type) {
      case 'success':
        return '✓';
      case 'error':
        return '✕';
      case 'info':
        return 'ℹ';
    }
  }
}