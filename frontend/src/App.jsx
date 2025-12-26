import React, { useState } from 'react';
import { useChat } from './useChat';
import './App.css'; // We will add styles next

// Generate a random User ID (e.g., User_842)
const USER_ID = `User_${Math.floor(Math.random() * 1000)}`;
const ROOM_ID = "general"; // Default room

function App() {
  const { messages, sendMessage, sendTyping, typingUsers } = useChat(ROOM_ID, USER_ID);
  const [newMessage, setNewMessage] = useState("");

  const handleSend = () => {
    if (newMessage.trim() !== "") {
      sendMessage(newMessage, USER_ID);
      setNewMessage("");
    }
  };
    const handleInputChange = (e) => {
        setNewMessage(e.target.value);
        sendTyping(); // <--- This line is what sends the signal!
    };
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') handleSend();
  };

  return (
    <div className="chat-container">
      <header className="chat-header">
        <h1>Distributed Chat</h1>
        <p>Logged in as: <strong>{USER_ID}</strong> in Room: <strong>{ROOM_ID}</strong></p>
      </header>

      <div className="message-list">
        {messages.map((msg, index) => {
          const isMyMessage = msg.sender === USER_ID;
          return (
            <div 
              key={index} 
              className={`message-bubble ${isMyMessage ? 'my-message' : 'other-message'}`}
            >
              <span className="sender-name">{isMyMessage ? 'You' : msg.sender}</span>
              <p>{msg.content}</p>
            </div>
          );
        })}
      </div>
      <div className="typing-indicator">
        {typingUsers.size > 0 && (
          <p>
            {/* Convert Set to Array to join names with commas */}
            {Array.from(typingUsers).join(", ")} {typingUsers.size > 1 ? "are" : "is"} typing...
          </p>
        )}
      </div>

      <div className="input-area">
        <input
          type="text"
          placeholder="Type a message..."
          value={newMessage}
          // CORRECT: It must call the handler that does BOTH tasks
          onChange={handleInputChange} 
          
          onKeyDown={handleKeyPress}
        />
        <button onClick={handleSend}>Send</button>
      </div>
    </div>
  );
}

export default App;