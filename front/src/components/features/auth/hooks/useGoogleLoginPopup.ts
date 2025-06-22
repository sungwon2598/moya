import { GoogleAuthResponse, GoogleCredentialResponse, GoogleCodeResponse } from '../types/auth.types';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_APP_GOOGLE_CLIENT_ID;

export const useGoogleLoginPopup = () => {
  return () =>
    new Promise<GoogleAuthResponse>((resolve, reject) => {
      if (!window.google?.accounts) {
        reject(new Error('Google API not initialized'));
        return;
      }

      const google = window.google;

      // Step 1: Get ID Token
      google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: (credentialResponse: GoogleCredentialResponse) => {
          if (!credentialResponse.credential) {
            reject(new Error('No ID token received'));
            return;
          }

          // Step 2: Request Code
          const codeClient = google.accounts.oauth2.initCodeClient({
            client_id: GOOGLE_CLIENT_ID,
            scope: 'openid profile email',
            callback: (codeResponse: GoogleCodeResponse) => {
              if (!codeResponse.code) {
                reject(new Error('No authorization code received'));
                return;
              }

              const authData: GoogleAuthResponse = {
                credential: credentialResponse.credential,
                authCode: codeResponse.code,
              };

              resolve(authData);
            },
            ux_mode: 'popup',
            access_type: 'offline',
          });

          codeClient.requestCode();
        },
        auto_select: false,
      });

      google.accounts.id.prompt();
    });
};
