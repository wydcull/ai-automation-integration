import { useState } from 'react';

export default function ChatInput({ onSend, disabled }) {
  const [text, setText] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!text.trim()) return;
    onSend(text.trim());
    setText('');
  };

  return (
    <form className="chat-input" onSubmit={handleSubmit}>
      <textarea
  value={text}
  onChange={(e) => setText(e.target.value)}
  onKeyDown={(e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  }}
  placeholder="Ask about products, orders, or shipping..."
  maxLength={1000}
  disabled={disabled}
  rows={1}
/>
      <button type="submit" disabled={disabled || !text.trim()}>
        Send
      </button>
    </form>
  );
}