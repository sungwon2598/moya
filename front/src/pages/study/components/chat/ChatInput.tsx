import React, { useState } from 'react';
import { Send } from 'lucide-react';

interface ChatInputProps {
    onSendMessage: (content: string) => void;
}

const ChatInput: React.FC<ChatInputProps> = ({ onSendMessage }) => {
    const [message, setMessage] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (message.trim()) {
            onSendMessage(message);
            setMessage('');
        }
    };

    return (
        <div className="h-[80px] min-h-[80px] border-t border-gray-200 p-4 px-6">
            <form onSubmit={handleSubmit} className="relative h-full">
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="메시지 입력..."
                    className="w-full h-full bg-white border border-gray-200 text-gray-900 px-4 pr-12 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <button
                    type="submit"
                    className="absolute right-2 top-1/2 -translate-y-1/2 p-2 hover:bg-gray-100 rounded-lg"
                >
                    <Send className="w-5 h-5 text-gray-500" />
                </button>
            </form>
        </div>
    );
};

export default ChatInput;
