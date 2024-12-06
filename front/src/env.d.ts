/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_APP_ENV: 'development' | 'production' | 'test'
    readonly VITE_API_BASE_URL: string
    readonly VITE_API_TIMEOUT: string
    readonly VITE_GOOGLE_CLIENT_ID: string

    // 추가 환경 변수가 있다면 여기에 계속 추가
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}

// TypeScript 컴파일러가 .env 파일의 타입을 인식할 수 있도록 declare 구문 추가
declare module '*.svg' {
    import React = require('react');
    export const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>;
    const src: string;
    export default src;
}

declare module '*.jpg' {
    const content: string;
    export default content;
}

declare module '*.png' {
    const content: string;
    export default content;
}

declare module '*.json' {
    const content: string;
    export default content;
}