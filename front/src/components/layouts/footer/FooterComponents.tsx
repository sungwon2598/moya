import React from 'react';
import { Link } from 'react-router-dom';
import { FooterLinkProps, SocialLinkProps } from '../../../core/types/footer.ts';

export const FooterLink: React.FC<FooterLinkProps> = ({ href, children }) => (
  <div className="block">
    <Link to={href} className="hover:text-moya-primary text-sm text-gray-600 transition-colors duration-200">
      {children}
    </Link>
  </div>
);

export const SocialLink: React.FC<SocialLinkProps> = ({ href, icon: Icon, label }) => (
  <a href={href} className="hover:text-moya-primary text-gray-400 transition-colors duration-200" aria-label={label}>
    <span className="sr-only">{label}</span>
    <Icon className="h-6 w-6" />
  </a>
);
