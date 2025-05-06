import { create } from 'zustand';
import { GoogleAuthResponse, User } from '@/types/auth.types';
import { postGoogleAuth, logout,  } from '@/api/authApi';
import { TokenStorage, UserStorage } from '../utils/tokenUtils';


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

            console.log('로그인 성공: 사용자 정보: ', response.data.user);

            const userData = response.data.user
            const accessToken = response.data.user.data?.accessToken;
            const refreshToken = response.data.user.data?.refreshToken;     

            if (accessToken && refreshToken) {
                TokenStorage.setTokens(
                    accessToken,
                    refreshToken
                );
                UserStorage.setUserData(userData);
            }

            set({
                loading: false,
                isLogin: true,
                user: userData,
                accessToken: accessToken,
                error: null,
            });
            
        } catch (error) {
            set({
            loading: false,
            isLogin: false,
            user: null,
            error: error instanceof Error ? error.message : '인증 실패',
            });
        }
    },

    checkLoginStatus: async () => {
        console.log("로그인 상태 확인 시작");
        const token = TokenStorage.getAccessToken();
        console.log("토큰 존재 여부:", !!token);
        
        if (!token) {
            console.log("토큰이 없습니다.");
            set(initialState);
            return;
        }
        
        set({ loading: true });
        
        // 우선 세션 스토리지에서 사용자 정보 확인
        const savedUserData = UserStorage.getUserData();
        if (savedUserData) {
            console.log("세션 스토리지에서 사용자 정보 복원:", savedUserData);
            set({
                loading: false,
                isLogin: true,
                user: savedUserData,
                accessToken: token,
                error: null,
            });
            return;
        }
    
        set({
            loading: false,
            isLogin: false,
            error: "사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.",
        });
        TokenStorage.clearTokens();
        UserStorage.clearUserData();
        
    },

    logoutUser: async () => {
        try {
            await logout();
            TokenStorage.clearTokens();
            UserStorage.clearUserData();
            set(initialState);

        } catch (error) {
            console.error('로그아웃 실패:', error);
            TokenStorage.clearTokens();
            UserStorage.clearUserData();
            set(initialState);
        }
    },

    clearError: () => set({ error: null }),

    resetAuth: () => {
        TokenStorage.clearTokens();
        set(initialState);
        },

}));

export const selectIsAuthenticated = (state: AuthState) => state.isLogin;
export const selectUser = (state: AuthState) => state.user;
export const selectAuthLoading = (state: AuthState) => state.loading;
export const selectAuthError = (state: AuthState) => state.error;