import React from 'react';

interface AlertProps {
    variant: 'default' | 'success' | 'error' | 'warning';
    children: React.ReactNode;
    className?: string;
}

export const Alert: React.FC<AlertProps> = ({
                                                variant = 'default',
                                                children,
                                                className = ''
                                            }) => {
    const variants = {
        default: 'bg-blue-50 text-blue-700 border-blue-200',
        success: 'bg-green-50 text-green-700 border-green-200',
        error: 'bg-red-50 text-red-700 border-red-200',
        warning: 'bg-yellow-50 text-yellow-700 border-yellow-200'
    };

    return (
        <div className={`px-4 py-3 rounded-lg border ${variants[variant]} ${className}`}>
            {children}
        </div>
    );
};

export default Alert;