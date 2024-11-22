import React from 'react';
import { Loader } from 'lucide-react';
import { cn } from '@/utils/cn';

interface LoadingSpinnerProps {
    size?: number;
    className?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
                                                                  size = 24,
                                                                  className
                                                              }) => {
    return (
        <div className="flex items-center justify-center">
            <Loader
                size={size}
                className={cn("animate-spin text-gray-500", className)}
            />
        </div>
    );
};