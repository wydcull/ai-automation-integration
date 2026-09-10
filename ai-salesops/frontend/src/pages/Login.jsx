import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import '../Login.css';

export default function Login() {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        email: '',
        password: '',
        role: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const data = await login(form);
            localStorage.setItem('user', JSON.stringify(data));
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.error || 'Login failed');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-page">
            <form className="login-panel" onSubmit={handleSubmit}>
                <p className="brand">AI SalesOps</p>
                <h1></h1>
                <p className="subtitle">Access leads, tasks, and assignments.</p>
                <label>
                    Email
                    <input
                        type="email"
                        placeholder="Email"
                        value={form.email}
                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                        required
                    />
                </label>
                <label>
                    Password
                    <input
                        type="password"
                        placeholder="Password"
                        value={form.password}
                        onChange={(e) => setForm({ ...form, password: e.target.value })}
                        required
                    />
                </label>
                <label>
                    Role
                    <select
                        value={form.role}
                        onChange={(e) => setForm({ ...form, role: e.target.value })}
                        required>
                        <option value="">Select</option>
                        <option value="Adm">Admin</option>
                        <option value="Man">Manager</option>
                        <option value="REP">Sales Representative</option>
                    </select>
                </label>
                {error && <p className="error">{error}</p>}
                <button type="submit" disabled={loading}>
                    {loading ? 'Signing in…' : 'Log in'}
                </button>
            </form>
        </div>
    );
}