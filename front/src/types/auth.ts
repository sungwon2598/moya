export interface MemberInfo {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
}

// apiConfig.ts에서 사용하는 Response 타입과 통일
export interface AuthResponse {
    token: string;
    memberInfo: MemberInfo;
}

// OAuth 콜백에서 사용할 확장된 Response 타입
export interface OAuth2Response {
    status: 'NEEDS_SIGNUP' | 'SUCCESS' | 'ERROR';
    data: {
        accessToken: string;
        refreshToken?: string;
        memberInfo?: MemberInfo;
        nextStep?: 'SIGNUP' | 'MAIN';
    };
    error?: {
        code: string;
        message: string;
    };
}
