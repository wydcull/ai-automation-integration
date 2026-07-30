import MessageList from './MessageList';
import ChatInput from './ChatInput';
import { useChat } from '../hooks/useChat';

export default function ChatWindow() {
  const { sessionId, messages, loading, error, send, clear } = useChat();

  return (
    <div className="chat-window">
      <header className="chat-header">
        <div>
          <h1>ShopEasy Support</h1>
          <small>Session: {sessionId}</small>
        </div>
        <button onClick={clear} className="clear-btn">
          Clear chat
        </button>
      </header>

      <MessageList messages={messages} loading={loading} />

      {error && <div className="error-banner">{error}</div>}

      <ChatInput onSend={send} disabled={loading} />
    </div>
  );
}