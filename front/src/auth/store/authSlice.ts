import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { AuthState } from '../types/auth.types';
import { authApi } from '../api/authApi';

const initialState: AuthState = {
    isAuthenticated: false,
    user: null,
    loading: false,
    error: null,
};

export const loginWithGoogle = createAsyncThunk(
    'auth/loginWithGoogle',
    async (credential: string) => {
        const result = await authApi.postLoginToken(credential);
        if (!result) throw new Error('Login failed');
        const userInfo = await authApi.getUserInfo();
        return userInfo;
    }
);

export const fetchUserInfo = createAsyncThunk(
    'auth/fetchUserInfo',
    async () => {
        const userInfo = await authApi.getUserInfo();
        if (!userInfo) throw new Error('Failed to fetch user info');
        return userInfo;
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logout: (state) => {
            state.isAuthenticated = false;
            state.user = null;
            state.error = null;
        },
        clearError: (state) => {
            state.error = null;
        },
    },
    extraReducers: (builder) => {
        builder
            // Login cases
            .addCase(loginWithGoogle.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(loginWithGoogle.fulfilled, (state, action) => {
                state.isAuthenticated = true;
                state.user = action.payload;
                state.loading = false;
            })
            .addCase(loginWithGoogle.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Login failed';
            })
            // Fetch user info cases
            .addCase(fetchUserInfo.pending, (state) => {
                state.loading = true;
            })
            .addCase(fetchUserInfo.fulfilled, (state, action) => {
                state.isAuthenticated = true;
                state.user = action.payload;
                state.loading = false;
            })
            .addCase(fetchUserInfo.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || 'Failed to fetch user info';
            });
    },
});

export const { logout, clearError } = authSlice.actions;
export default authSlice.reducer;
