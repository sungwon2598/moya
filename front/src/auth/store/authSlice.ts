import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { AuthState, User } from '../types/auth.types';
import { getUserInfo, postLoginToken } from '../api/authApi';

const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null
};

export const checkLoginStatus = createAsyncThunk<User | false>(
    'auth/checkLoginStatus',
    async () => {
        const userInfo = await getUserInfo();
        return userInfo;
    }
);

export const loginWithGoogle = createAsyncThunk<User | false, string>(
    'auth/loginWithGoogle',
    async (credential) => {
        const success = await postLoginToken(credential);
        if (success) {
            const userInfo = await getUserInfo();
            return userInfo;
        }
        return false;
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
                state.error = action.error.message || 'Failed to check login status';
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
                state.error = action.error.message || 'Google login failed';
            });
    }
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
