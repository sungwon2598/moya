export interface User {
    email: string;
    nickname: string;
    roles: string[];
    status: 'ACTIVE' | 'INACTIVE';
    profileImageUrl: string;
}

export interface AuthState {
    isLogin: boolean;
    user: User | null;  // User | false 대신 User | null 사용
    loading: boolean;
    error: string | null;
}
