import { useState, useCallback } from 'react';
import type { Message } from '../components/shared/types/message';

export const useMessage = () => {
    const [messages, setMessages] = useState<Message[]>([]);

    const sendMessage = useCallback((content: string) => {
        const newMessage: Message = {
            id: Date.now().toString(),
            content,
            sender: '사용자',
            timestamp: new Date(),
        };
        setMessages(prev => [...prev, newMessage]);
    }, []);

    return { messages, sendMessage };
};
