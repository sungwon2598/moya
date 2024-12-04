import React from 'react';
import type { Message } from '../shared/types/message';

interface MessageItemProps {
    message: Message;
}

const MessageItem: React.FC<MessageItemProps> = ({ message }) => {
    return (
        <div className="flex space-x-3">
            <div className="w-10 h-10 rounded-full bg-gray-200 flex-shrink-0" />
            <div>
                <div className="flex items-center space-x-2">
                    <span className="font-medium text-gray-900">{message.sender}</span>
                    <span className="text-sm text-gray-500">
            {message.timestamp.toLocaleTimeString()}
          </span>
                </div>
                <p className="text-gray-700 mt-1">{message.content}</p>
            </div>
        </div>
    );
};

export default MessageItem;
