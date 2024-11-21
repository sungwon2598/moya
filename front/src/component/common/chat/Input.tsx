import React from 'react';
import { cn } from '@/utils/cn';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    wrapperClassName?: string;
}

export const Input: React.FC<InputProps> = ({
                                                label,
                                                error,
                                                className,
                                                wrapperClassName,
                                                ...props
                                            }) => {
    return (
        <div className={cn("w-full", wrapperClassName)}>
            {label && (
                <label className="block text-sm font-medium text-gray-700">
                    {label}
                </label>
            )}
            <input
                className={cn(
                    "w-full px-4 py-2 border border-gray-300 rounded-xl",
                    "focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent",
                    "transition-all duration-200",
                    error && "border-red-500 focus:ring-red-500",
                    className
                )}
                {...props}
            />
            {error && (
                <p className="text-sm text-red-600">{error}</p>
            )}
        </div>
    );
};
