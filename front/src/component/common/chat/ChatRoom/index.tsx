import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { ChatHeader } from './ChatHeader.tsx';
import { MessageList } from './MessageList';
import { ChatInput } from './ChatInput';
import { ErrorMessage } from '../ErrorMessage';
import { ChatRoomProps, ChatMessage, MessageType } from '@/types/chat';
import { CHAT_API, WS_URL } from '@/config/apiConfig';

export const ChatRoom: React.FC<ChatRoomProps> = ({
                                                      roomId,
                                                      roomInfo: initialRoomInfo,
                                                      username,
                                                      onExit
                                                  }) => {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [isConnected, setIsConnected] = useState(false);
    const [roomInfo, setRoomInfo] = useState(initialRoomInfo);
    const [error, setError] = useState<string | null>(null);
    const [isExiting, setIsExiting] = useState(false);
    const clientRef = useRef<Client | null>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const fetchRoomInfo = async () => {
        try {
            const data = await CHAT_API.getRoom(roomId);
            setRoomInfo(data);
            setError(null);
        } catch (error) {
            console.error('Failed to fetch room info:', error);
            setError('방 정보를 불러오는데 실패했습니다.');
        }
    };

    // WebSocket 연결 설정
    useEffect(() => {
        const client = new Client({
            brokerURL: WS_URL,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                setIsConnected(true);
                setError(null);

                client.subscribe(`/sub/chat/room/${roomId}`, (message) => {
                    try {
                        const receivedMessage: ChatMessage = JSON.parse(message.body);
                        setMessages(prev => [...prev, receivedMessage]);
                    } catch (error) {
                        console.error('Message parsing error:', error);
                        setError('메시지 처리 중 오류가 발생했습니다.');
                    }
                });

                const joinMessage = {
                    type: MessageType.JOIN,
                    roomId,
                    sender: username,
                    message: '',
                    timestamp: new Date().toISOString()
                };

                client.publish({
                    destination: '/pub/chat/message',
                    body: JSON.stringify(joinMessage)
                });
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
                setError('채팅 연결에 실패했습니다.');
            },
            onDisconnect: () => {
                setIsConnected(false);
                if (!isExiting) {
                    setError('채팅 서버와의 연결이 끊어졌습니다.');
                }
            }
        });

        clientRef.current = client;
        client.activate();

        return () => {
            if (client.active && !isExiting) {
                handleExit();
            }
        };
    }, [roomId, username]);

    // 방 정보 주기적 업데이트
    useEffect(() => {
        if (isConnected) {
            fetchRoomInfo();
            const interval = setInterval(fetchRoomInfo, 5000);
            return () => clearInterval(interval);
        }
    }, [isConnected, roomId]);

    // 메시지 자동 스크롤
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleExit = async () => {
        try {
            setIsExiting(true);

            if (clientRef.current?.active) {
                const leaveMessage = {
                    type: MessageType.LEAVE,
                    roomId,
                    sender: username,
                    message: '',
                    timestamp: new Date().toISOString()
                };

                clientRef.current.publish({
                    destination: '/pub/chat/message',
                    body: JSON.stringify(leaveMessage)
                });

                clientRef.current.deactivate();
            }

            onExit();
        } catch (error) {
            console.error('Exit error:', error);
            setError('퇴장 처리에 실패했습니다.');
        } finally {
            setIsExiting(false);
        }
    };

    const handleSendMessage = (message: string) => {
        if (!message.trim() || !clientRef.current || !isConnected) return;

        const chatMessage = {
            type: MessageType.CHAT,
            roomId,
            sender: username,
            message: message,
            timestamp: new Date().toISOString()
        };

        clientRef.current.publish({
            destination: '/pub/chat/message',
            body: JSON.stringify(chatMessage)
        });
    };

    return (
        <div className="min-h-screen bg-gray-100 py-8">
            <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg overflow-hidden">
                {/* 에러 메시지 */}
                {error && (
                    <div className="absolute top-4 left-1/2 transform -translate-x-1/2 w-full max-w-md">
                        <ErrorMessage message={error}/>
                    </div>
                )}

                {/* 채팅방 컨테이너 */}
                <div className="flex flex-col h-[calc(100vh-4rem)]">
                    {/* 헤더 */}
                    <div className="bg-white border-b border-gray-200">
                        <ChatHeader
                            roomName={roomInfo.roomName}
                            userCount={roomInfo.userCount}
                            isConnected={isConnected}
                            onExit={handleExit}
                            isExiting={isExiting}
                        />
                    </div>

                    {/* 메시지 목록 */}
                    <div className="flex-1 overflow-y-auto bg-gray-50">
                        <MessageList
                            messages={messages}
                            currentUser={username}
                            messagesEndRef={messagesEndRef}
                        />
                    </div>

                    {/* 입력창 */}
                    <div className="bg-white border-t border-gray-200">
                        <ChatInput
                            onSendMessage={handleSendMessage}
                            isConnected={isConnected}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};