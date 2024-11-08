import React from 'react';
import { useModal } from '../../hooks/useModal';
import {Modal} from './Modal';

export const ModalRoot: React.FC = () => {
    const { isOpen, modalContent, modalProps, hideModal } = useModal();

    if (!isOpen) return null;

    return (
        <Modal {...modalProps} onClose={hideModal}>
            {modalContent}
        </Modal>
    );
};
