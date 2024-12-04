import React from 'react';
import ChatHeader from '../chat/ChatHeader';
import MessageList from '../chat/MessageList';
import ChatInput from '../chat/ChatInput';
import { useMessage } from '../../hooks/useMessage';

const ChatArea: React.FC = () => {
    const { messages, sendMessage } = useMessage();

    return (
        <div className="flex-1 flex flex-col bg-white min-w-0">
            <ChatHeader />
            <MessageList messages={messages} />
            <ChatInput onSendMessage={sendMessage} />
        </div>
    );
};

export default ChatArea;
