import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { ModalProps } from '@core/types/modal';

const Modal: React.FC<ModalProps> = ({
  title,
  children,
  onClose,
  showCloseButton = true,
  size = 'md',
  hideOverlay = false,
  className = '',
}) => {
  const modalRef = useRef<HTMLDivElement>(null);
  const previousActiveElement = useRef<HTMLElement | null>(null);

  useEffect(() => {
    // 모달이 열릴 때 현재 포커스된 요소 저장
    previousActiveElement.current = document.activeElement as HTMLElement;

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && onClose) {
        onClose();
      }
    };

    const handleClickOutside = (event: MouseEvent) => {
      if (modalRef.current && !modalRef.current.contains(event.target as Node) && onClose) {
        onClose();
      }
    };

    // 모달이 열릴 때 body 스크롤 방지
    document.body.style.overflow = 'hidden';

    document.addEventListener('keydown', handleEscape);
    if (!hideOverlay) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    // 모달 내의 첫 번째 포커스 가능한 요소에 포커스
    const focusableElements = modalRef.current?.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    if (focusableElements?.length) {
      (focusableElements[0] as HTMLElement).focus();
    }

    return () => {
      document.body.style.overflow = 'unset';
      document.removeEventListener('keydown', handleEscape);
      if (!hideOverlay) {
        document.removeEventListener('mousedown', handleClickOutside);
      }
      // 모달이 닫힐 때 이전 포커스 요소로 복원
      previousActiveElement.current?.focus();
    };
  }, [onClose, hideOverlay]);

  const sizeClasses = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
  };

  return createPortal(
    <div
      className={`fixed inset-0 z-50 flex items-center justify-center ${hideOverlay ? '' : 'bg-black bg-opacity-50'}`}
      role="dialog"
      aria-modal="true"
      aria-labelledby={title ? 'modal-title' : undefined}>
      <div
        ref={modalRef}
        className={`w-full rounded-lg bg-white shadow-xl ${sizeClasses[size]} mx-4 transform transition-all ${className}`}>
        {(title || showCloseButton) && (
          <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
            {title && (
              <h2 id="modal-title" className="text-xl font-semibold text-gray-900">
                {title}
              </h2>
            )}
            {showCloseButton && (
              <button
                onClick={onClose}
                className="focus:ring-moya-primary rounded-lg p-1 text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2"
                aria-label="Close modal">
                <svg
                  className="h-6 w-6"
                  fill="none"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            )}
          </div>
        )}
        <div className="px-6 py-4">{children}</div>
      </div>
    </div>,
    document.body
  );
};

export { Modal };
