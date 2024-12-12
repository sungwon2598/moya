import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '@store/store.ts';
import { loginWithGoogle, logoutUser, checkLoginStatus } from '../store/authSlice.ts';
import {GoogleAuthResponse} from "../types/auth.types.ts";
export const useAuth = () => {
    const dispatch = useDispatch<AppDispatch>();
    const auth = useSelector((state: RootState) => state.auth);

    const handleGoogleLogin = async (authData: GoogleAuthResponse) => {
        try {
            return await dispatch(loginWithGoogle(authData)).unwrap();
        } catch (error) {
            console.error('Google login failed:', error);
            throw error;
        }
    };

    const handleLogout = () => {
        dispatch(logoutUser());
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
