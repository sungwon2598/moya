import { User } from '@/types/auth.types';

const TokenStorage = {
  getAccessToken: () => {
    try {
      return localStorage.getItem('accessToken');
    } catch (error) {
      console.error('엑세스 토큰 가져오기 실패:', error);
      return null;
    }
  },

  getRefreshToken: () => {
    try {
      return localStorage.getItem('refreshToken');
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
      localStorage.setItem('accessToken', accessToken);
      console.log('엑세스 토큰 저장됨');

      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
        console.log('리프레시 토큰 저장됨');
      }
    } catch (error) {
      console.error('토큰 저장 실패:', error);
      throw new Error('로컬 스토리지 토큰 저장 실패');
    }
  },

  isTokenExpired: (accessToken: string): boolean => {
    if (!accessToken) return true;
    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      // 만료 30초 전에 갱신하도록 설정
      return payload.exp * 1000 < Date.now() + 30000;
    } catch {
      return true;
    }
  },

  clearTokens: () => {
    try {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      console.log('토큰 삭제됨');
    } catch (error) {
      console.error('토큰 삭제 실패:', error);
    }
  },

  isAuthenticated: (): boolean => {
    const token = TokenStorage.getAccessToken();
    return !!token && !TokenStorage.isTokenExpired(token);
  },
};

const UserStorage = {
  setUserData: (userData: User) => {
    try {
      // localStorage로 변경하여 새로고침 후에도 유지
      localStorage.setItem('userData', JSON.stringify(userData));
      console.log('사용자 정보 저장됨:', userData.data?.nickname);
    } catch (error) {
      console.error('사용자 정보 저장 실패:', error);
    }
  },

  getUserData: (): User | null => {
    try {
      const userData = localStorage.getItem('userData');
      const parsedData = userData ? JSON.parse(userData) : null;
      if (parsedData) {
        console.log('사용자 정보 불러옴:', parsedData.data?.nickname);
      }
      return parsedData;
    } catch (error) {
      console.error('사용자 정보 불러오기 실패:', error);
      return null;
    }
  },

  clearUserData: () => {
    try {
      localStorage.removeItem('userData');
      console.log('사용자 정보 삭제됨');
    } catch (error) {
      console.error('사용자 정보 삭제 실패:', error);
    }
  },
};

export { TokenStorage, UserStorage };
