// src/context/AuthContext.tsx
import React, { createContext, useContext, useState, useCallback } from 'react';
import {AUTH_API, AuthResponse} from '../config/apiConfig';
import { useModal } from '../hooks/useModal';
import Alert from '../component/signup/Alert';

interface AuthContextType {
    isLoggedIn: boolean;
    memberInfo: AuthResponse['memberInfo'] | null;
    login: (authResponse: AuthResponse) => void;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState<boolean>(true);
    const [memberInfo, setMemberInfo] = useState<AuthResponse['memberInfo'] | null>(null);
    const { showModal } = useModal();

    const login = useCallback((authResponse: AuthResponse) => {
        localStorage.setItem('token', authResponse.token);
        setMemberInfo(authResponse.memberInfo);
        setIsLoggedIn(true);
    }, []);

    const logout = useCallback(async () => {
        try {
            await AUTH_API.logout();
            setMemberInfo(null);
            setIsLoggedIn(false);
        } catch (error) {
            showModal(
                <Alert variant="error">로그아웃 중 오류가 발생했습니다.</Alert>,
                { size: 'sm' }
            );
        }
    }, [showModal]);

    return (
        <AuthContext.Provider value={{ isLoggedIn, memberInfo, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};
