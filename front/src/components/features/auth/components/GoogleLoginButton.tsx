import { FC, useEffect, useRef, useState } from 'react';
import { GoogleAuthResponse, GoogleCredentialResponse, GoogleCodeResponse } from '../types/auth.types';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_APP_GOOGLE_CLIENT_ID;
const GOOGLE_SCRIPT_URL = 'https://accounts.google.com/gsi/client';

interface GoogleButtonProps {
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    size?: 'large' | 'medium' | 'small';
    width?: string;
    onSuccess: (authData: GoogleAuthResponse) => void;
    onError?: (error: Error) => void;
}

const useGoogleScript = () => {
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (window.google?.accounts) {
            setIsLoaded(true);
            return;
        }

        const script = document.createElement('script');
        script.src = GOOGLE_SCRIPT_URL;
        script.async = true;
        script.defer = true;
        script.onload = () => setIsLoaded(true);
        script.onerror = () => {
            console.error('Failed to load Google script');
            setIsLoaded(false);
        };

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
    const isScriptLoaded = useGoogleScript();
    const [isInitialized, setIsInitialized] = useState(false);

    useEffect(() => {
        if (!isScriptLoaded || isInitialized || !window.google?.accounts) {
            return;
        }

        try {
            const google = window.google;

            if (!google?.accounts) {
                throw new Error('Google accounts not available');
            }

            google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: async (credentialResponse: GoogleCredentialResponse) => {
                    try {
                        if (!credentialResponse.credential) {
                            throw new Error('No credential received');
                        }

                        const oauth2Client = google.accounts.oauth2.initCodeClient({
                            client_id: GOOGLE_CLIENT_ID,
                            scope: 'email profile openid',
                            callback: async (codeResponse: GoogleCodeResponse) => {
                                if (!codeResponse.code) {
                                    throw new Error('No authorization code received');
                                }

                                const authData: GoogleAuthResponse = {
                                    credential: credentialResponse.credential,
                                    authCode: codeResponse.code
                                };

                                onSuccess(authData);
                            },
                            ux_mode: 'popup',
                            access_type: 'offline',
                        });

                        oauth2Client.requestCode();
                    } catch (error) {
                        console.error('Login failed:', error);
                        onError?.(error instanceof Error ? error : new Error('Login failed'));
                    }
                },
                auto_select: false
            });

            setIsInitialized(true);
        } catch (error) {
            console.error('Failed to initialize Google Sign-In:', error);
            onError?.(error instanceof Error ? error : new Error('Initialization failed'));
        }
    }, [isScriptLoaded, onSuccess, onError, isInitialized]);

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

            window.google.accounts.id.prompt();
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
