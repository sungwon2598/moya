export interface User {
    id: number;                // 사용자 고유 ID
    email: string;            // 이메일
    firstName: string;        // 이름
    lastName: string;         // 성
    nickName: string;        // 닉네임
    profileImage?: string;    // 프로필 이미지 URL (선택적)
    role: 'USER' | 'ADMIN';  // 사용자 권한
    createdAt: string;       // 계정 생성 일시
    updatedAt: string;       // 정보 수정 일시
    status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';  // 계정 상태
    settings: {              // 사용자 설정
        emailNotification: boolean;      // 이메일 알림 설정
        pushNotification: boolean;       // 푸시 알림 설정
        marketingConsent: boolean;       // 마케팅 수신 동의
    };
    socialProvider?: 'GOOGLE' | 'KAKAO' | 'NAVER';  // 소셜 로그인 제공자 (선택적)
}
// 나머지 인터페이스들은 동일하게 유지
export interface AuthState {
    isLogin: boolean;
    user: User | null;
    loading: boolean;
    error: string | null;
}
export interface LoginResponse {
    success: boolean;
    data?: {
        user: User;
    };
}
