import React from 'react';
// import { Facebook, Instagram, Youtube } from 'lucide-react';
import { FooterLink } from './FooterComponents.tsx';
import FooterSection from './FooterSection.tsx';
import { companyInfo, footerSections } from './footerData.ts';
import { QuickLink } from '@/core/types/footer.ts';

// const iconMap = {
//   Facebook,
//   Instagram,
//   Youtube,
// };

const Footer: React.FC = () => {
  return (
    <footer className="border-moya-primary/20 border-t">
      <div className="container mx-auto px-4 py-12">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-4">
          <FooterSection title={companyInfo.name}>
            <p className="text-sm opacity-60">{companyInfo.description}</p>
            <div className="space-y-2">
              <p className="text-sm opacity-50">사업자등록번호: {companyInfo.registration}</p>
              <p className="text-sm opacity-50">대표: {companyInfo.ceo}</p>
              <p className="text-sm opacity-50">연락처: {companyInfo.phone}</p>
            </div>
          </FooterSection>

          {footerSections.map((section) => (
            <FooterSection key={section.title} title={section.title}>
              <div className="flex flex-col space-y-2">
                {section.links.map((link: QuickLink) => (
                  <FooterLink key={link.href} href={link.href}>
                    {link.label}
                  </FooterLink>
                ))}
              </div>
            </FooterSection>
          ))}
        </div>

        <div className="mt-12 border-t border-gray-400 pt-8">
          <div className="flex flex-col items-center justify-between space-y-4 md:flex-row md:space-y-0">
            <div className="flex space-x-6">
              {/* {socialLinks.map((social) => (
                <SocialLink key={social.label} href={social.href} icon={iconMap[social.icon]} label={social.label} />
              ))} */}
            </div>
            <p className="text-sm opacity-50">© {new Date().getFullYear()} MOYA. All rights reserved.</p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
