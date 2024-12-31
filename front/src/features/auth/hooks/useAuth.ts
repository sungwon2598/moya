import { useDispatch, useSelector } from 'react-redux';
import { GoogleAuthResponse } from '../types/auth.types';
import {
    authenticateWithGoogleThunk,
    logoutUser,
    checkLoginStatus,
    selectIsAuthenticated,
    selectUser,
    selectAuthLoading,
    selectAuthError
} from '../store/authSlice';
import type { AppDispatch } from '@/core/store/store';

export interface UseAuthReturn {
    isAuthenticated: boolean;
    user: ReturnType<typeof selectUser>;
    loading: boolean;
    error: string | null;
    handleGoogleLogin: (authData: GoogleAuthResponse) => Promise<void>;
    handleLogout: () => Promise<void>;
    checkAuth: () => Promise<void>;
}

export const useAuth = (): UseAuthReturn => {
    const dispatch = useDispatch<AppDispatch>();
    const isAuthenticated = useSelector(selectIsAuthenticated);
    const user = useSelector(selectUser);
    const loading = useSelector(selectAuthLoading);
    const error = useSelector(selectAuthError);

    const handleGoogleLogin = async (authData: GoogleAuthResponse) => {
        try {
            await dispatch(authenticateWithGoogleThunk(authData)).unwrap();
        } catch (error) {
            console.error('Google login failed:', error);
            throw error;
        }
    };

    const handleLogout = async () => {
        try {
            await dispatch(logoutUser()).unwrap();
        } catch (error) {
            console.error('Logout failed:', error);
            throw error;
        }
    };

    const checkAuth = async () => {
        try {
            await dispatch(checkLoginStatus()).unwrap();
        } catch (error) {
            console.error('Auth check failed:', error);
            throw error;
        }
    };

    return {
        isAuthenticated,
        user,
        loading,
        error,
        handleGoogleLogin,
        handleLogout,
        checkAuth
    };
};
