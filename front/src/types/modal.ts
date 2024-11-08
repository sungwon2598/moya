export interface ModalProps {
    title?: string;
    children: React.ReactNode;
    onClose?: () => void;
    showCloseButton?: boolean;
    size?: 'sm' | 'md' | 'lg';
    hideOverlay?: boolean;
    className?: string;
}