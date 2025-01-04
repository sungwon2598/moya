import axios, { AxiosInstance } from "axios";
import type { User, GoogleAuthResponse } from "../types/auth.types";

export const BASE_URL =
    import.meta.env.VITE_API_URL || "https://api.moyastudy.com";

export const OAUTH_ENDPOINTS = {
  LOGIN: "/v1/oauth/login",
  LOGOUT: "/v1/oauth/logout",
  USER_INFO: "/v1/oauth/user/info",
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

export const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: BASE_URL,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    withCredentials: true,
  });

  return instance;
};

export const axiosInstance = createAxiosInstance();

export const postGoogleAuth = async (
    authData: GoogleAuthResponse,
): Promise<ApiResponse<AuthResponseData>> => {
  try {
    const response = await axiosInstance.post<User>(OAUTH_ENDPOINTS.LOGIN, {
      ...authData,
      redirectUri: window.location.origin, // 현재 도메인을 redirectUri로 추가
    });

    if (!response.data) {
      throw new Error("No response data received");
    }

    return {
      success: true,
      data: { user: response.data },
    };
  } catch (error) {
    console.error("[Auth API] Google auth failed:", error);
    return {
      success: false,
      message: error instanceof Error ? error.message : "Authentication failed",
    };
  }
};

export const logout = async (): Promise<void> => {
  try {
    await axiosInstance.post(OAUTH_ENDPOINTS.LOGOUT);
  } catch (error) {
    console.error("[Auth API] Logout failed:", error);
    throw error;
  }
};

export const getUserInfo = async (): Promise<User> => {
  try {
    const response = await axiosInstance.get<ApiResponse<User>>(
        OAUTH_ENDPOINTS.USER_INFO,
    );

    if (!response.data.success || !response.data.data) {
      throw new Error("Failed to get user info");
    }

    return response.data.data;
  } catch (error) {
    console.error("[Auth API] Get user info failed:", error);
    throw error;
  }
};
