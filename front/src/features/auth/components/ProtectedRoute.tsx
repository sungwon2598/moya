import { FC, useEffect } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '@store/store.ts';
import { checkLoginStatus } from '../store/authSlice.ts';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

export const ProtectedRoute: FC<ProtectedRouteProps> = ({ children }) => {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();
    const { isLogin, loading } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        const checkAuth = async () => {
            await dispatch(checkLoginStatus());
        };
        checkAuth();
    }, [dispatch]);

    useEffect(() => {
        if (!loading && !isLogin) {
            navigate('/');
        }
    }, [isLogin, loading, navigate]);

    if (loading) {
        return <div>Loading...</div>;
    }

    return isLogin ? <>{children}</> : <Navigate to="/" />;
};
