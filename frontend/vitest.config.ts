import { defineConfig } from 'vitest/config';

export default defineConfig({
  globals: true,
  test: {
    environment: 'jsdom',
    include: ['src/**/*.spec.ts', 'src/**/*.test.ts'],
  },
});