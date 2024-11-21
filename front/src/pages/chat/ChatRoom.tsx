import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ChatRoom as ChatRoomComponent } from '@/component/common/chat/ChatRoom';
import { CHAT_API } from '@/config/apiConfig';
import type { ChatRoomInfo } from '@/types/chat';

const ChatRoom = () => {
    const { roomId } = useParams<{ roomId: string }>();
    const navigate = useNavigate();
    const username = localStorage.getItem('chat_username') || '익명';
    const [roomInfo, setRoomInfo] = useState<ChatRoomInfo | null>(null);

    useEffect(() => {
        const loadRoomInfo = async () => {
            try {
                if (!roomId) return;
                const info = await CHAT_API.getRoom(roomId);
                setRoomInfo(info);
            } catch (error) {
                console.error('Failed to load room info:', error);
                navigate('/chat'); // 에러 시 채팅 목록으로 이동
            }
        };
        loadRoomInfo();
    }, [roomId, navigate]);

    if (!roomInfo || !roomId) {
        return <div>Loading...</div>;
    }

    return (
        <ChatRoomComponent
            roomId={roomId}
            roomInfo={roomInfo}
            username={username}
            onExit={() => navigate('/chat')}
        />
    );
};

export default ChatRoom;