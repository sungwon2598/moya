// src/auth/types/auth.types.ts
export interface User {
    id: string;
    email: string;
    nickname: string;
}

export interface AuthState {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    loading: boolean;
    error: string | null;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    accessToken: string;
    user: User;
}

export interface GoogleCredentialResponse {
    credential: string;
    select_by: string;
}

export interface UserInfo {
    email: string;
    firstName: string;
    lastName: string;
    picture?: string;
}

export interface AuthState {
    isAuthenticated: boolean;
    user: UserInfo | null;
    loading: boolean;
    error: string | null;
}
