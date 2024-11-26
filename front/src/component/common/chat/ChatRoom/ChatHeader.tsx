import React from 'react';
import { Users, LogOut } from 'lucide-react';
import { Button } from '../Button';

interface ChatHeaderProps {
    roomName: string;
    userCount: number;
    isConnected: boolean;
    onExit: () => void;
    isExiting: boolean;
}

export const ChatHeader: React.FC<ChatHeaderProps> = ({
                                                          roomName,
                                                          userCount,
                                                          isConnected,
                                                          onExit,
                                                          isExiting
                                                      }) => {
    const formatRoomName = (name: string) => {
        try {
            return name.replace(/[{"}]/g, '').split(':')[1]?.trim() || name;
        } catch {
            return name;
        }
    };

    return (
        <div className="bg-white shadow-lg p-6">
            <div className="flex justify-between items-center">
                <div className="space-y-1">
                    <h1 className="text-2xl font-bold text-gray-900">
                        {formatRoomName(roomName)}
                    </h1>
                    <div className="flex items-center space-x-2 text-gray-600">
                        <Users size={18} />
                        <p className="text-sm">
                            {isConnected ? `참여자 ${userCount}명` : '연결 중...'}
                        </p>
                    </div>
                </div>
                <Button
                    onClick={onExit}
                    disabled={isExiting}
                    variant="danger"
                    className="flex items-center space-x-2"
                >
                    <LogOut size={18} />
                    <span>{isExiting ? '나가는 중...' : '나가기'}</span>
                </Button>
            </div>
        </div>
    );
};