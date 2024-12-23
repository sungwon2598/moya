// auth.types.ts
export interface User {
    email: string;
    nickname: string;
    roles: string[];
    status: 'ACTIVE' | 'INACTIVE';
    profileImageUrl?: string;
}

export interface AuthState {
    isLogin: boolean;
    user: User | null;
    loading: boolean;
    error: string | null;
    tokens?: {
        accessToken: string;
        refreshToken?: string;
    };
}

export interface GoogleAuthResponse {
    credential: string;
    authCode: string;
}

// Google OAuth 응답 타입
export interface GoogleCredentialResponse {
    credential: string;
    select_by?: string;
}

// Google OAuth 코드 응답 타입
export interface GoogleCodeResponse {
    code: string;
}


export interface GoogleAuthResponse {
    credential: string;
    authCode: string;
}

export interface GoogleCredentialResponse {
    credential: string;
}

export interface GoogleCodeResponse {
    code: string;
}

// types/google.d.ts
declare global {
    interface Window {
        google?: {
            accounts: {
                id: {
                    initialize: (config: GoogleInitializeConfig) => void;
                    renderButton: (
                        element: HTMLElement,
                        config: GoogleButtonConfig
                    ) => void;
                    prompt: () => void;
                };
                oauth2: {
                    initCodeClient: (config: GoogleOAuthConfig) => {
                        requestCode: () => void;
                    };
                };
            };
        };
    }
}

interface GoogleInitializeConfig {
    client_id: string;
    callback: (response: GoogleCredentialResponse) => void;
    auto_select?: boolean;
}

interface GoogleButtonConfig {
    type: 'standard';
    theme: 'outline' | 'filled_blue' | 'filled_black';
    size: 'large' | 'medium' | 'small';
    text: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    width: number;
}

interface GoogleOAuthConfig {
    client_id: string;
    scope: string;
    callback: (response: GoogleCodeResponse) => void;
    ux_mode: 'popup';
    access_type: 'offline';
    redirect_uri: string;
}
