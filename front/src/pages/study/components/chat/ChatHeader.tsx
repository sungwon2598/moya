import React from 'react';
import { Search, Settings } from 'lucide-react';

const ChatHeader: React.FC = () => {
    return (
        <div className="h-16 min-h-[64px] flex items-center justify-between px-6 border-b border-gray-200">
            <div className="flex items-center space-x-2">
                <span className="text-lg font-semibold text-gray-900"># 일반</span>
            </div>
            <div className="flex items-center space-x-2">
                <button className="p-2 hover:bg-gray-100 rounded-lg">
                    <Search className="w-5 h-5 text-gray-500" />
                </button>
                <button className="p-2 hover:bg-gray-100 rounded-lg">
                    <Settings className="w-5 h-5 text-gray-500" />
                </button>
            </div>
        </div>
    );
};

export default ChatHeader;
