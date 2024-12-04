import { useEffect } from 'react';

export const useScript = (
    src: string,
    onload?: () => void
) => {
    useEffect(() => {
        const script = document.createElement('script');
        script.src = src;
        script.onload = onload ? onload : null;
        document.head.appendChild(script);

        return () => {
            document.head.removeChild(script);
        };
    }, [src, onload]);
};
