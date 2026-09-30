import axios from 'axios';

export async function fetchUserSummary() {
  const { data } = await axios.get('/api/dashboard/users-summary');
  return data;
}

export async function fetchDashboardSummary() {
  const { data } = await axios.get('/api/dashboard/summary');
  return data;
}

export async function fetchEmailStats() {
  const { data } = await axios.get('/api/dashboard/email-stats');
  return data;
}

export async function fetchSalesSummary() {
  const { data } = await axios.get('/api/dashboard/sales-summary');
  return data;
}

export async function fetchPendingReview() {
  const { data } = await axios.get('/api/dashboard/pending-review');
  return data;
}
