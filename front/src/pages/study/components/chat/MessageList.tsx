import React from 'react';
import MessageItem from './MessageItem';
import type { Message } from '../shared/types/message';

interface MessageListProps {
    messages: Message[];
}

const MessageList: React.FC<MessageListProps> = ({ messages }) => {
    return (
        <div className="flex-1 overflow-y-auto px-6 py-4 space-y-4">
            {messages.map((message) => (
                <MessageItem key={message.id} message={message} />
            ))}
        </div>
    );
};

export default MessageList;
