
import React, { createContext, useCallback, useState, useRef } from 'react';
import { ModalProps } from '../../types/modal.ts';

interface ModalContextType {
    showModal: (content: React.ReactNode, options?: Partial<ModalProps>) => void;
    hideModal: () => void;
    isOpen: boolean;
    modalContent: React.ReactNode | null;
    modalProps: Partial<ModalProps>;
}

export const ModalContext = createContext<ModalContextType | undefined>(undefined);

export const ModalProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [modalContent, setModalContent] = useState<React.ReactNode | null>(null);
    const [modalProps, setModalProps] = useState<Partial<ModalProps>>({});
    const modalHistoryRef = useRef<Array<{ content: React.ReactNode; props: Partial<ModalProps> }>>([]);

    const showModal = useCallback((content: React.ReactNode, options: Partial<ModalProps> = {}) => {
        // 현재 모달이 있다면 히스토리에 저장
        if (isOpen) {
            modalHistoryRef.current.push({ content: modalContent!, props: modalProps });
        }

        setModalContent(content);
        setModalProps(options);
        setIsOpen(true);
    }, [isOpen, modalContent, modalProps]);

    const hideModal = useCallback(() => {
        // 히스토리에서 이전 모달이 있는지 확인
        const previousModal = modalHistoryRef.current.pop();

        if (previousModal) {
            // 이전 모달이 있다면 복원
            setModalContent(previousModal.content);
            setModalProps(previousModal.props);
        } else {
            // 이전 모달이 없다면 완전히 닫기
            setIsOpen(false);
            setModalContent(null);
            setModalProps({});
        }
    }, []);

    return (
        <ModalContext.Provider value={{ isOpen, modalContent, modalProps, showModal, hideModal }}>
            {children}
        </ModalContext.Provider>
    );
};
