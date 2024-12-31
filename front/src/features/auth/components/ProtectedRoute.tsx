import { FC, useEffect } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

export const ProtectedRoute: FC<ProtectedRouteProps> = ({ children }) => {
    const { isAuthenticated, loading, checkAuth } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        checkAuth().catch(() => {
            // Error handling is already done in the hook
            navigate('/');
        });
    }, [checkAuth, navigate]);

    useEffect(() => {
        if (!loading && !isAuthenticated) {
            navigate('/');
        }
    }, [isAuthenticated, loading, navigate]);

    if (loading) {
        return <div>Loading...</div>;
    }

    return isAuthenticated ? <>{children}</> : <Navigate to="/" />;
};
