interface Window {
    google: {
        accounts: {
            id: {
                initialize: (config: any) => void;
                renderButton: (element: HTMLElement, options: any) => void;
                prompt: () => void;
            };
        };
    };
}
