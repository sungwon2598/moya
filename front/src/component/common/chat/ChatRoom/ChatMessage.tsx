import React from 'react';
import { ChatMessage as MessageType } from '@/types/chat';

interface ChatMessageProps {
    message: MessageType;
    currentUser: string;
}

export const ChatMessage: React.FC<ChatMessageProps> = ({
                                                            message,
                                                            currentUser
                                                        }) => {
    const isOwnMessage = message.sender === currentUser;
    const isSystemMessage = message.type === 'JOIN' || message.type === 'LEAVE';

    return (
        <div
            className={`flex ${
                isOwnMessage ? 'justify-end' : 'justify-start'
            }`}
        >
            <div
                className={`max-w-xs lg:max-w-md rounded-2xl shadow-md ${
                    isSystemMessage
                        ? 'bg-gray-100 text-gray-600 mx-auto px-6 py-2'
                        : isOwnMessage
                            ? 'bg-blue-500 text-white px-5 py-3'
                            : 'bg-white text-gray-800 px-5 py-3'
                }`}
            >
                {!isSystemMessage && (
                    <p className="text-xs mb-1 font-medium opacity-80">
                        {message.sender}
                    </p>
                )}
                <p className="break-words text-sm lg:text-base">
                    {message.message || (
                        message.type === 'JOIN'
                            ? `${message.sender}님이 입장하셨습니다.`
                            : message.type === 'LEAVE'
                                ? `${message.sender}님이 퇴장하셨습니다.`
                                : message.message
                    )}
                </p>
                <p className={`text-xs mt-1 ${
                    isOwnMessage ? 'text-blue-100' : 'text-gray-400'
                }`}>
                    {new Date(message.timestamp).toLocaleTimeString()}
                </p>
            </div>
        </div>
    );
};