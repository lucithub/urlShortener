import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { UrlService } from './url.service';
import { CreateUrlRequest, UrlResponse } from '../models/url.model';

describe('UrlService', () => {
  let service: UrlService;
  let httpClientSpy: { post: any; get: any; delete: any };

  beforeEach(() => {
    httpClientSpy = {
      post: vi.fn(),
      get: vi.fn(),
      delete: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        UrlService,
        { provide: HttpClient, useValue: httpClientSpy }
      ]
    });
    service = TestBed.inject(UrlService);
  });

  // Test 1: shortenUrl_success_returnsUrlResponse
  it('shortenUrl_success_returnsUrlResponse', (done) => {
    const request: CreateUrlRequest = { url: 'https://example.com' };
    const mockResponse: UrlResponse = {
      code: 'abc1234',
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:8080/abc1234',
      hitCount: 0,
      createdAt: '2026-03-31T16:30:00.000+00:00',
      expired: false
    };

    (httpClientSpy.post as ReturnType<typeof vi.fn>).mockReturnValue(of(mockResponse));

    service.shortenUrl(request).subscribe({
      next: (response) => {
        expect(response.code).toBe('abc1234');
        expect(response.originalUrl).toBe('https://example.com');
        expect(response.shortUrl).toBe('http://localhost:8080/abc1234');
        expect(response.hitCount).toBe(0);
        done();
      },
      error: () => {
        done(new Error('Should not have thrown an error'));
      }
    });
  });

  // Test 2: shortenUrl_httpError_throwsError
  it('shortenUrl_httpError_throwsError', (done) => {
    const request: CreateUrlRequest = { url: 'https://invalid-example.com' };
    const httpError = new HttpErrorResponse({
      status: 400,
      statusText: 'Bad Request',
      error: { message: 'Invalid URL format' }
    });

    (httpClientSpy.post as ReturnType<typeof vi.fn>).mockReturnValue(throwError(() => httpError));

    service.shortenUrl(request).subscribe({
      next: () => {
        done(new Error('Should have thrown an error'));
      },
      error: (error) => {
        expect(error).toBeInstanceOf(Error);
        expect(error.message).toContain('400');
        done();
      }
    });
  });

  // Test 3: getAllUrls_success_returnsArray
  it('getAllUrls_success_returnsArray', (done) => {
    const mockUrls: UrlResponse[] = [
      {
        code: 'abc1234',
        originalUrl: 'https://example.com',
        shortUrl: 'http://localhost:8080/abc1234',
        hitCount: 5,
        createdAt: '2026-03-31T16:30:00.000+00:00',
        expired: false
      },
      {
        code: 'xyz5678',
        originalUrl: 'https://test.com',
        shortUrl: 'http://localhost:8080/xyz5678',
        hitCount: 10,
        createdAt: '2026-03-30T16:30:00.000+00:00',
        expired: false
      }
    ];

    (httpClientSpy.get as ReturnType<typeof vi.fn>).mockReturnValue(of(mockUrls));

    service.getAllUrls().subscribe({
      next: (urls) => {
        expect(urls).toBeInstanceOf(Array);
        expect(urls.length).toBe(2);
        expect(urls[0].code).toBe('abc1234');
        expect(urls[1].code).toBe('xyz5678');
        done();
      },
      error: () => {
        done(new Error('Should not have thrown an error'));
      }
    });
  });

  // Test 4: getAllUrls_httpError_throwsError
  it('getAllUrls_httpError_throwsError', (done) => {
    const httpError = new HttpErrorResponse({
      status: 500,
      statusText: 'Internal Server Error',
      error: { message: 'Server error' }
    });

    (httpClientSpy.get as ReturnType<typeof vi.fn>).mockReturnValue(throwError(() => httpError));

    service.getAllUrls().subscribe({
      next: () => {
        done(new Error('Should have thrown an error'));
      },
      error: (error) => {
        expect(error).toBeInstanceOf(Error);
        expect(error.message).toContain('500');
        done();
      }
    });
  });

  // Test 5: getUrlByCode_success_returnsUrlResponse
  it('getUrlByCode_success_returnsUrlResponse', (done) => {
    const code = 'abc1234';
    const mockResponse: UrlResponse = {
      code: code,
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:8080/abc1234',
      hitCount: 5,
      createdAt: '2026-03-31T16:30:00.000+00:00',
      expired: false
    };

    (httpClientSpy.get as ReturnType<typeof vi.fn>).mockReturnValue(of(mockResponse));

    service.getUrlByCode(code).subscribe({
      next: (response) => {
        expect(response.code).toBe(code);
        expect(response.originalUrl).toBe('https://example.com');
        expect(response.hitCount).toBe(5);
        done();
      },
      error: () => {
        done(new Error('Should not have thrown an error'));
      }
    });
  });

  // Test 6: getUrlByCode_notFound_throwsError
  it('getUrlByCode_notFound_throwsError', (done) => {
    const code = 'nonexist';
    const httpError = new HttpErrorResponse({
      status: 404,
      statusText: 'Not Found',
      error: { message: `Short URL not found for code: ${code}` }
    });

    (httpClientSpy.get as ReturnType<typeof vi.fn>).mockReturnValue(throwError(() => httpError));

    service.getUrlByCode(code).subscribe({
      next: () => {
        done(new Error('Should have thrown an error'));
      },
      error: (error) => {
        expect(error).toBeInstanceOf(Error);
        expect(error.message).toContain('404');
        done();
      }
    });
  });

  // Test 7: deleteUrl_success_completes
  it('deleteUrl_success_completes', (done) => {
    const code = 'abc1234';

    (httpClientSpy.delete as ReturnType<typeof vi.fn>).mockReturnValue(of(undefined));

    service.deleteUrl(code).subscribe({
      next: () => {
        done();
      },
      error: () => {
        done(new Error('Should not have thrown an error'));
      }
    });
  });

  // Test 8: deleteUrl_httpError_throwsError
  it('deleteUrl_httpError_throwsError', (done) => {
    const code = 'abc1234';
    const httpError = new HttpErrorResponse({
      status: 404,
      statusText: 'Not Found',
      error: { message: `Short URL not found for code: ${code}` }
    });

    (httpClientSpy.delete as ReturnType<typeof vi.fn>).mockReturnValue(throwError(() => httpError));

    service.deleteUrl(code).subscribe({
      next: () => {
        done(new Error('Should have thrown an error'));
      },
      error: (error) => {
        expect(error).toBeInstanceOf(Error);
        expect(error.message).toContain('404');
        done();
      }
    });
  });
});
