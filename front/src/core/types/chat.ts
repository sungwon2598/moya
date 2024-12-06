export interface ChatRoomInfo {
    roomId: string;
    roomName: string;
    userCount: number;
    type: string;
    userList: Record<string, string>;
}

export interface CreateRoomRequest {
    roomName: string;
}

export enum MessageType {
    JOIN = 'JOIN',
    CHAT = 'CHAT',
    LEAVE = 'LEAVE'
}

export interface ChatMessage {
    type: MessageType;
    roomId: string;
    sender: string;
    message: string;
    timestamp: string;  // ISO string format
}

export interface ChatListProps {
    onRoomSelect: (roomInfo: ChatRoomInfo) => void;  // 수정된 부분
    username: string;
}

export interface ChatRoomProps {
    roomId: string;
    roomInfo: ChatRoomInfo;
    username: string;
    onExit: () => void;
}