import React from 'react';

interface FooterSectionProps {
    title: string;
    children: React.ReactNode;
}

const FooterSection: React.FC<FooterSectionProps> = ({ title, children }) => {
    return (
        <div className="space-y-4">
            <h3 className="text-lg font-bold text-moya-primary">{title}</h3>
            {children}
        </div>
    );
};

export default FooterSection;