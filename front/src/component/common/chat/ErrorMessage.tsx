import React from 'react';
import { AlertCircle } from 'lucide-react';
import { cn } from '@/utils/cn';

interface ErrorMessageProps {
    message: string;
    className?: string;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({
                                                              message,
                                                              className
                                                          }) => {
    if (!message) return null;

    return (
        <div
            className={cn(
                "p-4 bg-red-50 border-l-4 border-red-500",
                "flex items-center space-x-2",
                className
            )}
        >
            <AlertCircle className="text-red-500" size={20} />
            <span className="text-red-700">{message}</span>
        </div>
    );
};