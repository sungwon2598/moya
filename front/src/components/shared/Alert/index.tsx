import React from 'react';
import { AlertTriangle, CheckCircle, Info, X, XCircle } from 'lucide-react';
import { cn } from '@shared/utils/cn';

export type AlertVariant = 'success' | 'error' | 'warning' | 'info';

export interface AlertProps {
  variant?: AlertVariant;
  title?: string;
  children: React.ReactNode;
  onClose?: () => void;
  className?: string;
}

const variants = {
  success: {
    containerClass: 'bg-green-50 text-green-800 border-green-200',
    iconClass: 'text-green-500',
    Icon: CheckCircle,
  },
  error: {
    containerClass: 'bg-red-50 text-red-800 border-red-200',
    iconClass: 'text-red-500',
    Icon: XCircle,
  },
  warning: {
    containerClass: 'bg-yellow-50 text-yellow-800 border-yellow-200',
    iconClass: 'text-yellow-500',
    Icon: AlertTriangle,
  },
  info: {
    containerClass: 'bg-blue-50 text-blue-800 border-blue-200',
    iconClass: 'text-blue-500',
    Icon: Info,
  },
} as const;

const Alert: React.FC<AlertProps> = ({ variant = 'info', title, children, onClose, className }) => {
  const { containerClass, iconClass, Icon } = variants[variant];

  return (
    <div role="alert" className={cn('relative rounded-lg border p-4', containerClass, className)}>
      <div className="flex">
        <div className="flex-shrink-0">
          <Icon className={cn('h-5 w-5', iconClass)} aria-hidden="true" />
        </div>
        <div className="ml-3">
          {title && <h3 className="mb-1 text-sm font-medium">{title}</h3>}
          <div className="text-sm">{children}</div>
        </div>
        {onClose && (
          <div className="ml-auto pl-3">
            <div className="-mx-1.5 -my-1.5">
              <button
                type="button"
                onClick={onClose}
                className={cn(
                  'inline-flex rounded-md p-1.5',
                  'hover:bg-white/20 focus:outline-none focus:ring-2 focus:ring-offset-2',
                  'transition-colors focus:ring-offset-transparent',
                  variant === 'error' && 'focus:ring-red-500',
                  variant === 'success' && 'focus:ring-green-500',
                  variant === 'warning' && 'focus:ring-yellow-500',
                  variant === 'info' && 'focus:ring-blue-500'
                )}>
                <span className="sr-only">닫기</span>
                <X className="h-5 w-5" aria-hidden="true" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export { Alert };
