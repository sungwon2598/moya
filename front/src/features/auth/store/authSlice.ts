import { createSlice, createAsyncThunk, PayloadAction, Reducer } from '@reduxjs/toolkit';
import { AuthState, GoogleAuthResponse, User } from '../types/auth.types';
import { getUserInfo, postGoogleAuth, refreshAccessToken, logout } from '../api/authApi';
import type { AuthResponseData } from '../api/authApi';
import { TokenStorage } from '../api/authApi';

const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null,
    tokens: undefined
};

export const authenticateWithGoogleThunk = createAsyncThunk<AuthResponseData, GoogleAuthResponse>(
    'auth/authenticateWithGoogle',
    async (authData, { rejectWithValue }) => {
        try {
            const response = await postGoogleAuth(authData);

            if (!response.success || !response.data) {
                throw new Error(response.message || 'Authentication failed');
            }

            return response.data;
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Authentication failed'
            );
        }
    }
);

export const checkLoginStatus = createAsyncThunk<User>(
    'auth/checkLoginStatus',
    async (_, { rejectWithValue }) => {
        try {
            return await getUserInfo();
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Failed to fetch user info'
            );
        }
    }
);

// export const refreshAuthToken = createAsyncThunk(
//     'auth/refreshToken',
//     async (_, { rejectWithValue }) => {
//         try {
//             const refreshToken = TokenStorage.getRefreshToken();
//             if (!refreshToken) {
//                 throw new Error('No refresh token available');
//             }
//             return await refreshAccessToken(refreshToken);
//         } catch (error) {
//             return rejectWithValue(
//                 error instanceof Error ? error.message : 'Token refresh failed'
//             );
//         }
//     }
// );

// export const refreshAuthToken = createAsyncThunk(
//     'auth/refreshToken',
//     async (_, { rejectWithValue }) => {
//         try {
//             return await refreshAccessToken();  // refreshToken 파라미터 제거
//         } catch (error) {
//             return rejectWithValue(
//                 error instanceof Error ? error.message : 'Token refresh failed'
//             );
//         }
//     }
// ); //이전꺼

export const refreshAuthToken = createAsyncThunk(
    'auth/refreshToken',
    async (_, { rejectWithValue }) => {
        try {
            await refreshAccessToken();
            return {}; // 성공만 반환
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Token refresh failed'
            );
        }
    }
);

export const logoutUser = createAsyncThunk<void, void>(
    'auth/logout',
    async (_, { rejectWithValue }) => {
        try {
            await logout();
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Logout failed'
            );
        }
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        clearError: (state) => {
            state.error = null;
        },
        updateTokens: (state, action: PayloadAction<AuthResponseData>) => {
            state.tokens = {
                accessToken: action.payload.accessToken,
                refreshToken: action.payload.refreshToken
            };
        },
        resetAuth: () => {
            return initialState;
        }
    },
    extraReducers: (builder) => {
        builder
            // Google Authentication
            .addCase(authenticateWithGoogleThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(authenticateWithGoogleThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload.user;
                state.tokens = {
                    accessToken: action.payload.accessToken,
                    refreshToken: action.payload.refreshToken
                };
                state.error = null;
            })
            .addCase(authenticateWithGoogleThunk.rejected, (state, action) => {
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
            // .addCase(refreshAuthToken.fulfilled, (state, action) => {
            //     if (action.payload) {
            //         state.tokens = {
            //             accessToken: action.payload.accessToken,
            //             refreshToken: action.payload.refreshToken
            //         };
            //     }
            //     state.error = null;
            // })

            .addCase(refreshAuthToken.fulfilled, (state) => {
                state.error = null;
            })

            .addCase(refreshAuthToken.rejected, (state, action) => {
                state.isLogin = false;
                state.user = null;
                state.tokens = undefined;
                state.error = action.payload as string;
            })
            // Logout
            .addCase(logoutUser.fulfilled, () => {
                return initialState;
            })
            .addCase(logoutUser.rejected, () => {
                return initialState;
            });
    }
});

export const { clearError, updateTokens, resetAuth } = authSlice.actions;

export const selectIsAuthenticated = (state: { auth: AuthState }) => state.auth.isLogin;
export const selectUser = (state: { auth: AuthState }) => state.auth.user;
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.loading;
export const selectAuthError = (state: { auth: AuthState }) => state.auth.error;
export const selectAuthTokens = (state: { auth: AuthState }) => state.auth.tokens;

const authReducer: Reducer<AuthState> = authSlice.reducer;
export default authReducer;