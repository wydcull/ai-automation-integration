import { useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';

const SUGGESTIONS = [
  'Do you have laptops?',
  'Track order ORD-12345',
  'What is your return policy?',
  'Show me headphones',
];

export default function MessageList({ messages, loading, onSuggestionClick }) {
  const endRef = useRef(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  return (
    <div className="message-list">
      {messages.length === 0 && !loading && (
        <div className="empty-state">
          <p className="empty-hint">How can I help you today?</p>
          <div className="suggestions">
            {SUGGESTIONS.map((text) => (
              <button
                key={text}
                type="button"
                className="suggestion-chip"
                onClick={() => onSuggestionClick?.(text)}
              >
                {text}
              </button>
            ))}
          </div>
        </div>
      )}

      {messages.map((m, i) => (
        <MessageBubble key={i} role={m.role} content={m.content} />
      ))}

      {loading && (
        <div className="bubble-row bot">
          <div className="bubble bot-bubble typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      )}

      <div ref={endRef} />
    </div>
  );
}