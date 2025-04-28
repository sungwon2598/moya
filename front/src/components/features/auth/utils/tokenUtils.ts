// features/auth/utils/tokenUtils.ts
export const TokenStorage = {
    getAccessToken: () => {
        try {
            return sessionStorage.getItem('accessToken');
        } catch (error) {
            console.error('Failed to get access token:', error);
            return null;
        }
    },
    getRefreshToken: () => {
        try {
            return sessionStorage.getItem('refreshToken');
        } catch (error) {
            console.error('Failed to get refresh token:', error);
            return null;
        }
    },
    setTokens: (accessToken: string, refreshToken?: string) => {
        try {
            if (!accessToken) {
                throw new Error('Access token is required');
            }
            sessionStorage.setItem('accessToken', accessToken);
            if (refreshToken) {
                sessionStorage.setItem('refreshToken', refreshToken);
            }
            console.log('Tokens stored successfully');
        } catch (error) {
            console.error('Failed to store tokens:', error);
            throw new Error('Failed to store tokens in sessionStorage');
        }
    },
    clearTokens: () => {
        try {
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('refreshToken');
            console.log('Tokens cleared');
        } catch (error) {
            console.error('Failed to clear tokens:', error);
        }
    }
};
