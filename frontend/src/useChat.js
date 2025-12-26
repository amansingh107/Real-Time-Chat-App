import { useEffect, useRef, useState } from 'react';

const SOCKET_URL = 'ws://localhost:80/chat'; // Connects to Nginx

export const useChat = (roomId) => {
    const [messages, setMessages] = useState([]);
    const socketRef = useRef(null);

    useEffect(() => {
        // 1. Create WebSocket connection
        socketRef.current = new WebSocket(SOCKET_URL);

        // 2. Connection Opened
        socketRef.current.onopen = () => {
            console.log('Connected to Chat Server');
            // Optional: Send a "JOIN" message here if your backend supported it
        };

        // 3. Handle Incoming Messages
        socketRef.current.onmessage = (event) => {
            const incomingMessage = JSON.parse(event.data);
            
            // Only add message if it belongs to this room
            if (incomingMessage.roomId === roomId) {
                setMessages((prev) => [...prev, incomingMessage]);
            }
        };

        // 4. Cleanup on Unmount
        return () => {
            socketRef.current.close();
        };
    }, [roomId]); // Re-connect if roomId changes

    // Function to send messages
    const sendMessage = (content, sender) => {
        if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
            const payload = {
                content,
                sender,
                roomId
            };
            socketRef.current.send(JSON.stringify(payload));
        } else {
            console.error('WebSocket is not open');
        }
    };

    return { messages, sendMessage };
};