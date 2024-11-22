import { useNavigate } from 'react-router-dom';
import { ChatList as ChatListComponent } from '@/component/common/chat/ChatList';
import type { ChatRoomInfo } from '@/types/chat';

const ChatList = () => {
    const navigate = useNavigate();
    const username = localStorage.getItem('chat_username') || '익명';

    const handleRoomSelect = (roomInfo: ChatRoomInfo) => {
        navigate(`/chat/${roomInfo.roomId}`);
    };

    return (
        <ChatListComponent
            onRoomSelect={handleRoomSelect}
            username={username}
        />
    );
};

export default ChatList;