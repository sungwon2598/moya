import React from 'react';
import { ChatMessage } from './ChatMessage';
import { ChatMessage as MessageType } from '@/types/chat';
import { type RefObject } from 'react';

interface MessageListProps {
    messages: MessageType[];
    currentUser: string;
    messagesEndRef: RefObject<HTMLDivElement>;  // 타입 정의 추가
}

export const MessageList: React.FC<MessageListProps> = ({
                                                            messages,
                                                            currentUser,
                                                            messagesEndRef
                                                        }) => {
    return (
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
            {messages.map((message, index) => (
                <ChatMessage
                    key={index}
                    message={message}
                    currentUser={currentUser}
                />
            ))}
            <div ref={messagesEndRef} />
        </div>
    );
};
