// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'node:path';  // node: prefix 사용

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: [
      { find: '@', replacement: path.resolve(__dirname, './src') },
      { find: '@components', replacement: path.resolve(__dirname, './src/components') },
      { find: '@pages', replacement: path.resolve(__dirname, './src/pages') },
      { find: '@hooks', replacement: path.resolve(__dirname, './src/hooks') },
      { find: '@utils', replacement: path.resolve(__dirname, './src/utils') },
      { find: '@types', replacement: path.resolve(__dirname, './src/types') },
      { find: '@styles', replacement: path.resolve(__dirname, './src/styles') },
      { find: '@assets', replacement: path.resolve(__dirname, './src/assets') }
    ]
  }
});