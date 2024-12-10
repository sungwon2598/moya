import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { AuthState, User } from '../types/auth.types';
import { getUserInfo, postLoginToken } from '../api/authApi';

const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null
};

// Google 로그인 thunk의 반환 타입을 User로 변경
export const loginWithGoogle = createAsyncThunk<User, string>(
    'auth/loginWithGoogle',
    async (credential, { rejectWithValue }) => {
        try {
            const loginResponse = await postLoginToken(credential);
            if (!loginResponse.success || !loginResponse.data) {
                throw new Error('Login failed');
            }
            return loginResponse.data.user;
        } catch (error) {
            return rejectWithValue(
                error instanceof Error ? error.message : 'Google login failed'
            );
        }
    }
);

// checkLoginStatus의 반환 타입도 User로 변경
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
            return rejectWithValue('Failed to fetch user info');
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
            .addCase(loginWithGoogle.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(loginWithGoogle.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload;  // User 타입
            })
            .addCase(loginWithGoogle.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.error = action.payload as string;
            })
            .addCase(checkLoginStatus.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(checkLoginStatus.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload;  // User 타입
            })
            .addCase(checkLoginStatus.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.error = action.payload as string;
            });
    }
});

export const { logout, clearError } = authSlice.actions;
export default authSlice.reducer;
