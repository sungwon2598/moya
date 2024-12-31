import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {AuthState, GoogleAuthResponse, User} from '../types/auth.types';
import { getUserInfo, postGoogleAuth, logout } from '../api/authApi';
import type { AuthResponseData } from '../api/authApi';

const initialState: AuthState = {
    isLogin: false,
    user: null,
    loading: false,
    error: null
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
        resetAuth: () => {
            return initialState;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(authenticateWithGoogleThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(authenticateWithGoogleThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.isLogin = true;
                state.user = action.payload.user;
                state.error = null;
            })
            .addCase(authenticateWithGoogleThunk.rejected, (state, action) => {
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
                state.user = action.payload;
                state.error = null;
            })
            .addCase(checkLoginStatus.rejected, (state, action) => {
                state.loading = false;
                state.isLogin = false;
                state.user = null;
                state.error = action.payload as string;
            })
            .addCase(logoutUser.fulfilled, () => {
                return initialState;
            })
            .addCase(logoutUser.rejected, () => {
                return initialState;
            });
    }
});

export const { clearError, resetAuth } = authSlice.actions;

export const selectIsAuthenticated = (state: { auth: AuthState }) => state.auth.isLogin;
export const selectUser = (state: { auth: AuthState }) => state.auth.user;
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.loading;
export const selectAuthError = (state: { auth: AuthState }) => state.auth.error;

export default authSlice.reducer;
