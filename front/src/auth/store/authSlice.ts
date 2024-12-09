import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { AuthState, User } from '../types/auth.types';
import { getUserInfo, postLoginToken } from '../api/authApi';

const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null
};

// 사용자 정보 체크 thunk
export const checkLoginStatus = createAsyncThunk<User | false>(
    'auth/checkLoginStatus',
    async (_, { rejectWithValue }) => {
        try {
            const userInfo = await getUserInfo();
            return userInfo;
        } catch (error) {
            return rejectWithValue('Failed to fetch user info');
        }
    }
);

// Google 로그인 thunk
export const loginWithGoogle = createAsyncThunk<User | false, string>(
    'auth/loginWithGoogle',
    async (credential, { rejectWithValue }) => {
        try {
            const loginResponse = await postLoginToken(credential);
            if (!loginResponse.success) {
                throw new Error('Login failed');
            }

            // 로그인 성공 시 사용자 정보 조회
            const userInfo = await getUserInfo();
            if (!userInfo) {
                throw new Error('Failed to fetch user info after login');
            }

            return userInfo;
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Google login failed'
            );
        }
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logout: (state) => {
            state.isLogin = false;
            state.user = null;
            state.error = null;
        },
        clearError: (state) => {
            state.error = null;
        }
    },
    extraReducers: (builder) => {
        builder
            // 초기 로그인 상태 체크
            .addCase(checkLoginStatus.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(checkLoginStatus.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = !!action.payload;
                state.user = action.payload || null;
            })
            .addCase(checkLoginStatus.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.error = action.payload as string || 'Failed to check login status';
            })
            // Google 로그인
            .addCase(loginWithGoogle.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(loginWithGoogle.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = !!action.payload;
                state.user = action.payload || null;
            })
            .addCase(loginWithGoogle.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.error = action.payload as string || 'Google login failed';
            });
    }
});

export const { logout, clearError } = authSlice.actions;
export default authSlice.reducer;
