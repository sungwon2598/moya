import { create } from 'zustand';
import { GoogleAuthResponse, User } from '../types/auth.types';
import { getUserInfo, postGoogleAuth, logout } from '../api/authApi';
import { TokenStorage } from '../utils/tokenUtils';

interface AuthState {
    isLogin: boolean;
    user: User | null;
    loading: boolean;
    error: string | null;
    accessToken: string | null;
    
    authenticateWithGoogle: (authData: GoogleAuthResponse) => Promise<void>;
    checkLoginStatus: () => Promise<void>;
    logoutUser: () => Promise<void>;
    clearError: () => void;
    resetAuth: () => void;
}

const initialState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null,
    accessToken: null,
};

export const useAuthStore = create<AuthState>((set) => ({
...initialState,

authenticateWithGoogle: async (authData: GoogleAuthResponse) => {
try {
    set({ loading: true, error: null });
    const response = await postGoogleAuth(authData);
    
    if (!response.success || !response.data) {
    throw new Error(response.message || 'Authentication failed');
    }

    console.log(response.data.user);
    TokenStorage.setTokens(response.data.user.data.accessToken);

    set({
    loading: false,
    isLogin: true,
    user: response.data.user,
    accessToken: response.data.user.accessToken,
    error: null,
    });

    // 로컬 스토리지에 토큰 저장
    
} catch (error) {
    set({
    loading: false,
    isLogin: false,
    user: null,
    error: error instanceof Error ? error.message : 'Authentication failed',
    });
}
},

checkLoginStatus: async () => {
try {
    set({ loading: true, error: null });
    const userData = await getUserInfo();
    
    set({
    loading: false,
    isLogin: true,
    user: userData,
    error: null,
    });
} catch (error) {
    set({
    loading: false,
    isLogin: false,
    user: null,
    error: error instanceof Error ? error.message : 'Failed to fetch user info',
    });
}
},

logoutUser: async () => {
try {
    await logout();
    TokenStorage.clearTokens();
    set(initialState);
} catch (error) {
    console.error('Logout failed:', error);
    set(initialState);
}
},

clearError: () => set({ error: null }),

resetAuth: () => set(initialState),
}));

export const selectIsAuthenticated = (state: AuthState) => state.isLogin;
export const selectUser = (state: AuthState) => state.user;
export const selectAuthLoading = (state: AuthState) => state.loading;
export const selectAuthError = (state: AuthState) => state.error;