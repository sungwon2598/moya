import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { AuthState, GoogleAuthResponse, User } from '../types/auth.types';
import { getUserInfo, postGoogleAuth, refreshToken, logout as logoutApi } from '../api/authApi';

// Response types
interface LoginResponse {
    user: User;
    accessToken: string;
    refreshToken?: string;
}

interface TokenResponse {
    accessToken: string;
    refreshToken?: string;
}

// Initial state
const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null,
    tokens: undefined
};

// Async thunks
export const loginWithGoogle = createAsyncThunk<LoginResponse, GoogleAuthResponse>(
    'auth/loginWithGoogle',
    async (authData, { rejectWithValue }) => {
        try {
            const response = await postGoogleAuth(authData);

            if (!response.success || !response.data) {
                throw new Error(response.message || 'Login failed');
            }

            return response.data;
        } catch (error) {
            console.error('[Auth Slice] Google login error:', error);
            return rejectWithValue(
                error instanceof Error ? error.message : 'Google login failed'
            );
        }
    }
);

export const checkLoginStatus = createAsyncThunk<User>(
    'auth/checkLoginStatus',
    async (_, { rejectWithValue }) => {
        try {
            const userInfo = await getUserInfo();

            if (!userInfo) {
                throw new Error('Failed to fetch user info');
            }

            return userInfo;
        } catch (error) {
            console.error('[Auth Slice] Check login status error:', error);
            return rejectWithValue(
                error instanceof Error ? error.message : 'Failed to fetch user info'
            );
        }
    }
);

export const refreshAuthToken = createAsyncThunk<TokenResponse>(
    'auth/refreshToken',
    async (_, { getState, rejectWithValue }) => {
        try {
            const state = getState() as { auth: AuthState };
            const currentRefreshToken = state.auth.tokens?.refreshToken;

            if (!currentRefreshToken) {
                throw new Error('No refresh token available');
            }

            const newAccessToken = await refreshToken(currentRefreshToken);
            return { accessToken: newAccessToken, refreshToken: currentRefreshToken };
        } catch (error) {
            console.error('[Auth Slice] Token refresh error:', error);
            return rejectWithValue(
                error instanceof Error ? error.message : 'Token refresh failed'
            );
        }
    }
);

export const logoutUser = createAsyncThunk<
    { success: boolean },
    void,
    { rejectValue: string }
>('auth/logout', async (_, { rejectWithValue }) => {
    try {
        await logoutApi();
        return { success: true };
    } catch (error) {
        console.error('[Auth Slice] Logout error:', error);
        return rejectWithValue(
            error instanceof Error ? error.message : 'Logout failed'
        );
    }
});

// Slice
const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        updateTokens: (state, action: PayloadAction<TokenResponse>) => {
            state.tokens = action.payload;
        },
        resetAuth: (state) => {
            Object.assign(state, initialState);
        }
    },
    extraReducers: (builder) => {
        builder
            // Google Login
            .addCase(loginWithGoogle.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(loginWithGoogle.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload.user;
                state.tokens = {
                    accessToken: action.payload.accessToken,
                    refreshToken: action.payload.refreshToken
                };
                state.error = null;
            })
            .addCase(loginWithGoogle.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.tokens = undefined;
                state.error = action.payload as string;
            })
            // Check Login Status
            .addCase(checkLoginStatus.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(checkLoginStatus.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload;
                state.error = null;
            })
            .addCase(checkLoginStatus.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.tokens = undefined;
                state.error = action.payload as string;
            })
            // Refresh Token
            .addCase(refreshAuthToken.fulfilled, (state, action) => {
                state.tokens = action.payload;
                state.error = null;
            })
            .addCase(refreshAuthToken.rejected, (state, action) => {
                state.isLogin = false;
                state.user = null;
                state.tokens = undefined;
                state.error = action.payload as string;
            })
            // Logout
            .addCase(logoutUser.fulfilled, (state) => {
                Object.assign(state, initialState);
            })
            .addCase(logoutUser.rejected, (state) => {
                Object.assign(state, initialState);
            });
    }
});

// Actions
export const { clearError, updateTokens, resetAuth } = authSlice.actions;

// Selectors
export const selectIsAuthenticated = (state: { auth: AuthState }) => state.auth.isLogin;
export const selectUser = (state: { auth: AuthState }) => state.auth.user;
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.loading;
export const selectAuthError = (state: { auth: AuthState }) => state.auth.error;
export const selectAuthTokens = (state: { auth: AuthState }) => state.auth.tokens;

// Reducer
export default authSlice.reducer;
