import { FC, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '../../store';
import { loginWithGoogle } from '../store/authSlice';
import { useScript } from '../hooks/useScript';

interface GoogleCredentialResponse {
    credential: string;
    select_by: string;
    clientId?: string;
}

interface GoogleButtonProps {
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    size?: 'large' | 'medium' | 'small';
    width?: string;
}

const GOOGLE_CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID!;

export const GoogleLoginButton: FC<GoogleButtonProps> = ({
                                                             text = 'signin_with',
                                                             theme = 'filled_blue',
                                                             size = 'large',
                                                             width = '250'
                                                         }) => {
    const buttonRef = useRef<HTMLDivElement>(null);
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();

    const handleCredentialResponse = async (response: GoogleCredentialResponse) => {
        try {
            console.log('[Google OAuth] Received credential response');
            const result = await dispatch(loginWithGoogle(response.credential)).unwrap();

            if (result) {
                console.log('[Google OAuth] Login successful, redirecting to main page');
                navigate('/');
            } else {
                console.error('[Google OAuth] Login failed: No user data received');
            }
        } catch (error) {
            console.error('[Google OAuth] Login failed:', error);
        }
    };

    const scriptLoaded = useScript('https://accounts.google.com/gsi/client', () => {
        if (buttonRef.current) {
            console.log('[Google OAuth] Script loaded, initializing...');

            window.google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: handleCredentialResponse,
            });

            window.google.accounts.id.renderButton(buttonRef.current, {
                type: 'standard',
                theme,
                size,
                text,
                width
            });

            console.log('[Google OAuth] Button rendered');
        }
    });

    useEffect(() => {
        return () => {
            // 컴포넌트 언마운트 시 Google 클라이언트 정리
            window.google?.accounts?.id?.cancel();
        };
    }, []);

    if (!scriptLoaded) {
        return <div className="w-[250px] h-[40px] bg-gray-100 rounded animate-pulse" />;
    }

    return <div ref={buttonRef} className="google-login-button" />;
};
