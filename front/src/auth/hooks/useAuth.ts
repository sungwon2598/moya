import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { loginStart, loginSuccess, loginFailure, logout } from '../store/authSlice';
import { authenticateWithGoogle } from '../api/authApi';

export const useAuth = () => {
    const dispatch = useDispatch();
    const auth = useSelector((state: RootState) => state.auth);

    const handleGoogleLogin = async (credential: string) => {
        try {
            dispatch(loginStart());
            const userData = await authenticateWithGoogle(credential);
            dispatch(loginSuccess(userData.user));
        } catch (error) {
            dispatch(loginFailure(error instanceof Error ? error.message : 'Authentication failed'));
        }
    };

    const handleLogout = () => {
        dispatch(logout());
    };

    return {
        ...auth,
        handleGoogleLogin,
        handleLogout
    };
};
