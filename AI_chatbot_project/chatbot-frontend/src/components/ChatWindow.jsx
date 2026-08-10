import MessageList from './MessageList';
import ChatInput from './ChatInput';
import { useChat } from '../hooks/useChat';

export default function ChatWindow() {
  const { messages, loading, error, send, clear, startNewChat } = useChat();

  return (
    <div className="chat-window">
      <header className="chat-header">
        <div>
          <h1>ShopEasy Support</h1>
          <small>Customer support assistant</small>
        </div>
        <div className="header-actions">
          <button onClick={startNewChat} className="new-chat-btn" disabled={loading}>
            New chat
          </button>
          <button onClick={clear} className="clear-btn" disabled={loading}>
            Clear chat
          </button>
        </div>
      </header>

      <MessageList messages={messages} loading={loading} onSuggestionClick={send} />

      {error && <div className="error-banner">{error}</div>}

      <ChatInput onSend={send} disabled={loading} />
    </div>
  );
}