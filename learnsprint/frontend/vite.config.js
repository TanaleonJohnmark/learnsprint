import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Development: `npm run dev` serves the app on port 5173 and forwards every
// /api request to the Spring Boot backend on port 8080.
//
// Production: `npm run build` writes the finished site into the backend's
// static folder, so Spring Boot serves the React app at http://localhost:8080.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
});
