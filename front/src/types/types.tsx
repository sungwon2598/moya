import { LucideIcon } from 'lucide-react';

export type NavItemType = 'A' | 'B' | 'C' | 'D';

export interface Types {
    label: string;
    type: NavItemType;
    icon?: LucideIcon;
}

export interface Feature {
    title: string;
    description: string;
    icon: LucideIcon;
    iconBgColor: string;
    iconColor: string;
}