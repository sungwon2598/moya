import { GoogleAuthResponse } from '../types/auth.types';
import { useAuthStore } from '../store/auth';
import type { User } from '../types/auth.types';

export interface UseAuthReturn {
  isAuthenticated: boolean;
  user: User | null;
  loading: boolean;
  error: string | null;
  handleGoogleLogin: (authData: GoogleAuthResponse) => Promise<void>;
  handleLogout: () => Promise<void>;
  checkAuth: () => Promise<void>;
}

export const useAuth = (): UseAuthReturn => {
  const {
    isLogin: isAuthenticated,
    user,
    loading,
    error,
    authenticateWithGoogle: handleGoogleLogin,
    logoutUser: handleLogout,
    checkLoginStatus: checkAuth
  } = useAuthStore();

  return {
    isAuthenticated,
    user,
    loading,
    error,
    handleGoogleLogin,
    handleLogout,
    checkAuth,
  };
};
