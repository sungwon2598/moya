import { useState, useEffect, useCallback } from 'react';

export const useScript = (src: string, onLoad?: () => void): boolean => {
    const [loaded, setLoaded] = useState(false);

    const handleLoad = useCallback(() => {
        setLoaded(true);
        onLoad?.();
    }, [onLoad]);

    useEffect(() => {
        const script = document.createElement('script');
        script.src = src;
        script.async = true;
        script.onload = handleLoad;

        document.body.appendChild(script);

        return () => {
            document.body.removeChild(script);
        };
    }, [src, handleLoad]);

    return loaded;
};
