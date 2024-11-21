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
        <div className="w-full bg-white border-t border-gray-200 py-4">
            <form onSubmit={handleSubmit} className="w-full px-4">
                <div className="flex items-center w-full gap-2">
                    <Input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="메시지를 입력하세요..."
                        disabled={!isConnected}
                        className="flex-1 w-full"
                    />
                    <Button
                        type="submit"
                        disabled={!isConnected || !message.trim()}
                        className="shrink-0 flex items-center space-x-2 px-4 min-w-[100px]"
                    >
                        <Send size={18} />
                        <span>전송</span>
                    </Button>
                </div>
            </form>
        </div>
    );
};