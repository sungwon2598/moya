import { FC, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '@store/store';
import { loginWithGoogle } from '../store/authSlice';
import { GoogleAuthResponse } from '../types/auth.types';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_APP_GOOGLE_CLIENT_ID;
const GOOGLE_SCRIPT_URL = 'https://accounts.google.com/gsi/client';

interface GoogleButtonProps {
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    size?: 'large' | 'medium' | 'small';
    width?: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

const useGoogleScript = () => {
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        const script = document.createElement('script');
        script.src = GOOGLE_SCRIPT_URL;
        script.async = true;
        script.onload = () => setIsLoaded(true);

        document.body.appendChild(script);

        return () => {
            document.body.removeChild(script);
        };
    }, []);

    return isLoaded;
};

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
    const isScriptLoaded = useGoogleScript();
    const [isInitialized, setIsInitialized] = useState(false);

    useEffect(() => {
        if (!isScriptLoaded || !window.google?.accounts?.id || isInitialized) {
            return;
        }

        try {
            window.google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: async (response) => {
                    try {
                        if (!response.credential) {
                            throw new Error('No credentials received');
                        }

                        const authData: GoogleAuthResponse = {
                            idToken: response.credential,
                            authCode: ''
                        };

                        await dispatch(loginWithGoogle(authData)).unwrap();
                        onSuccess?.();
                        navigate('/');
                    } catch (error) {
                        console.error('Login failed:', error);
                        onError?.(error instanceof Error ? error : new Error('Login failed'));
                    }
                }
            });

            setIsInitialized(true);
        } catch (error) {
            console.error('Failed to initialize Google Sign-In:', error);
            onError?.(error instanceof Error ? error : new Error('Initialization failed'));
        }
    }, [isScriptLoaded, dispatch, navigate, onSuccess, onError, isInitialized]);

    useEffect(() => {
        if (!isInitialized || !buttonRef.current || !window.google?.accounts?.id) {
            return;
        }

        try {
            window.google.accounts.id.renderButton(buttonRef.current, {
                type: 'standard',
                theme,
                size,
                text,
                width: parseInt(width)
            });

            console.log('Google Sign-In button rendered');
        } catch (error) {
            console.error('Failed to render button:', error);
            onError?.(error instanceof Error ? error : new Error('Failed to render button'));
        }
    }, [isInitialized, theme, size, text, width, onError]);

    if (!isScriptLoaded || !isInitialized) {
        return (
            <div
                className="w-64 h-10 bg-gray-100 rounded animate-pulse"
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

export default GoogleLoginButton;
