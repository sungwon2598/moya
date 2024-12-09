import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '@store/store.ts';
import { loginWithGoogle, logout, checkLoginStatus } from '../store/authSlice.ts';

export const useAuth = () => {
    const dispatch = useDispatch<AppDispatch>();
    const auth = useSelector((state: RootState) => state.auth);

    const handleGoogleLogin = async (credential: string) => {
        try {
            return await dispatch(loginWithGoogle(credential)).unwrap();
        } catch (error) {
            console.error('Google login failed:', error);
            throw error;
        }
    };

    const handleLogout = () => {
        dispatch(logout());
    };
    const checkAuth = async () => {
        try {
            return await dispatch(checkLoginStatus()).unwrap();
        } catch (error) {
            console.error('Auth check failed:', error);
            throw error;
        }
    };

    return {
        ...auth,
        handleGoogleLogin,
        handleLogout,
        checkAuth
    };
};
