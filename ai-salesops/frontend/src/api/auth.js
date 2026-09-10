import axios from 'axios';

export async function login({ email, password, role }) {
  const { data } = await axios.post('/api/auth/login', {
    email,
    password,
    role,
  });
  return data;
}