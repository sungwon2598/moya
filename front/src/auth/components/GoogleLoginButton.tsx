import { FC, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useScript } from '../hooks/useScript';
import { useAuth } from '../hooks/useAuth';
import type { CredentialResponse } from 'google-one-tap';

const GOOGLE_CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID!;

export const GoogleLoginButton: FC = () => {
    const buttonRef = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();
    const { handleGoogleLogin, isAuthenticated } = useAuth();
    const googleScriptLoaded = useScript('https://accounts.google.com/gsi/client');

    useEffect(() => {
        if (googleScriptLoaded && buttonRef.current) {
            console.log('[Google OAuth] Script loaded, initializing...');

            window.google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: async (response: CredentialResponse) => {
                    console.log('[Google OAuth] Received credential response');

                    if (response.credential) {
                        try {
                            console.log('[Google OAuth] Attempting login with credential');
                            await handleGoogleLogin(response.credential);
                            console.log('[Google OAuth] Login successful, redirecting to main page');
                            navigate('/');
                        } catch (error) {
                            console.error('[Google OAuth] Login failed:', error);
                        }
                    }
                }
            });

            window.google.accounts.id.renderButton(buttonRef.current, {
                theme: 'outline',
                size: 'large',
                type: 'standard'
            });

            console.log('[Google OAuth] Button rendered');
        }
    }, [googleScriptLoaded, handleGoogleLogin, navigate]);

    // 로그인 상태 변경 감지 및 리다이렉션
    useEffect(() => {
        if (isAuthenticated) {
            console.log('[Auth] User authenticated, redirecting to main page');
            navigate('/');
        }
    }, [isAuthenticated, navigate]);

    return <div ref={buttonRef} />;
};

// api/authApi.ts
export const authenticateWithGoogle = async (credential: string) => {
    console.log('[API] Sending credential to backend');

    try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/auth/google`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ credential })
        });

        if (!response.ok) {
            console.error('[API] Backend authentication failed:', response.status);
            throw new Error('Authentication failed');
        }

        console.log('[API] Backend authentication successful');
        return response.json();
    } catch (error) {
        console.error('[API] Network error:', error);
        throw error;
    }
};
