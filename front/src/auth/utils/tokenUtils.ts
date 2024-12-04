
// 타입 정의
interface DecodedToken {
    iss: string;
    azp: string;
    aud: string;
    sub: string;
    email: string;
    email_verified: boolean;
    name: string;
    picture: string;
    given_name: string;
    family_name: string;
    locale: string;
    iat: number;
    exp: number;
}

// Redux action types 정의
import { AppDispatch } from '../../store';
import { logout, refreshToken } from '../store/authSlice';

class TokenService {
    private dispatch: AppDispatch | null = null;
    private refreshTimer: NodeJS.Timeout | null = null;

    // dispatch 설정
    setDispatch(dispatch: AppDispatch) {
        this.dispatch = dispatch;
    }

    decodeToken(token: string): DecodedToken | null {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                window
                    .atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );
            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error('Token decode error:', error);
            return null;
        }
    }

    isTokenExpired(token: string): boolean {
        const decoded = this.decodeToken(token);
        if (!decoded) return true;
        return decoded.exp * 1000 < Date.now();
    }

    validateToken(token: string): boolean {
        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const isValidIssuer = decoded.iss === 'accounts.google.com';
        const isValidAudience = decoded.aud === process.env.REACT_APP_GOOGLE_CLIENT_ID;
        const isTokenActive = !this.isTokenExpired(token);
        const isEmailVerified = decoded.email_verified;

        if (!isTokenActive && this.dispatch) {
            this.dispatch(logout());
        }

        return isValidIssuer && isValidAudience && isTokenActive && isEmailVerified;
    }

    extractUserInfo(token: string) {
        const decoded = this.decodeToken(token);
        if (!decoded) return null;

        return {
            email: decoded.email,
            firstName: decoded.given_name,
            lastName: decoded.family_name,
            picture: decoded.picture,
            name: decoded.name,
        };
    }

    private getTimeUntilExpiry(token: string): number {
        const decoded = this.decodeToken(token);
        if (!decoded) return 0;
        return (decoded.exp * 1000) - Date.now();
    }

    needsRefresh(token: string): boolean {
        const timeUntilExpiry = this.getTimeUntilExpiry(token);
        const fiveMinutes = 5 * 60 * 1000;
        return timeUntilExpiry < fiveMinutes;
    }

    setupRefreshTimer(token: string) {
        if (this.refreshTimer) {
            clearTimeout(this.refreshTimer);
        }

        if (!this.dispatch) {
            console.error('Dispatch not set in TokenService');
            return;
        }

        const timeUntilRefresh = this.getTimeUntilExpiry(token) - (5 * 60 * 1000);

        this.refreshTimer = setTimeout(() => {
            this.dispatch!(refreshToken());
        }, timeUntilRefresh);
    }

    getAuthHeaders(token: string) {
        return {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
        };
    }

    // Redux 상태와 연동되는 메서드들
    clearRefreshTimer() {
        if (this.refreshTimer) {
            clearTimeout(this.refreshTimer);
            this.refreshTimer = null;
        }
    }
}

export const tokenService = new TokenService();

// Type Guard
export const isValidToken = (token: unknown): token is string => {
    if (typeof token !== 'string') return false;
    try {
        return tokenService.validateToken(token);
    } catch {
        return false;
    }
};

// Redux store와 함께 사용하기 위한 미들웨어
export const tokenMiddleware = (store: any) => (next: any) => (action: any) => {
    if (!tokenService.dispatch) {
        tokenService.setDispatch(store.dispatch);
    }
    return next(action);
};

export default tokenService;
