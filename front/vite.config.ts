import { defineConfig, loadEnv, UserConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig(({ mode }): UserConfig => {
  // env 타입 명시적 정의
  const env = loadEnv(mode, path.resolve(process.cwd()), '') as Record<string, string>;

  return {
    plugins: [react()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@components': path.resolve(__dirname, './src/components'),
        '@pages': path.resolve(__dirname, './src/pages'),
        '@hooks': path.resolve(__dirname, './src/hooks'),
        '@utils': path.resolve(__dirname, './src/utils'),
        '@styles': path.resolve(__dirname, './src/styles'),
        '@assets': path.resolve(__dirname, './src/assets')
      }
    },
    define: {
      // env 객체를 JSON으로 문자열화하여 전달
      'process.env': JSON.stringify(env)
    },
    build: {
      outDir: 'dist',
      emptyOutDir: true,
      sourcemap: false,
      rollupOptions: {
        output: {
          manualChunks: {
            vendor: ['react', 'react-dom', 'react-router-dom']
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
            }
            if (/\.(woff2?|eot|ttf|otf)$/.test(assetInfo.name)) {
              return `assets/fonts/[name]-[hash].${extType}`;
            }
            return `assets/[name]-[hash].${extType}`;
          }
        },
        input: {
          main: path.resolve(__dirname, 'index.html')
        }
      },
      chunkSizeWarningLimit: 1000
    },
    server: {
      host: true,
      port: 3000,
      open: true,
      cors: true,
      hmr: {
        overlay: true
      }
    },
    preview: {
      port: 3000,
      open: true
    },
    css: {
      devSourcemap: true
    }
  };
  build: {
    sourcemap: true
  }
});