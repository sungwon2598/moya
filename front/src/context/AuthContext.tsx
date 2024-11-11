import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import { api } from '../config/apiConfig';

// 사용자 정보 인터페이스
interface User {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
}

// 구글 인증 상태 인터페이스
interface GoogleAuthState {
    token: string;
    email: string;
    name: string;
    picture?: string;
}

// 인증 컨텍스트 타입
interface AuthContextType {
    user: User | null;
    isLoggedIn: boolean;
    isLoading: boolean;
    error: string | null;
    googleAuthState: GoogleAuthState | null;
    // 일반 로그인/로그아웃
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    // 구글 OAuth 관련
    setGoogleAuthState: (state: GoogleAuthState) => void;
    completeGoogleSignup: (nickname: string, password: string) => Promise<void>;
    // 회원가입 관련
    register: (email: string, password: string, nickname: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [googleAuthState, setGoogleAuthState] = useState<GoogleAuthState | null>(null);

    // 일반 로그인
    const login = useCallback(async (email: string, password: string) => {
        try {
            setIsLoading(true);
            setError(null);

            const response = await api.post('/api/auth/login', { email, password });
            setUser(response.data.user);
            setIsLoggedIn(true);

            // 토큰 저장
            localStorage.setItem('token', response.data.token);
            api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
        } catch (err: any) {
            setError(err.response?.data?.message || '로그인 중 오류가 발생했습니다.');
            throw err;
        } finally {
            setIsLoading(false);
        }
    }, []);

    // 로그아웃
    const logout = useCallback(async () => {
        try {
            setIsLoading(true);
            await api.post('/api/auth/logout');

            // 상태 초기화
            setUser(null);
            setIsLoggedIn(false);
            setGoogleAuthState(null);

            // 토큰 제거
            localStorage.removeItem('token');
            delete api.defaults.headers.common['Authorization'];
        } catch (err) {
            console.error('Logout error:', err);
        } finally {
            setIsLoading(false);
        }
    }, []);

    // 구글 회원가입 완료
    const completeGoogleSignup = useCallback(async (nickname: string, password: string) => {
        if (!googleAuthState) {
            throw new Error('Google authentication state is missing');
        }

        try {
            setIsLoading(true);
            setError(null);

            const response = await api.post('/api/auth/google/complete', {
                nickname,
                password,
                googleToken: googleAuthState.token,
                email: googleAuthState.email
            });

            setUser(response.data.user);
            setIsLoggedIn(true);
            setGoogleAuthState(null); // 완료 후 구글 인증 상태 초기화

            // 토큰 저장
            localStorage.setItem('token', response.data.token);
            api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
        } catch (err: any) {
            setError(err.response?.data?.message || '회원가입 완료 중 오류가 발생했습니다.');
            throw err;
        } finally {
            setIsLoading(false);
        }
    }, [googleAuthState]);

    // 일반 회원가입
    const register = useCallback(async (email: string, password: string, nickname: string) => {
        try {
            setIsLoading(true);
            setError(null);

            const response = await api.post('/api/auth/register', {
                email,
                password,
                nickname
            });

            setUser(response.data.user);
            setIsLoggedIn(true);

            // 토큰 저장
            localStorage.setItem('token', response.data.token);
            api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
        } catch (err: any) {
            setError(err.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
            throw err;
        } finally {
            setIsLoading(false);
        }
    }, []);

    // 앱 초기화 시 토큰 체크 및 사용자 정보 로드
    React.useEffect(() => {
        const initializeAuth = async () => {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                    const response = await api.get('/api/auth/me');
                    setUser(response.data.user);
                    setIsLoggedIn(true);
                } catch (err) {
                    localStorage.removeItem('token');
                    delete api.defaults.headers.common['Authorization'];
                }
            }
        };

        initializeAuth();
    }, []);

    const value = {
        user,
        isLoggedIn,
        isLoading,
        error,
        googleAuthState,
        login,
        logout,
        setGoogleAuthState,
        completeGoogleSignup,
        register
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

// 커스텀 훅
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};