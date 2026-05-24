import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { UrlFormComponent } from './url-form.component';
import { UrlService } from '../../../../services/url.service';
import { ToastService } from '../../../../services/toast.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('UrlFormComponent', () => {
  let component: UrlFormComponent;
  let fixture: ComponentFixture<UrlFormComponent>;
  let urlServiceSpy: any;
  let toastServiceSpy: any;

  const mockUrlResponse = {
    code: 'abc123',
    originalUrl: 'https://example.com',
    shortUrl: 'http://localhost:8080/abc123',
    hitCount: 0,
    createdAt: new Date().toISOString(),
    expired: false
  };

  beforeEach(async () => {
    urlServiceSpy = {
      shortenUrl: vi.fn().mockReturnValue(of(mockUrlResponse))
    };

    toastServiceSpy = {
      success: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [UrlFormComponent, ReactiveFormsModule],
      providers: [
        { provide: UrlService, useValue: urlServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UrlFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('should_create_component', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('should_show_validation_error_when_url_is_empty', () => {
    it('should show validation error when URL is empty and form is submitted', () => {
      // Set empty URL
      component.form.patchValue({ url: '' });
      component.form.markAsTouched();

      // Trigger submit
      component.onSubmit();

      // The Form should be invalid
      expect(component.form.invalid).toBe(true);
      expect(component.form.get('url')?.hasError('required')).toBe(true);
      expect(component.getUrlError()).toBe('URL is required');
    });
  });

  describe('should_show_validation_error_when_url_is_invalid', () => {
    it('should show validation error when URL pattern is invalid', () => {
      // Set invalid URL (missing http/https)
      component.form.patchValue({ url: 'not-a-valid-url' });
      component.form.markAsTouched();

      // Trigger submit
      component.onSubmit();

      // Form should be invalid due to pattern
      expect(component.form.invalid).toBe(true);
      expect(component.form.get('url')?.hasError('pattern')).toBe(true);
      expect(component.getUrlError()).toBe('URL must start with http:// or https://');
    });
  });

  describe('should_accept_valid_url', () => {
    it('should accept valid URL with proper scheme', () => {
      // Set valid URL
      component.form.patchValue({ url: 'https://example.com' });

      // Form should be valid
      expect(component.form.valid).toBe(true);
    });

    it('should accept URL with http scheme', () => {
      component.form.patchValue({ url: 'http://example.com' });
      expect(component.form.valid).toBe(true);
    });
  });

  describe('should_emit_created_event_on_success', () => {
    it('should emit created event with short URL on successful shortenUrl', () => {
      const emitSpy = vi.spyOn(component.created, 'emit');
      component.form.patchValue({ url: 'https://example.com' });

      component.onSubmit();

      expect(urlServiceSpy.shortenUrl).toHaveBeenCalledWith({
        url: 'https://example.com',
        customAlias: undefined,
        expirationDays: undefined
      });
      expect(toastServiceSpy.success).toHaveBeenCalledWith('URL shortened successfully!');
      expect(emitSpy).toHaveBeenCalledWith(mockUrlResponse.shortUrl);
    });
  });

  describe('should_reset_form_after_submission', () => {
    it('should reset form after successful submission', () => {
      component.form.patchValue({
        url: 'https://example.com',
        customAlias: 'myalias',
        expirationDays: 30
      });

      component.onSubmit();

      // Form should be reset
      expect(component.form.value.url).toBe('');
      expect(component.form.value.customAlias).toBe('');
      expect(component.form.value.expirationDays).toBeNull();
    });
  });

  describe('should_handle_custom_alias', () => {
    it('should send custom alias when provided', () => {
      component.form.patchValue({
        url: 'https://example.com',
        customAlias: 'my-custom-alias'
      });

      component.onSubmit();

      expect(urlServiceSpy.shortenUrl).toHaveBeenCalledWith({
        url: 'https://example.com',
        customAlias: 'my-custom-alias',
        expirationDays: undefined
      });
    });

    it('should validate custom alias pattern', () => {
      component.form.patchValue({ customAlias: 'ab' }); // Too short
      expect(component.form.get('customAlias')?.hasError('pattern')).toBe(true);

      component.form.patchValue({ customAlias: 'valid-alias' }); // Valid
      expect(component.form.get('customAlias')?.hasError('pattern')).toBe(false);
    });
  });

  describe('should_handle_expiration_days', () => {
    it('should send expiration days when provided', () => {
      component.form.patchValue({
        url: 'https://example.com',
        expirationDays: 30
      });

      component.onSubmit();

      expect(urlServiceSpy.shortenUrl).toHaveBeenCalledWith({
        url: 'https://example.com',
        customAlias: undefined,
        expirationDays: 30
      });
    });
  });
});
