import React, { forwardRef } from 'react';
import { cn } from '@shared/utils/cn';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helpText?: string;
  startIcon?: React.ReactNode;
  endIcon?: React.ReactNode;
  wrapperClassName?: string;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, wrapperClassName, label, error, helpText, startIcon, endIcon, type = 'text', ...props }, ref) => {
    const inputStyles = cn(
      'block w-full rounded-md border-gray-300 shadow-sm',
      'focus:border-moya-primary focus:ring-moya-primary',
      'disabled:cursor-not-allowed disabled:bg-gray-50 disabled:text-gray-500',
      error ? 'border-red-500' : 'border-gray-300',
      startIcon ? 'pl-10' : '',
      endIcon ? 'pr-10' : '',
      className
    );

    const wrapperStyles = cn('relative rounded-md', wrapperClassName);

    const iconStyles = 'absolute inset-y-0 flex items-center pointer-events-none text-gray-400';

    return (
      <div className={wrapperStyles}>
        {label && <label className="mb-1 block text-sm font-medium text-gray-700">{label}</label>}

        <div className="relative">
          {startIcon && <div className={cn(iconStyles, 'left-3')}>{startIcon}</div>}

          <input
            ref={ref}
            type={type}
            className={inputStyles}
            {...props}
            aria-describedby={error ? `${props.id}-error` : helpText ? `${props.id}-description` : undefined}
          />

          {endIcon && <div className={cn(iconStyles, 'right-3')}>{endIcon}</div>}
        </div>

        {error ? (
          <p className="mt-1 text-sm text-red-500" id={`${props.id}-error`}>
            {error}
          </p>
        ) : helpText ? (
          <p className="mt-1 text-sm text-gray-500" id={`${props.id}-description`}>
            {helpText}
          </p>
        ) : null}
      </div>
    );
  }
);

Input.displayName = 'Input';

export { Input };
