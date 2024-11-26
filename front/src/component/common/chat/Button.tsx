import React from 'react';
import { cn } from '@/utils/cn';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'danger';
    size?: 'sm' | 'md' | 'lg';
    isLoading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
                                                  children,
                                                  variant = 'primary',
                                                  size = 'md',
                                                  isLoading = false,
                                                  className,
                                                  disabled,
                                                  ...props
                                              }) => {
    const baseStyles = "rounded-xl transition-all duration-200 flex items-center justify-center gap-2";

    const variants = {
        primary: "bg-blue-600 text-white hover:bg-blue-700 disabled:bg-blue-300",
        secondary: "bg-gray-500 text-white hover:bg-gray-600 disabled:bg-gray-300",
        danger: "bg-red-500 text-white hover:bg-red-600 disabled:bg-red-300"
    };

    const sizes = {
        sm: "px-4 py-2 text-sm",
        md: "px-6 py-2.5 text-base",
        lg: "px-8 py-3 text-lg"
    };

    return (
        <button
            className={cn(
                baseStyles,
                variants[variant],
                sizes[size],
                isLoading && "opacity-75 cursor-not-allowed",
                className
            )}
            disabled={disabled || isLoading}
            {...props}
        >
            {isLoading ? (
                <>
                    <span className="animate-spin">⌛</span>
                    <span>Loading...</span>
                </>
            ) : children}
        </button>
    );
};
