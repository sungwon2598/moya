export interface User {
  email?: string;
  nickname?: string;
  roles?: string[];
  status?: string;
  profileImageUrl?: string;
  accessToken?: string;
  data?: {
    accessToken?: string;
    refreshToken?: string;
    profileImageUrl?: string;
    email?: string;
    nickname?: string;
    roles?: [string];
    status?: string;
  };
}

export interface AuthState {
  isLogin: boolean;
  user: User | null;
  loading: boolean;
  error: string | null;
  accessToken?: string | null;
}

export interface GoogleAuthResponse {
  credential: string;
  authCode: string;
}

export interface GoogleCredentialResponse {
  credential: string;
}

export interface GoogleCodeResponse {
  code: string;
}

export interface AuthResponseData {
  data: User;
}
