import React, { useState } from 'react';
import { Send } from 'lucide-react';
import { Button } from '../Button';
import { Input } from '../Input';

interface ChatInputProps {
    onSendMessage: (message: string) => void;
    isConnected: boolean;
}

export const ChatInput: React.FC<ChatInputProps> = ({
                                                        onSendMessage,
                                                        isConnected
                                                    }) => {
    const [message, setMessage] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (message.trim() && isConnected) {
            onSendMessage(message);
            setMessage('');
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSubmit(e);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="bg-white p-6 shadow-lg">
            <div className="flex space-x-3">
                <Input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder="메시지를 입력하세요..."
                    disabled={!isConnected}
                    className="flex-1"
                />
                <Button
                    type="submit"
                    disabled={!isConnected || !message.trim()}
                    className="shrink-0 flex items-center space-x-2"
                >
                    <Send size={18} />
                    <span>전송</span>
                </Button>
            </div>
        </form>
    );
};