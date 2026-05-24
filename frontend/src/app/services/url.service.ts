import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { CreateUrlRequest, UrlResponse, ErrorResponse } from '../models/url.model';

@Injectable({
  providedIn: 'root'
})
export class UrlService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/urls';

  shortenUrl(request: CreateUrlRequest): Observable<UrlResponse> {
    return this.http.post<UrlResponse>(this.API_URL, request).pipe(
      catchError(this.handleError)
    );
  }

  getAllUrls(): Observable<UrlResponse[]> {
    return this.http.get<UrlResponse[]>(this.API_URL).pipe(
      catchError(this.handleError)
    );
  }

  getUrlByCode(code: string): Observable<UrlResponse> {
    return this.http.get<UrlResponse>(`${this.API_URL}/${code}`).pipe(
      catchError(this.handleError)
    );
  }

  deleteUrl(code: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${code}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = error.error?.message || `Error Code: ${error.status}`;
    }
    console.error('HTTP Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}