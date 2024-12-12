import { FC, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '@store/store';
import { loginWithGoogle } from '../store/authSlice';
import { GoogleAuthResponse, GoogleCredentialResponse } from '../types/auth.types';

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

// useScript hook
export const useScript = (src: string, onLoad?: () => void) => {
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        const script = document.createElement('script');
        script.src = src;
        script.async = true;

        const handleLoad = () => {
            setLoaded(true);
            if (onLoad) {
                setTimeout(onLoad, 100); // Google API 초기화를 위한 약간의 지연
            }
        };

        script.addEventListener('load', handleLoad);
        document.body.appendChild(script);

        return () => {
            script.removeEventListener('load', handleLoad);
            document.body.removeChild(script);
        };
    }, [src, onLoad]);

    return loaded;
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

    // 환경변수 체크
    if (!GOOGLE_CLIENT_ID) {
        console.error('Google Client ID is not defined');
        return null;
    }

    const handleCredentialResponse = async (response: GoogleCredentialResponse) => {
        try {
            console.log('Google Response:', response);

            if (!response.credential) {
                throw new Error('Missing credential in Google response');
            }

            const authData: GoogleAuthResponse = {
                idToken: response.credential,
                authCode: response.code || ''
            };

            await dispatch(loginWithGoogle(authData)).unwrap();
            onSuccess?.();
            navigate('/');
        } catch (error) {
            console.error('[Google OAuth] Login failed:', error);
            onError?.(error instanceof Error ? error : new Error('Login failed'));
        }
    };

    const scriptLoaded = useScript(GOOGLE_SCRIPT_URL);

    useEffect(() => {
        if (scriptLoaded && buttonRef.current && window.google?.accounts?.id) {
            try {
                console.log('Initializing Google Sign-In with:', {
                    client_id: GOOGLE_CLIENT_ID,
                    buttonRef: buttonRef.current
                });

                window.google.accounts.id.initialize({
                    client_id: GOOGLE_CLIENT_ID,
                    callback: handleCredentialResponse,
                    auto_select: false,
                    scope: 'email profile',
                    ux_mode: 'redirect'
                });

                window.google.accounts.id.renderButton(buttonRef.current, {
                    type: 'standard',
                    theme,
                    size,
                    text,
                    width: parseInt(width)
                });

                // One tap prompt
                window.google.accounts.id.prompt();

            } catch (error) {
                console.error('[Google OAuth] Initialization error:', {
                    error,
                    message: error instanceof Error ? error.message : 'Unknown error',
                    stack: error instanceof Error ? error.stack : undefined
                });
                onError?.(error instanceof Error ? error : new Error('Initialization failed'));
            }
        }

        return () => {
            window.google?.accounts?.id?.cancel();
        };
    }, [scriptLoaded, buttonRef.current, theme, size, text, width]);

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

// 타입 선언 추가
declare global {
    interface Window {
        google?: {
            accounts?: {
                id?: {
                    initialize: (config: any) => void;
                    renderButton: (element: HTMLElement, options: any) => void;
                    prompt: () => void;
                    cancel: () => void;
                };
            };
        };
    }
}
