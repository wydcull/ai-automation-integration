import { useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';

export default function MessageList({ messages, loading }) {
  const endRef = useRef(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  return (
    <div className="message-list">
      {messages.length === 0 && (
        <p className="empty-hint">
          Ask about products, orders (ORD-xxxxx), or shipping.
        </p>
      )}
      {messages.map((m, i) => (
        <MessageBubble key={i} role={m.role} content={m.content} />
      ))}
      {loading && (
        <div className="bubble-row bot">
          <div className="bubble bot-bubble typing">Thinking...</div>
        </div>
      )}
      <div ref={endRef} />
    </div>
  );
}