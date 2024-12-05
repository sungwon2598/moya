export interface User {
    email: string;
    name: string;
    picture: string;
}

export interface AuthState {
    isAuthenticated: boolean;
    user: User | null;
    loading: boolean;
    error: string | null;
}

export interface GoogleCredentialResponse {
    credential: string;
}
