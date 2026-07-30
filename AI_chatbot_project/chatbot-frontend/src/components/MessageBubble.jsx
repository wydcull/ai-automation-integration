export default function MessageBubble({ role, content }) {
  const isUser = role === 'USER';

  return (
    <div className={`bubble-row ${isUser ? 'user' : 'bot'}`}>
      <div className={`bubble ${isUser ? 'user-bubble' : 'bot-bubble'}`}>
        {content}
      </div>
    </div>
  );
}