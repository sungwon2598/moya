import { LucideIcon } from 'lucide-react';

export type NavItemType = 'A' | 'B' | 'C' | 'D';

export interface Types {
    label: string;       // 표시될 한글 라벨
    path: string;        // 실제 라우팅 경로
    type: 'A' | 'B' | 'C' | 'D';
    icon?: LucideIcon;
    onClick?: () => void;
}

export interface Feature {
    title: string;
    description: string;
    icon: LucideIcon;
    iconBgColor: string;
    iconColor: string;
}