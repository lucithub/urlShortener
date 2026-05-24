import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastComponent } from './toast.component';
import { ToastService } from '../../services/toast.service';

describe('ToastComponent', () => {
  let component: ToastComponent;
  let fixture: ComponentFixture<ToastComponent>;
  let toastService: ToastService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastComponent],
      providers: [ToastService]
    }).compileComponents();

    toastService = TestBed.inject(ToastService);
    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('should_create_component', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('should_display_toasts', () => {
    it('should display toast messages from ToastService', () => {
      toastService.success('Test success message');
      fixture.detectChanges();

      // Access the toasts signal
      const toasts = toastService.toasts();
      expect(toasts.length).toBeGreaterThan(0);
      expect(toasts[0].message).toBe('Test success message');
      expect(toasts[0].type).toBe('success');
    });

    it('should display multiple toasts', () => {
      toastService.success('Success message');
      toastService.error('Error message');
      toastService.info('Info message');
      fixture.detectChanges();

      const toasts = toastService.toasts();
      expect(toasts.length).toBe(3);
    });

    it('should display error toast with correct type', () => {
      toastService.error('Error message');
      fixture.detectChanges();

      const toasts = toastService.toasts();
      expect(toasts[0].type).toBe('error');
    });

    it('should display info toast with correct type', () => {
      toastService.show('Info message', 'info');
      fixture.detectChanges();

      const toasts = toastService.toasts();
      expect(toasts[0].type).toBe('info');
    });
  });

  describe('should_not_display_when_empty', () => {
    it('should not display when no toasts are present', () => {
      const toasts = toastService.toasts();
      expect(toasts.length).toBe(0);
    });
  });

  describe('getIcon', () => {
    it('should return correct icon for success type', () => {
      expect(component.getIcon('success')).toBe('✓');
    });

    it('should return correct icon for error type', () => {
      expect(component.getIcon('error')).toBe('✕');
    });

    it('should return correct icon for info type', () => {
      expect(component.getIcon('info')).toBe('ℹ');
    });
  });

  describe('toast_auto_dismiss', () => {
    it('should auto-dismiss toast after default duration', async () => {
      toastService.success('Auto-dismiss test');
      expect(toastService.toasts().length).toBe(1);

      // Wait for auto-dismiss (default is 3000ms)
      await new Promise(resolve => setTimeout(resolve, 3100));

      expect(toastService.toasts().length).toBe(0);
    }, 5000);
  });

  describe('toast_visibility', () => {
    it('should have visible flag set to true for new toasts', () => {
      toastService.success('Visible toast');
      fixture.detectChanges();

      const toasts = toastService.toasts();
      expect(toasts[0].visible).toBe(true);
    });
  });

  describe('toast_message_content', () => {
    it('should store exact message text', () => {
      const message = 'This is my exact message';
      toastService.show(message, 'success');
      fixture.detectChanges();

      const toasts = toastService.toasts();
      expect(toasts[0].message).toBe(message);
    });
  });
});
