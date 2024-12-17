// src/types/google.d.ts

declare global {
    interface Window {
        google?: {
            accounts: {
                oauth2: {
                    initCodeClient: (config: {
                        client_id: string;
                        scope: string;
                        ux_mode: 'popup' | 'redirect';
                        callback: (response: any) => void;
                        access_type?: string;
                        prompt?: string;
                    }) => {
                        requestCode: () => void;
                    };
                };
                id: {
                    initialize: (config: {
                        client_id: string;
                        callback: (response: {
                            credential: string;
                            select_by?: string;
                            client_id?: string;
                            code?: string;
                        }) => void;
                        auto_select?: boolean;
                        cancel_on_tap_outside?: boolean;
                        scope?: string;
                        ux_mode?: 'popup' | 'redirect';
                        response_type?: string;
                    }) => void;
                    renderButton: (
                        element: HTMLElement,
                        config: {
                            type?: 'standard' | 'icon';
                            theme?: 'outline' | 'filled_blue' | 'filled_black';
                            size?: 'large' | 'medium' | 'small';
                            text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
                            width?: string | number;
                        }
                    ) => void;
                    prompt: () => void;
                    cancel: () => void;
                };

            };
        };
    }
}

// Type guard 함수
export const isGoogleLoaded = (): boolean => {
    return window.google !== undefined &&
        window.google.accounts !== undefined;
};

export {};


interface GoogleAccountsOAuth2 {
    initCodeClient(config: {
        client_id: string;
        scope: string;
        ux_mode: 'popup' | 'redirect';
        callback: (response: any) => void;
        access_type?: string;
        prompt?: string;
    }): {
        requestCode: () => void;
    };
}

interface GoogleButtonConfig {
    type?: 'standard' | 'icon';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    size?: 'large' | 'medium' | 'small';
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    width?: number;
}

interface GoogleAccountsId {
    initialize: (config: {
        client_id: string;
        callback: (response: any) => void;
        auto_select?: boolean;
        ux_mode?: 'popup' | 'redirect';
        scope?: string;
        access_type?: string;
        prompt?: string;
    }) => void;
    renderButton: (
        element: HTMLElement,
        config: GoogleButtonConfig
    ) => void;
    prompt: () => void;
    cancel: () => void;
}

interface GoogleAccounts {
    id: GoogleAccountsId;
    oauth2: GoogleAccountsOAuth2;
}

declare global {
    interface Window {
        google?: {
            accounts: GoogleAccounts;
        };
    }
}

// Type guard functions
export const isGoogleScriptLoaded = (): boolean => {
    return typeof window !== 'undefined' &&
        typeof window.google !== 'undefined' &&
        typeof window.google.accounts !== 'undefined';
};

export const isOAuth2Available = (): boolean => {
    return isGoogleScriptLoaded() &&
        typeof window.google.accounts.oauth2 !== 'undefined';
};

export const isGoogleIdAvailable = (): boolean => {
    return isGoogleScriptLoaded() &&
        typeof window.google.accounts.id !== 'undefined';
};
