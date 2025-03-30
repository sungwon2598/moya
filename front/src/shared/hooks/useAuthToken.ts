import { useState, useEffect } from 'react';

/**
 * Custom hook to extract and manage authentication token from cookies
 * @returns Object containing the auth token and related utility functions
 */

export const useAuthToken = () => {
  const [authToken, setAuthToken] = useState<string | null>(null);

  // Function to extract a specific cookie value by name
  const getCookieValue = (name: string): string | null => {
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
      const [cookieName, cookieValue] = cookie.trim().split('=');
      if (cookieName === name) {
        return cookieValue;
      }
    }
    return null;
  };

  // Get authorization header with Bearer token
  const getAuthHeader = (): { Authorization: string } | undefined => {
    console.log(authToken);
    console.log(document.cookie);
    if (!authToken) return undefined;
    console.log(authToken);
    // return { Authorization: `Bearer ${authToken}` };
  };

  // Check if user is authenticated
  const isAuthenticated = (): boolean => {
    return !!authToken;
  };

  // Effect to load token when component mounts
  useEffect(() => {
    const token = getCookieValue('AUTH-TOKEN');
    if (token) {
      setAuthToken(token);
    }
  }, []);

  // Function to manually refresh the token
  const refreshToken = (): void => {
    const token = getCookieValue('AUTH-TOKEN');
    setAuthToken(token);
  };

  return {
    authToken,
    getAuthHeader,
    isAuthenticated,
    refreshToken,
  };
};
