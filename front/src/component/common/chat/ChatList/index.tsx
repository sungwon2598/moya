import React, { useState, useEffect } from 'react';
import { Users, Plus, Loader } from 'lucide-react';
import { Button } from '../Button';
import { Input } from '../Input';
import { ErrorMessage } from '../ErrorMessage';
import { ChatRoomInfo, CreateRoomRequest } from '@/types/chat';
import { CHAT_API } from '@/config/apiConfig';

interface ChatListProps {
    onRoomSelect: (room: ChatRoomInfo) => void;
    username?: string;
}

export const ChatList: React.FC<ChatListProps> = ({ onRoomSelect }) => {
    const [rooms, setRooms] = useState<ChatRoomInfo[]>([]);
    const [newRoomName, setNewRoomName] = useState('');
    const [isCreatingRoom, setIsCreatingRoom] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    // roomName이 JSON 문자열인 경우 파싱하여 실제 이름을 추출하는 함수
    const parseRoomName = (roomName: string): string => {
        try {
            const parsed = JSON.parse(roomName);
            return parsed.roomName || roomName;
        } catch {
            return roomName;
        }
    };

    const fetchRooms = async () => {
        try {
            const data = await CHAT_API.getAllRooms();
            setRooms(data);
            setError(null);
        } catch (error) {
            console.error('Failed to fetch rooms:', error);
            setError('채팅방 목록을 불러오는데 실패했습니다.');
        }
    };

    useEffect(() => {
        fetchRooms();
        const interval = setInterval(fetchRooms, 5000);
        return () => clearInterval(interval);
    }, []);

    const handleCreateRoom = async () => {
        if (!newRoomName.trim()) return;
        setLoading(true);

        try {
            const createRoomRequest: CreateRoomRequest = {
                roomName: newRoomName,
            };

            await CHAT_API.createRoom(createRoomRequest);
            await fetchRooms();
            setNewRoomName('');
            setIsCreatingRoom(false);
            setError(null);
        } catch (error) {
            console.error('Failed to create room:', error);
            setError('방 생성에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white shadow-xl rounded-2xl p-6 max-w-2xl mx-auto mt-10">
            {error && <ErrorMessage message={error} />}

            <div className="flex justify-between items-center mb-8">
                <h2 className="text-3xl font-bold text-gray-900">채팅방 목록</h2>
                <Button
                    onClick={() => setIsCreatingRoom(true)}
                    className="flex items-center gap-2"
                    disabled={loading}
                >
                    <Plus size={18} />
                    방 만들기
                </Button>
            </div>

            {isCreatingRoom && (
                <div className="mb-8 p-6 bg-gray-50 rounded-xl border border-gray-200 shadow-inner">
                    <Input
                        type="text"
                        value={newRoomName}
                        onChange={(e) => setNewRoomName(e.target.value)}
                        placeholder="방 이름을 입력하세요"
                        className="mb-4"
                        disabled={loading}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter' && newRoomName.trim()) {
                                handleCreateRoom();
                            }
                        }}
                    />
                    <div className="flex space-x-3">
                        <Button
                            onClick={handleCreateRoom}
                            disabled={loading || !newRoomName.trim()}
                            className="flex-1 bg-green-600 hover:bg-green-700 flex items-center justify-center gap-2"
                        >
                            {loading ? <Loader className="animate-spin" size={18} /> : <Plus size={18} />}
                            {loading ? '생성 중...' : '생성'}
                        </Button>
                        <Button
                            onClick={() => {
                                setIsCreatingRoom(false);
                                setNewRoomName('');
                            }}
                            disabled={loading}
                            variant="secondary"
                            className="flex-1"
                        >
                            취소
                        </Button>
                    </div>
                </div>
            )}

            {loading && !isCreatingRoom && (
                <div className="flex items-center justify-center py-8 text-gray-600 space-x-2">
                    <Loader className="animate-spin" />
                    <span>로딩 중...</span>
                </div>
            )}

            <div className="space-y-4">
                {rooms.map((room) => (
                    <div
                        key={room.roomId}
                        onClick={() => !loading && onRoomSelect(room)}
                        className="group border border-gray-200 rounded-xl p-5 hover:bg-gray-50
                                 transition-all duration-200 cursor-pointer hover:shadow-md
                                 transform hover:-translate-y-0.5"
                    >
                        <div className="flex justify-between items-center">
                            <div className="space-y-1">
                                <h3 className="font-bold text-xl text-gray-900 group-hover:text-blue-600
                                             transition-colors duration-200">
                                    {parseRoomName(room.roomName)}
                                </h3>
                                <div className="flex items-center text-gray-500 space-x-2">
                                    <Users size={16} />
                                    <span>참여자: {room.userCount}명</span>
                                </div>
                            </div>
                            <span className="text-sm px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg
                                         font-medium">
                                {room.type}
                            </span>
                        </div>
                        {Object.entries(room.userList).length > 0 && (
                            <div className="mt-3 pt-3 border-t border-gray-100">
                                <p className="text-sm text-gray-600">
                                    <span className="font-medium">참여자 목록:</span>{' '}
                                    {Object.values(room.userList).join(', ')}
                                </p>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            {!loading && rooms.length === 0 && (
                <div className="text-center py-12">
                    <Users size={48} className="mx-auto mb-4 text-gray-400" />
                    <p className="text-gray-500 text-lg">
                        생성된 채팅방이 없습니다.
                    </p>
                </div>
            )}
        </div>
    );
};
