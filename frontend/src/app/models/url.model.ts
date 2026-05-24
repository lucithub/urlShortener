export interface CreateUrlRequest {
  url: string;
  customAlias?: string;
  expirationDays?: number;
}

export interface UrlResponse {
  code: string;
  originalUrl: string;
  shortUrl: string;
  customAlias?: string;
  hitCount: number;
  createdAt: string;
  expiresAt?: string;
  expired: boolean;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}