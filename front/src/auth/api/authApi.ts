// import { GoogleCredentialResponse } from '../types/auth.types';

const API_URL = process.env.REACT_APP_API_URL;

export const authenticateWithGoogle = async (credential: string) => {
    const response = await fetch(`${API_URL}/auth/google`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ credential })
    });

    if (!response.ok) {
        throw new Error('Google authentication failed');
    }

    return response.json();
};
