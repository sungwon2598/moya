import { defineConfig, loadEnv, UserConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import svgr from 'vite-plugin-svgr';

import path from 'path';

export default defineConfig(({ mode }): UserConfig => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react(), tailwindcss(), svgr()],
    base: '/',
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@core': path.resolve(__dirname, './src/core'),
        '@features': path.resolve(__dirname, './src/features'),
        '@pages': path.resolve(__dirname, './src/pages'),
        '@shared': path.resolve(__dirname, './src/shared'),
        '@styles': path.resolve(__dirname, './src/styles'),
        '@types': path.resolve(__dirname, './src/core/types'),
        '@store': path.resolve(__dirname, './src/core/store'),
        '@config': path.resolve(__dirname, './src/core/config'),
      },
    },
    define: {
      'process.env': JSON.stringify(env),
    },
    build: {
      outDir: 'dist',
      emptyOutDir: true,
      sourcemap: true, // 개발 디버깅을 위해 true로 설정
      rollupOptions: {
        output: {
          manualChunks: {
            vendor: ['react', 'react-dom', 'react-router-dom'],
          },
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: (assetInfo) => {
            if (!assetInfo.name) return 'assets/[name]-[hash][extname]';

            const info = assetInfo.name.split('.');
            const extType = info[info.length - 1];

            if (/\.(png|jpe?g|gif|svg|webp|ico)$/.test(assetInfo.name)) {
              return `assets/images/[name]-[hash].${extType}`;
            }
            if (/\.css$/.test(assetInfo.name)) {
              return `assets/css/[name]-[hash].${extType}`;
              //  return `assets/css/[name]-[hash].${extType}`;
            }
            if (/\.(woff2?|eot|ttf|otf)$/.test(assetInfo.name)) {
              return `assets/fonts/[name]-[hash].${extType}`;
            }
            return `assets/[name]-[hash].${extType}`;
          },
        },
        input: {
          main: path.resolve(__dirname, 'index.html'),
        },
      },
      chunkSizeWarningLimit: 1000,
    },
    server: {
      port: 3000,
      host: true,
      open: true,
      cors: true,
      proxy: {
        '/api': {
          target: 'https://api.moyastudy.com',

          // http://localhost:8080
          changeOrigin: true,
          secure: false,
        },
      },
      hmr: {
        overlay: true,
      },
    },
    preview: {
      port: 3000,
      open: true,
    },
    css: {
      devSourcemap: true,
    },
  };
});
