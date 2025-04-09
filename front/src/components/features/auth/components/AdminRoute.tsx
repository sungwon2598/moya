import { FC } from 'react';
import { Navigate } from 'react-router-dom';
import { useAdmin } from '../hooks/useAdmin';
import { useAuth } from '../hooks/useAuth';

interface AdminRouteProps {
    children: React.ReactNode;
}

export const AdminRoute: FC<AdminRouteProps> = ({ children }) => {
    const { isAdmin } = useAdmin();
    const { loading } = useAuth();

    if (loading) {
        return <div className="flex justify-center items-center h-screen">
            <div className="text-gray-500">Loading...</div>
        </div>;
    }

    if (!isAdmin) {
        return <Navigate to="/" replace />;
    }

    return <>{children}</>;
};