import React from 'react';
import { AlertCircle, CheckCircle2, Info, XCircle } from 'lucide-react';
import { cn } from '@utils/cn';

export type AlertType = 'success' | 'error' | 'warning' | 'info';

interface AlertProps {
    type?: AlertType;
    title?: string;
    message?: string;
    className?: string;
    onClose?: () => void;
    children?: React.ReactNode;  // children prop 추가
}

const Alert: React.FC<AlertProps> = ({
                                         type = 'info',
                                         title,
                                         message,
                                         className,
                                         onClose
                                     }) => {
    const alertStyles = {
        success: {
            container: 'bg-green-50 border-green-200',
            icon: 'text-green-500',
            title: 'text-green-800',
            message: 'text-green-700',
        },
        error: {
            container: 'bg-red-50 border-red-200',
            icon: 'text-red-500',
            title: 'text-red-800',
            message: 'text-red-700',
        },
        warning: {
            container: 'bg-yellow-50 border-yellow-200',
            icon: 'text-yellow-500',
            title: 'text-yellow-800',
            message: 'text-yellow-700',
        },
        info: {
            container: 'bg-blue-50 border-blue-200',
            icon: 'text-blue-500',
            title: 'text-blue-800',
            message: 'text-blue-700',
        },
    };

    const AlertIcon = {
        success: CheckCircle2,
        error: XCircle,
        warning: AlertCircle,
        info: Info,
    }[type];

    return (
        <div className={cn(
            'flex gap-3 p-4 rounded-lg border',
            alertStyles[type].container,
            className
        )}>
            <AlertIcon className={cn('w-5 h-5 mt-0.5', alertStyles[type].icon)} />
            <div className="flex-1">
                {title && (
                    <h5 className={cn('font-semibold mb-1', alertStyles[type].title)}>
                        {title}
                    </h5>
                )}
                <p className={cn('text-sm', alertStyles[type].message)}>
                    {message}
                </p>
            </div>
            {onClose && (
                <button
                    onClick={onClose}
                    className={cn(
                        'p-1 hover:bg-opacity-20 hover:bg-black rounded transition-colors',
                        'self-start -mt-1 -mr-1'
                    )}
                    aria-label="Close alert"
                >
                    <svg className="w-4 h-4 opacity-60" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M6 18L18 6M6 6l12 12" strokeWidth="2" strokeLinecap="round" />
                    </svg>
                </button>
            )}
        </div>
    );
};

export default Alert;
