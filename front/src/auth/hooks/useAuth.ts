import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { loginStart, loginSuccess, loginFailure, logout } from '../store/authSlice';
import { authApi } from '../api/authApi';
import { LoginRequest } from '../types/auth.types';
import { setStoredToken, removeStoredToken } from '../utils/tokenUtils';

export const useAuth = () => {
    const dispatch = useDispatch();
    const auth = useSelector((state: RootState) => state.auth);

    const login = async (credentials: LoginRequest) => {
        try {
            dispatch(loginStart());
            const response = await authApi.login(credentials);
            setStoredToken(response.accessToken);
            dispatch(loginSuccess({
                token: response.accessToken,
                user: response.user
            }));
        } catch (error: any) {
            const errorMessage = error.response?.data?.message || '로그인에 실패했습니다.';
            dispatch(loginFailure(errorMessage));
            throw error;
        }
    };

    const logoutUser = async () => {
        try {
            await authApi.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            removeStoredToken();
            dispatch(logout());
        }
    };

    return {
        ...auth,
        login,
        logout: logoutUser
    };
};
