import { useAuth } from './useAuth';

export const useAdmin = () => {
    const { user } = useAuth();
    return {
        isAdmin: user?.roles.includes('ROLE_ADMIN') ?? false
    };
};