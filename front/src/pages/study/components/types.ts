interface Message {
    id: string;
    content: string;
    sender: string;
    timestamp: Date;
}

interface Channel {
    id: string;
    name: string;
    unreadCount?: number;
}
