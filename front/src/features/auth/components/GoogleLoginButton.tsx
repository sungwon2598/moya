import { FC, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '@store/store';
import { loginWithGoogle } from '../store/authSlice';
import { useScript } from '../hooks/useScript';
import {GoogleAuthResponse, GoogleCredentialResponse} from '../types/auth.types';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const GOOGLE_SCRIPT_URL = 'https://accounts.google.com/gsi/client';

interface GoogleButtonProps {
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    size?: 'large' | 'medium' | 'small';
    width?: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

export const GoogleLoginButton: FC<GoogleButtonProps> = ({
                                                             text = 'signin_with',
                                                             theme = 'filled_blue',
                                                             size = 'large',
                                                             width = '250',
                                                             onSuccess,
                                                             onError
                                                         }) => {
    const buttonRef = useRef<HTMLDivElement>(null);
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();

    const handleCredentialResponse = async (response: GoogleCredentialResponse) => {
        try {
            if (!response.credential || !response.code) {
                throw new Error('Invalid Google response');
            }

            const authData: GoogleAuthResponse = {
                idToken: response.credential,
                authCode: response.code
            };

            await dispatch(loginWithGoogle(authData)).unwrap();
            onSuccess?.();
            navigate('/');
        } catch (error) {
            console.error('[Google OAuth] Login failed:', error);
            onError?.(error instanceof Error ? error : new Error('Login failed'));
        }
    };

    const initializeGoogleButton = () => {
        if (!buttonRef.current || !window.google?.accounts?.id) return;

        try {
            const google = window.google;
            google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: handleCredentialResponse,
                auto_select: false,
                scope: 'email profile https://www.googleapis.com/auth/calendar.readonly',
                ux_mode: 'popup',
                response_type: 'code id_token'
            });

            google.accounts.oauth2.initCodeClient({
                client_id: GOOGLE_CLIENT_ID,
                scope: 'email profile https://www.googleapis.com/auth/calendar.readonly',
                ux_mode: 'popup',
                callback: (response: any) => {
                    if (response.code) {
                        handleCredentialResponse({
                            credential: response.id_token || '',
                            code: response.code
                        });
                    }
                },
            });

            google.accounts.id.renderButton(buttonRef.current, {
                type: 'standard',
                theme,
                size,
                text,
                width: width
            });
        } catch (error) {
            console.error('[Google OAuth] Initialization error:', error);
            onError?.(error instanceof Error ? error : new Error('Initialization failed'));
        }
    };

    const scriptLoaded = useScript(GOOGLE_SCRIPT_URL, initializeGoogleButton);

    useEffect(() => {
        return () => {
            window.google?.accounts?.id?.cancel();
        };
    }, []);

    if (!scriptLoaded) {
        return (
            <div
                className="w-[250px] h-[40px] bg-gray-100 rounded animate-pulse"
                aria-label="Loading Google Sign-In"
            />
        );
    }

    return (
        <div
            ref={buttonRef}
            className="google-login-button"
            role="button"
            aria-label="Sign in with Google"
        />
    );
};
