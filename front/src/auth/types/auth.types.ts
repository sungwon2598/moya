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
