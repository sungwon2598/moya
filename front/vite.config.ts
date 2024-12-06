import { defineConfig, loadEnv, UserConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig(({ mode }): UserConfig => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@core': path.resolve(__dirname, './src/core'),
        '@features': path.resolve(__dirname, './src/features'),
        '@shared': path.resolve(__dirname, './src/shared'),
        '@pages': path.resolve(__dirname, './src/pages'),
        '@styles': path.resolve(__dirname, './src/styles'),
        '@assets': path.resolve(__dirname, './src/assets'),
        '@types': path.resolve(__dirname, './src/core/types'),

        // 자주 사용되는 하위 경로들에 대한 별칭
        '@config': path.resolve(__dirname, './src/core/config'),
        '@providers': path.resolve(__dirname, './src/core/providers'),
        '@store': path.resolve(__dirname, './src/core/store'),
        '@components': path.resolve(__dirname, './src/shared/components'),
        '@hooks': path.resolve(__dirname, './src/shared/hooks'),
        '@utils': path.resolve(__dirname, './src/shared/utils'),
        '@constants': path.resolve(__dirname, './src/shared/constants'),
        '@ui': path.resolve(__dirname, './src/shared/components/ui')
      }
    },
    define: {
      'process.env': JSON.stringify(env)
    },
    build: {
      outDir: 'dist',
      emptyOutDir: true,
      sourcemap: true, // 개발 디버깅을 위해 true로 설정
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
      port: 3000,
      host: true,
      open: true,
      cors: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false,

        }      },
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
});
