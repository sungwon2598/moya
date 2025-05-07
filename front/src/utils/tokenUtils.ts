import { User } from "@/types/auth.types";

const TokenStorage = {
    getAccessToken: () => {
        try {
            return sessionStorage.getItem('accessToken');
        } catch (error) {
            console.error('엑세스 토큰 가져오기 실패:', error);
            return null;
        }
    },
    getRefreshToken: () => {
        try {
            return sessionStorage.getItem('refreshToken');
        } catch (error) {
            console.error('리프레시 토큰 가져오기 실패:', error);
            return null;
        }
    },
    setTokens: (accessToken: string, refreshToken: string) => {
        try {
            if (!accessToken) {
                throw new Error('엑세스 토큰이 필요합니다.');
            }
            sessionStorage.setItem('accessToken', accessToken);
            console.log('엑세스 토큰 저장됨');

            if (refreshToken) {
                sessionStorage.setItem('refreshToken', refreshToken);
                console.log('리프레시 토큰 저장됨');
            }
        } catch (error) {
            console.error('토큰 저장 실패:', error);
            throw new Error('세션 스토리지 토큰 저장 실패');
        }
    },
    isTokenExpired: (accessToken: string): boolean => {
        if (!accessToken) return true;
        try {
            const payload = JSON.parse(atob(accessToken.split(".")[1]));
            // 만료 30초 전에 갱신하도록 설정
            return payload.exp * 1000 < (Date.now() + 30000); 
        } catch {
            return true;
        }
    },
    clearTokens: () => {
        try {
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('refreshToken');
            console.log('토큰 삭제됨');
        } catch (error) {
            console.error('토큰 삭제 실패:', error);
        }
    },
    isAuthenticated: (): boolean => {
        const token = TokenStorage.getAccessToken();
        return !!token && !TokenStorage.isTokenExpired(token);
    }
};

const UserStorage = {
    setUserData: (userData: User) => {
        try {
            sessionStorage.setItem('userData', JSON.stringify(userData));
        } catch (error) {
            console.error('사용자 정보 저장 실패:', error);
        }
    },
    
    getUserData: (): User | null => {
        try {
            const userData = sessionStorage.getItem('userData');
            return userData ? JSON.parse(userData) : null;
        } catch (error) {
            console.error('사용자 정보 불러오기 실패:', error);
            return null;
        }
    },
    
    clearUserData: () => {
        try {
            sessionStorage.removeItem('userData');
        } catch (error) {
            console.error('사용자 정보 삭제 실패:', error);
        }
    }
};



export { TokenStorage, UserStorage }