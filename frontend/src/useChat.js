import { useEffect, useRef, useState } from 'react';

const SOCKET_URL = 'ws://localhost:80/chat';

export const useChat = (roomId, userId) => {
    const [messages, setMessages] = useState([]);
    const [typingUsers, setTypingUsers] = useState(new Set()); 
    const socketRef = useRef(null);

    useEffect(() => {
        // 1. Create Connection
        socketRef.current = new WebSocket(SOCKET_URL);

        // 2. Handle Open
        socketRef.current.onopen = () => {
            console.log("Connected to Chat");
            const joinMsg = { type: 'JOIN', sender: userId, roomId, content: '' };
            socketRef.current.send(JSON.stringify(joinMsg));
        };

        // 3. Handle Messages
        socketRef.current.onmessage = (event) => {
            try {
                const msg = JSON.parse(event.data);
                
                // Filter by Room ID
                if (String(msg.roomId) !== String(roomId)) return;

                switch (msg.type) {
                    case 'TYPING':
                        handleTypingEvent(msg.sender);
                        break;
                    case 'CHAT':
                    case 'JOIN':
                    case 'LEAVE':
                        setMessages((prev) => [...prev, msg]);
                        break;
                    default:
                        break;
                }
            } catch (err) {
                console.error("Error parsing message:", err);
            }
        };

        return () => {
            if (socketRef.current) socketRef.current.close();
        };
    }, [roomId, userId]); // <--- End of useEffect

    // --- Helper Functions defined OUTSIDE useEffect ---

    const handleTypingEvent = (sender) => {
        if (sender === userId) return;
        
        setTypingUsers((prev) => {
            const newSet = new Set(prev);
            newSet.add(sender);
            return newSet;
        });

        setTimeout(() => {
            setTypingUsers((prev) => {
                const newSet = new Set(prev);
                newSet.delete(sender);
                return newSet;
            });
        }, 2000);
    };

    const sendMessage = (content) => {
        if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'CHAT', content, sender: userId, roomId
            }));
        }
    };

    // Ensure this is defined HERE, before the return
    const sendTyping = () => {
        if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'TYPING', content: '', sender: userId, roomId
            }));
        }
    };

    // Return all functions and state
    return { messages, sendMessage, sendTyping, typingUsers };
};