import React, { useRef, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { useScript } from '../hooks/useScript';
import { loginWithGoogle } from '../store/authSlice';
import { GoogleCredentialResponse } from '../types/auth.types';

interface GoogleLoginButtonProps {
    text?: string;
}

export const GoogleLoginButton: React.FC<GoogleLoginButtonProps> = ({
                                                                        text = 'signin_with',
                                                                    }) => {
    const dispatch = useDispatch();
    const buttonRef = useRef<HTMLDivElement>(null);

    const handleSuccess = useCallback(
        async (response: GoogleCredentialResponse) => {
            await dispatch(loginWithGoogle(response.credential));
        },
        [dispatch]
    );

    const initializeGoogleButton = useCallback(() => {
        if (window.google && buttonRef.current) {
            window.google.accounts.id.initialize({
                client_id: process.env.REACT_APP_GOOGLE_CLIENT_ID!,
                callback: handleSuccess,
            });

            window.google.accounts.id.renderButton(buttonRef.current, {
                theme: 'filled_blue',
                size: 'large',
                text,
                width: 250,
            });
        }
    }, [handleSuccess, text]);

    useScript('https://accounts.google.com/gsi/client', initializeGoogleButton);

    return (
        <div
            ref={buttonRef}
            className="flex justify-center items-center p-4"
        />
    );
};
