import { create } from 'zustand';
import { User } from '@/types/auth.types';
import { logout } from '@/api/authApi';
import { TokenStorage, UserStorage } from '../utils/tokenUtils';

interface AuthState {
  isLogin: boolean;
  user: User | null;
  loading: boolean;
  error: string | null;
  accessToken: string | null;

  handleOAuthCallback: (params: {
    accessToken: string;
    refreshToken: string;
    email?: string;
    nickname?: string;
    profileImage?: string;
  }) => Promise<void>;

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

  // OAuth 콜백
  handleOAuthCallback: async (params) => {
    try {
      set({ loading: true, error: null });

      const { accessToken, refreshToken, email, nickname, profileImage } = params;

      console.log('OAuth 콜백 처리 시작:', { email, nickname });

      TokenStorage.setTokens(accessToken, refreshToken);

      const userData: User = {
        data: {
          email: email || '',
          nickname: nickname || '',
          profileImageUrl: profileImage || '',
          accessToken,
          refreshToken,
        },
      };

      // 사용자 정보 저장
      UserStorage.setUserData(userData);

      set({
        loading: false,
        isLogin: true,
        user: userData,
        accessToken: accessToken,
        error: null,
      });

      console.log('OAuth 로그인 완료:', userData);
    } catch (error) {
      console.error('OAuth 콜백 처리 실패:', error);
      set({
        loading: false,
        isLogin: false,
        user: null,
        error: error instanceof Error ? error.message : 'OAuth 로그인 실패',
      });
    }
  },

  checkLoginStatus: async () => {
    console.log('로그인 상태 확인 시작');
    const token = TokenStorage.getAccessToken();
    console.log('토큰 존재 여부:', !!token);

    if (!token) {
      console.log('토큰이 없습니다.');
      set(initialState);
      return;
    }

    // 토큰 만료 확인
    if (TokenStorage.isTokenExpired(token)) {
      console.log('토큰이 만료되었습니다.');
      TokenStorage.clearTokens();
      UserStorage.clearUserData();
      set(initialState);
      return;
    }

    set({ loading: true });

    const savedUserData = UserStorage.getUserData();
    if (savedUserData) {
      console.log('세션 스토리지에서 사용자 정보 복원:', savedUserData);
      set({
        loading: false,
        isLogin: true,
        user: savedUserData,
        accessToken: token,
        error: null,
      });
      return;
    }

    // 사용자 정보가 없으면 로그아웃 처리
    console.log('사용자 정보를 찾을 수 없습니다.');
    set({
      loading: false,
      isLogin: false,
      error: '사용자 정보를 찾을 수 없습니다. 다시 로그인해주세요.',
    });
    TokenStorage.clearTokens();
    UserStorage.clearUserData();
  },

  logoutUser: async () => {
    try {
      set({ loading: true });

      try {
        await logout();
      } catch (error) {
        console.error('서버 로그아웃 실패:', error);
      }

      TokenStorage.clearTokens();
      UserStorage.clearUserData();
      set(initialState);

      console.log('로그아웃 완료');
    } catch (error) {
      console.error('로그아웃 실패:', error);
      // 실패해도 클라이언트 정보는 정리
      TokenStorage.clearTokens();
      UserStorage.clearUserData();
      set(initialState);
    }
  },

  clearError: () => set({ error: null }),

  resetAuth: () => {
    TokenStorage.clearTokens();
    UserStorage.clearUserData();
    set(initialState);
  },
}));

export const selectIsAuthenticated = (state: AuthState) => state.isLogin;
export const selectUser = (state: AuthState) => state.user;
export const selectAuthLoading = (state: AuthState) => state.loading;
export const selectAuthError = (state: AuthState) => state.error;
