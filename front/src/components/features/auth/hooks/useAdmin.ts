import { useAuth } from "./useAuth";

export const useAdmin = () => {
  const { user } = useAuth();
  return {
    isAdmin: user?.roles?.includes("ADMIN") ?? false,
  };
};
