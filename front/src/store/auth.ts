import { create } from 'zustand';
import { UserProfile } from '@/types/auth';
import { userService } from '@/services/auth';
import { auth } from '@/services/config';

interface AuthState {
  isLogin: boolean;
  user: UserProfile | null;
  loading: boolean;
  error: string | null;

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
};
export const useAuthStore = create<AuthState>((set) => ({
  ...initialState,

  checkLoginStatus: async () => {
    try {
      set({ loading: true, error: null });

      const response = await userService.getUser();

      set({
        loading: false,
        isLogin: true,
        user: response.data,
        error: null,
      });
    } catch (error) {
      console.log('유저 정보 조회 실패:', error);
      set({
        loading: false,
        isLogin: false,
        user: null,
        error: null, // 비로그인은 에러가 아님
      });
    }
  },

  // 로그아웃
  logoutUser: async () => {
    try {
      set({ loading: true });

      await auth.post('/api/v1/auth/logout');

      set(initialState);
      // console.log('로그아웃 완료');
    } catch (error) {
      console.error('로그아웃 실패:', error);
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
