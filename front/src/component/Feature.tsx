
import React from 'react';
import { Feature } from '../types/types.tsx';

interface FeatureCardProps {
    feature: Feature;
}

const FeatureCard: React.FC<FeatureCardProps> = ({ feature }) => {
    const { title, description, icon: Icon, iconBgColor, iconColor } = feature;

    return (
        <div className="text-center">
            <div className={`w-16 h-16 ${iconBgColor} rounded-full flex items-center justify-center mx-auto mb-4`}>
                <Icon className={`w-8 h-8 ${iconColor}`} />
            </div>
            <h3 className="text-lg font-semibold mb-2">{title}</h3>
            <p className="text-gray-600">{description}</p>
        </div>
    );
};

export default FeatureCard;