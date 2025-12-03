import axios from 'axios'

const isProd = typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.PROD
const baseURL = (isProd ? (window.BASE_URL?.replace('http://', 'https://') || 'https://localhost:8080') : (window.BASE_URL || 'http://localhost:8080'))

axios.defaults.baseURL = baseURL
axios.defaults.withCredentials = true
axios.defaults.headers.common['Accept'] = 'application/json'
axios.defaults.xsrfCookieName = 'XSRF-TOKEN'
axios.defaults.xsrfHeaderName = 'X-XSRF-TOKEN'

axios.interceptors.response.use(
  (res) => res,
  async (error) => {
    const { response, config } = error || {}
    if (!response) throw error
    const status = response.status
    if (status === 401) {
      try {
        await axios.post('/auth/refresh')
        return axios(config)
      } catch (e) {
        if (window.location.pathname !== '/admin') window.location.href = '/admin'
        throw error
      }
    }
    if (status === 403) {
      throw error
    }
    throw error
  }
)

export default axios
