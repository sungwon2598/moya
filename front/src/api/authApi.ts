import axios, { AxiosInstance } from "axios";
import { createBrowserHistory } from "history";
import { TokenStorage } from "@/utils/tokenUtils";
import type { User, GoogleAuthResponse } from "@/types/auth.types";

export const BASE_URL = import.meta.env.VITE_API_URL || "https://api.moyastudy.com";

const history = createBrowserHistory();

export const OAUTH_ENDPOINTS = {
  LOGIN: "/v1/oauth/login",
  LOGOUT: "/v1/oauth/logout",
  USER_INFO: "/api/auth/user",
  REFRESH: "/v1/oauth/refresh"
  // /v1/oauth/user/info
} as const;

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  errorCode?: string;
}

export interface AuthResponseData {
  user: User;
}

export class AuthApiError extends Error {
  constructor(
    message: string,
    public statusCode?: number,
    public errorCode?: string,
  ) {
    super(message);
    this.name = "AuthApiError";
  }
}

// 요청 재시도 설정
const MAX_RETRIES = 3;
const RETRY_DELAY = 1000;

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: BASE_URL,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    timeout: 30000,
  });

  // 요청 인터셉터
  instance.interceptors.request.use(
    async (config) => {
      const token = TokenStorage.getAccessToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      
        // 토큰이 만료된 경우 갱신 시도
        if (
          TokenStorage.isTokenExpired(token) &&
          config.url !== OAUTH_ENDPOINTS.REFRESH
        ) {
          try {
            const refreshToken = TokenStorage.getRefreshToken();
            if (!refreshToken) {
              throw new Error("리프레시 토큰 없음");
            }
            
            const tokens = await refreshTokens(refreshToken);
            if (tokens) {
              TokenStorage.setTokens(tokens.accessToken, tokens.refreshToken);
              config.headers.Authorization = `Bearer ${tokens.accessToken}`;
            } else {
              throw new Error("토큰 갱신 실패");
            }
          } catch (error) {
            // 토큰 갱신 실패 시 세션 스토리지에서 토큰 제거
            TokenStorage.clearTokens();
            history.replace("/login");
            throw error;
          }
        }
      }
      return config;
    },
    (error) => {
      console.error("[API] 요청 인터셉터 에러:", error);
      return Promise.reject(
        new AuthApiError("요청 설정 실패", undefined, undefined)
      );
    }
  );

  // 응답 인터셉터
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (axios.isAxiosError(error)) {
        const originalRequest = error.config;
        
        if (!originalRequest) {
          throw new AuthApiError("요청 설정을 사용할 수 없음", error.response?.status);
        }
        
        // 재시도 속성이 없으면 초기화
        if (originalRequest._retry === undefined) {
          originalRequest._retry = 0;
        }
        
        // 401 오류 처리 (인증 실패)
        if (error.response?.status === 401) {
          // 리프레시 토큰 갱신 중 오류 발생
          if (originalRequest.url === OAUTH_ENDPOINTS.REFRESH) {
            TokenStorage.clearTokens();
            history.replace("/login");
            throw new AuthApiError("세션 만료", 401);
          }
          
          // 재시도 횟수가 최대 시도 횟수보다 작으면 재시도
          if (originalRequest._retry < MAX_RETRIES) {
            originalRequest._retry += 1;
            await sleep(RETRY_DELAY * originalRequest._retry);
            
            try {
              const refreshToken = TokenStorage.getRefreshToken();
              if (!refreshToken) {
                throw new Error("리프레시 토큰 없음");
              }
              
              const tokens = await refreshTokens(refreshToken);
              if (tokens) {
                TokenStorage.setTokens(tokens.accessToken, tokens.refreshToken);
                originalRequest.headers.Authorization = `Bearer ${tokens.accessToken}`;
                return instance(originalRequest);
              } else {
                throw new Error("토큰 갱신 실패");
              }
            } catch (refreshError) {
              console.log(refreshError)
              TokenStorage.clearTokens();
              history.replace("/login");
              throw new AuthApiError("인증 실패", 401);
            }
          }
        }
        
        // 429 오류 처리 (너무 많은 요청)
        if (error.response?.status === 429 && originalRequest._retry < MAX_RETRIES) {
          originalRequest._retry += 1;
          await sleep(RETRY_DELAY * originalRequest._retry);
          return instance(originalRequest);
        }
        
        // 기타 오류
        const errorResponse = error.response?.data as ApiResponse<unknown>;
        throw new AuthApiError(
          errorResponse?.message || error.message,
          error.response?.status,
          errorResponse?.errorCode
        );
      }
      
      return Promise.reject(error);
    }
  );

  return instance;
};

export const axiosInstance = createAxiosInstance();

// Google 로그인
export const postGoogleAuth = async (
  authData: GoogleAuthResponse
): Promise<ApiResponse<AuthResponseData>> => {
  try {
    const response = await axiosInstance.post<User>(OAUTH_ENDPOINTS.LOGIN, {
      ...authData,
      redirectUri: window.location.origin,
    });

    if (!response.data) {
      throw new Error("응답 데이터 없음");
    }

    return {
      success: true,
      data: { user: response.data },
    };
  } catch (error) {
    console.error("[Auth API] Google 인증 실패:", error);
    return {
      success: false,
      message: error instanceof Error ? error.message : "인증 실패",
    };
  }
};

// 로그아웃
export const logout = async (): Promise<void> => {
  try {
    await axiosInstance.post(OAUTH_ENDPOINTS.LOGOUT);
  } catch (error) {
    console.error("[Auth API] 로그아웃 실패:", error);
  } finally {
    TokenStorage.clearTokens();
  }
};

export const refreshTokens = async (refreshToken: string): Promise<{
  accessToken: string;
  refreshToken: string;
} | null> => {
  try {
    const response = await axios.post(`${BASE_URL}${OAUTH_ENDPOINTS.REFRESH}`, {
      refreshToken
    });
    
    if (response.data?.accessToken && response.data?.refreshToken) {
      return {
        accessToken: response.data.accessToken,
        refreshToken: response.data.refreshToken
      };
    }
    return null;
  } catch (error) {
    console.error("토큰 갱신 실패:", error);
    throw error;
  }
};

// 사용자 정보 가져오기
export const getUserInfo = async (): Promise<User> => {
  const token = TokenStorage.getAccessToken();
  console.log("사용자 정보 요청 - 토큰:", token ? "있음" : "없음");
  
  if (!token) {
    throw new Error("인증 토큰이 없습니다");
  }
  
  try {
    console.log("API 요청 시작:", OAUTH_ENDPOINTS.USER_INFO);
    // Authorization 헤더와 함께 요청 보내기
    const response = await axiosInstance.get<ApiResponse<User>>(
      OAUTH_ENDPOINTS.USER_INFO
    );
    console.log("API 응답:", response.status, response.data);

    if (!response.data.success || !response.data.data) {
      throw new Error("사용자 정보 가져오기 실패");
    }

    return response.data.data;
  } catch (error) {
    console.error("사용자 정보 요청 오류:", error);
    
    if (axios.isAxiosError(error)) {
      console.error("상태 코드:", error.response?.status);
      console.error("응답 데이터:", error.response?.data);
      
      if (error.response?.status === 401) {
        // 401 오류일 때만 토큰 제거
        TokenStorage.clearTokens(); 
        throw new Error("인증이 만료되었습니다. 다시 로그인해주세요.");
      }
    }
    throw error;
  }
};

// Axios 타입 선언
declare module "axios" {
  export interface AxiosRequestConfig {
    _retry?: number;
  }
}