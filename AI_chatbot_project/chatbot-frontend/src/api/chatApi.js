import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL;

export const sendMessage = async (sessionId, message) => {
  const { data } = await axios.post(`${API_BASE}/send`, {
    sessionId,
    message,
  });
  return data; // { sessionId, reply, timestamp }
};

export const getRecentHistory = async (sessionId, limit = 50) => {
  const { data } = await axios.get(`${API_BASE}/history/${sessionId}/recent`, {
    params: { limit },
  });
  return data; // ChatMessage[]
};

export const clearHistory = async (sessionId) => {
  const { data } = await axios.delete(`${API_BASE}/history/${sessionId}`);
  return data;
};

export const checkHealth = async () => {
  const { data } = await axios.get(`${API_BASE}/health`);
  return data;
};