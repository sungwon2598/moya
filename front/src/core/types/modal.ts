export interface ModalProps {
  title?: React.ReactNode;
  children: React.ReactNode;
  onClose?: () => void;
  showCloseButton?: boolean;
  size?: 'sm' | 'md' | 'lg';
  hideOverlay?: boolean;
  className?: string;
}
