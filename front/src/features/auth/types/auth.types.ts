// auth.types.ts
export interface User {
    email: string;
    nickname: string;
    roles: string[];
    status: 'ACTIVE' | 'INACTIVE';
    profileImageUrl?: string; // optional로 변경
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
    idToken: string;
    authCode: string;
}

export interface GoogleCredentialResponse {
    credential: string;
    select_by?: string;
    code?: string;
}
