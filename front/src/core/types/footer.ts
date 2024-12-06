import { type ElementType } from 'react';

export interface FooterLinkProps {
    href: string;
    children: React.ReactNode;
}

export interface SocialLinkProps {
    href: string;
    icon: ElementType;
    label: string;
}

export interface QuickLink {
    href: string;
    label: string;
}

export interface CompanyInfo {
    name: string;
    description: string;
    registration: string;
    ceo: string;
    phone: string;
}

export interface FooterSection {
    title: string;
    links: Array<QuickLink>;  // QuickLink 인터페이스 재사용
}