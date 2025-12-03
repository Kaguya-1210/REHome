import axios from './http'

/**
 * @typedef {Object} LoginPayload
 * @property {string} account
 * @property {string} password
 * @property {string} code
 */

export async function login(payload) {
  const { account, password, code } = payload
  return axios.post(`/admin/login?code=${encodeURIComponent(code)}`, { account, password })
}

export async function refresh() {
  return axios.post('/auth/refresh')
}

export async function check() {
  return axios.get('/auth/check')
}

export async function logout() {
  try { await axios.post('/auth/logout') } catch {}
  if (window.location.pathname !== '/admin') window.location.href = '/admin'
}
