// src/types/google.d.ts

declare global {
    interface Window {
        google?: {
            accounts: {
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
                oauth2: {
                    initCodeClient: (config: {
                        client_id: string;
                        scope: string;
                        ux_mode: 'popup' | 'redirect';
                        callback: (response: any) => void;
                    }) => void;
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
