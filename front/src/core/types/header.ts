export interface DropdownItem {
    label: string;
    href?: string;
    onClick?: () => void;
    icon?: React.ComponentType;
    type?: 'default' | 'danger';
}

export interface NavDropdownProps {
    type: 'roadmap' | 'study' | 'user';
    items: DropdownItem[];
    isOpen: boolean;
    onClose: () => void;
}