import { useState, useEffect, useCallback } from 'react';
import { sendMessage, getRecentHistory, clearHistory } from '../api/chatApi';

const createSessionId = () =>
  `session-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;

const getOrCreateSessionId = () => {
  let id = localStorage.getItem('chatSessionId');
  if (!id) {
    id = createSessionId();
    localStorage.setItem('chatSessionId', id);
  }
  return id;
};

export function useChat() {
  const [sessionId, setSessionId] = useState(getOrCreateSessionId);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    getRecentHistory(sessionId)
      .then((history) => {
        setMessages(
          history.map((m) => ({
            role: m.role,
            content: m.content,
            timestamp: m.createdAt,
          }))
        );
      })
      .catch(() => {
        setMessages([]);
      });
  }, [sessionId]);

  const send = useCallback(async (text) => {
    if (!text.trim() || loading) return;

    setError(null);
    setMessages((prev) => [
      ...prev,
      { role: 'USER', content: text, timestamp: new Date().toISOString() },
    ]);
    setLoading(true);

    try {
      const res = await sendMessage(sessionId, text);
      setMessages((prev) => [
        ...prev,
        {
          role: 'ASSISTANT',
          content: res.reply,
          timestamp: res.timestamp,
        },
      ]);
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        'Failed to get response. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, [sessionId, loading]);

  const clear = useCallback(async () => {
    await clearHistory(sessionId);
    setMessages([]);
    setError(null);
  }, [sessionId]);

  // NEW: start a brand-new conversation
  const startNewChat = useCallback(() => {
    const newId = createSessionId();
    localStorage.setItem('chatSessionId', newId);
    setSessionId(newId);
    setMessages([]);
    setError(null);
  }, []);

  return { sessionId, messages, loading, error, send, clear, startNewChat };
}