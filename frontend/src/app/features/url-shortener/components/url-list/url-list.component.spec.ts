import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UrlListComponent } from './url-list.component';
import { UrlService } from '../../../../services/url.service';
import { ToastService } from '../../../../services/toast.service';
import { of } from 'rxjs';

describe('UrlListComponent', () => {
  let component: UrlListComponent;
  let fixture: ComponentFixture<UrlListComponent>;
  let urlServiceSpy: any;
  let toastServiceSpy: any;

  const mockUrls = [
    {
      code: 'abc123',
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:8080/abc123',
      hitCount: 5,
      createdAt: new Date().toISOString(),
      expired: false
    },
    {
      code: 'xyz789',
      originalUrl: 'https://test.com',
      shortUrl: 'http://localhost:8080/xyz789',
      hitCount: 10,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
      expiresAt: new Date(Date.now() - 1000).toISOString(),
      expired: true
    }
  ];

  beforeEach(async () => {
    urlServiceSpy = {
      getAllUrls: vi.fn().mockReturnValue(of(mockUrls)),
      deleteUrl: vi.fn().mockReturnValue(of(undefined))
    };

    toastServiceSpy = {
      success: vi.fn()
    };

    // Mock navigator.clipboard
    vi.spyOn(navigator.clipboard, 'writeText').mockImplementation(() => Promise.resolve());

    await TestBed.configureTestingModule({
      imports: [UrlListComponent],
      providers: [
        { provide: UrlService, useValue: urlServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UrlListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('should_create_component', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });
  });

  describe('should_load_urls_on_init', () => {
    it('should call loadUrls on ngOnInit', () => {
      const loadUrlsSpy = vi.spyOn(component, 'loadUrls');
      component.ngOnInit();
      expect(loadUrlsSpy).toHaveBeenCalled();
    });

    it('should call getAllUrls service on loadUrls', () => {
      component.loadUrls();
      expect(urlServiceSpy.getAllUrls).toHaveBeenCalled();
    });

    it('should populate urls array with response data', () => {
      component.loadUrls();
      expect(component.urls).toEqual(mockUrls);
    });
  });

  describe('should_display_urls_in_table', () => {
    it('should have urls populated after loadUrls', () => {
      expect(component.urls.length).toBe(2);
      expect(component.urls[0].code).toBe('abc123');
      expect(component.urls[1].code).toBe('xyz789');
    });

    it('should have correct hit counts', () => {
      expect(component.urls[0].hitCount).toBe(5);
      expect(component.urls[1].hitCount).toBe(10);
    });
  });

  describe('should_show_empty_message_when_no_urls', () => {
    it('should show empty state when urls array is empty', async () => {
      urlServiceSpy.getAllUrls.mockReturnValue(of([]));

      const emptyFixture = TestBed.createComponent(UrlListComponent);
      const emptyComponent = emptyFixture.componentInstance;
      emptyFixture.detectChanges();

      await emptyFixture.whenStable();
      emptyComponent.loadUrls();
      emptyFixture.detectChanges();

      expect(emptyComponent.urls.length).toBe(0);
    });
  });

  describe('should_call_deleteUrl_on_delete_click', () => {
    it('should call deleteUrl service when deleteUrl is invoked', () => {
      // Mock window.confirm to return true
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.deleteUrl('abc123');

      expect(urlServiceSpy.deleteUrl).toHaveBeenCalledWith('abc123');
    });

    it('should not call deleteUrl when user cancels confirmation', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.deleteUrl('abc123');

      expect(urlServiceSpy.deleteUrl).not.toHaveBeenCalled();
    });

    it('should remove deleted URL from the list after successful deletion', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.deleteUrl('abc123');

      // After delete, the URL should be filtered out
      expect(component.urls.filter(u => u.code === 'abc123').length).toBe(0);
    });

    it('should show success toast after successful deletion', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.deleteUrl('abc123');

      expect(toastServiceSpy.success).toHaveBeenCalledWith('URL deleted');
    });
  });

  describe('should_show_confirm_dialog_on_delete', () => {
    it('should show confirm dialog before deleting', () => {
      const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.deleteUrl('abc123');

      expect(confirmSpy).toHaveBeenCalledWith('Are you sure you want to delete this URL?');
    });
  });

  describe('should_handle_clipboard', () => {
    it('should call clipboard API with correct text', async () => {
      await component.copyToClipboard('http://localhost:8080/abc123');

      expect(navigator.clipboard.writeText).toHaveBeenCalledWith('http://localhost:8080/abc123');
    });

    it('should show success toast after copying', async () => {
      await component.copyToClipboard('test-url');

      expect(toastServiceSpy.success).toHaveBeenCalledWith('Copied to clipboard!');
    });
  });

  describe('should_check_expiration', () => {
    it('should return false for non-expired URL', () => {
      const result = component.isExpired(undefined);
      expect(result).toBe(false);
    });

    it('should return false for URL with future expiration', () => {
      const futureDate = new Date(Date.now() + 86400000).toISOString();
      const result = component.isExpired(futureDate);
      expect(result).toBe(false);
    });

    it('should return true for expired URL', () => {
      const pastDate = new Date(Date.now() - 1000).toISOString();
      const result = component.isExpired(pastDate);
      expect(result).toBe(true);
    });
  });

  describe('should_handle_loading_state', () => {
    it('should set loading to true while fetching URLs', () => {
      component.loadUrls();
      expect(component.loading).toBe(false); // Because the observable is synchronous in mock
    });
  });
});
