import type { CompanyInfo, FooterSection } from '@/core/types/footer';

export const companyInfo: CompanyInfo = {
  name: 'MOYA',
  description: '학습 성장을 위한 최적의 스터디 플랫폼',
  registration: '123-45-67890',
  ceo: '홍성원',
  phone: '02-490-7345',
} as const;

export const footerSections: FooterSection[] = [
  {
    title: '바로가기',
    links: [
      { href: '/about', label: '회사소개' },
      { href: '/roadmap', label: '로드맵' },
      { href: '/study', label: '스터디' },
      // { href: '/cooperate', label: '협업툴' }
    ],
  },
  {
    title: '고객지원',
    links: [
      { href: '/faq', label: '자주 묻는 질문' },
      { href: '/contact', label: '문의하기' },
      { href: '/notice', label: '공지사항' },
      {
        href: 'https://docs.google.com/forms/d/e/1FAIpQLSfiWdVsmn4WD12WoFfWfeSBN5Cu-QVdcd6YNR9hG2K64BRIyA/closedform',
        label: '피드백',
      },
    ],
  },
  {
    title: '약관 및 정책',
    links: [
      { href: '/terms', label: '이용약관' },
      { href: '/privacy', label: '개인정보처리방침' },
      { href: '/refund', label: '환불정책' },
    ],
  },
] as const;

export const socialLinks = [
  { href: '#', icon: 'Facebook', label: 'Facebook' },
  { href: '#', icon: 'Instagram', label: 'Instagram' },
  { href: '#', icon: 'Youtube', label: 'YouTube' },
] as const;
