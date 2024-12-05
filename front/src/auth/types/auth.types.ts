export interface User {
    email: string;
    firstName: string;
    lastName: string;
}

export interface AuthState {
    isLogin: boolean;
    user: User | null;
    loading: boolean;
    error: string | null;
}
