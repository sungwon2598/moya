import { useEffect } from 'react';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../api/authApi';

export const useAuth = () => {
    const {
        isAuthenticated,
        user,
        loading,
        setUser,
        setAuthenticated,
        setLoading
    } = useAuthStore();

    useEffect(() => {
        const initAuth = async () => {
            try {
                setLoading(true);
                const userInfo = await authApi.getUserInfo();
                if (userInfo) {
                    setUser(userInfo);
                    setAuthenticated(true);
                }
            } finally {
                setLoading(false);
            }
        };

        initAuth();
    }, []);

    return { isAuthenticated, user, loading };
};
