import React from 'react';
import { ChevronDown } from 'lucide-react';

const ChannelHeader: React.FC = () => {
    return (
        <div className="h-16 min-h-[64px] p-4 border-b border-gray-200">
            <div className="flex items-center justify-between h-full">
                <div className="flex items-center space-x-2">
                    <h1 className="text-lg font-semibold text-gray-900">채널</h1>
                    <button className="p-1 hover:bg-gray-100 rounded-lg">
                        <ChevronDown className="w-4 h-4 text-gray-500" />
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChannelHeader;
