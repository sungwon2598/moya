interface Window {
    google: {
        accounts: {
            id: {
                initialize: (config: {
                    client_id: string;
                    callback: (response: GoogleCredentialResponse) => void;
                    auto_select?: boolean;
                    cancel_on_tap_outside?: boolean;
                }) => void;
                renderButton: (
                    element: HTMLElement,
                    config: {
                        type?: 'standard' | 'icon';
                        theme?: 'outline' | 'filled_blue' | 'filled_black';
                        size?: 'large' | 'medium' | 'small';
                        text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
                        width?: string;
                    }
                ) => void;
                prompt: () => void;
                cancel: () => void;
            };
        };
    };
}
