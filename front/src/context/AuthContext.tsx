import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import { api } from '../config/apiConfig';

// 사용자 정보 인터페이스
interface User {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
}

// 인증 컨텍스트 타입
interface AuthContextType {
    user: User | null;
    isLoggedIn: boolean;
    isLoading: boolean;
    error: string | null;
    login: (token: string) => void;
    logout: () => Promise<void>;
    register: (nickname: string, email: string, termsAgreed: boolean) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // JWT 토큰으로 로그인
    const login = useCallback((token: string) => {
        localStorage.setItem('token', token);
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        setIsLoggedIn(true);
    }, []);

    // 로그아웃
    const logout = useCallback(async () => {
        try {
            setIsLoading(true);
            await api.post('/api/auth/logout');

            // 상태 초기화
            setUser(null);
            setIsLoggedIn(false);

            // 토큰 제거
            localStorage.removeItem('token');
            delete api.defaults.headers.common['Authorization'];
        } catch (err) {
            console.error('Logout error:', err);
        } finally {
            setIsLoading(false);
        }
    }, []);

    // 회원가입 (추가 정보 입력)
    const register = useCallback(async (nickname: string, email: string, termsAgreed: boolean) => {
        try {
            setIsLoading(true);
            setError(null);

            const response = await api.post('/api/auth/register', {
                nickname,
                email,
                termsAgreed
            });

            // 회원가입 성공 시 받은 JWT 토큰으로 로그인 처리
            const { token } = response.data;
            login(token);
        } catch (err: any) {
            setError(err.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
            throw err;
        } finally {
            setIsLoading(false);
        }
    }, [login]);

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

    // 로그인 상태 변경 시 사용자 정보 로드
    React.useEffect(() => {
        const loadUserInfo = async () => {
            if (isLoggedIn && !user) {
                try {
                    const response = await api.get('/api/auth/me');
                    setUser(response.data.user);
                } catch (err) {
                    console.error('Failed to load user info:', err);
                    logout();
                }
            }
        };

        loadUserInfo();
    }, [isLoggedIn, user, logout]);

    const value = {
        user,
        isLoggedIn,
        isLoading,
        error,
        login,
        logout,
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