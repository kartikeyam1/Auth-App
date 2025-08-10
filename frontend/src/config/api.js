// API Configuration
import axios from 'axios'

// Base URL for the backend API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10 seconds timeout
})

// Request interceptor (can be used later for authentication tokens)
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token to requests when available
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    console.log(`🔄 ${config.method?.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    console.error('❌ Request Error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    console.log(`✅ ${response.config.method?.toUpperCase()} ${response.config.url} - ${response.status}`)
    return response
  },
  (error) => {
    console.error(`❌ ${error.config?.method?.toUpperCase()} ${error.config?.url} - ${error.response?.status}`)
    
    // Handle common error scenarios
    if (error.response?.status === 401) {
      // Unauthorized - remove invalid token and redirect to login
      localStorage.removeItem('authToken')
      // Note: In a real app, you might want to redirect to login page here
      console.log('🔓 Unauthorized - token removed')
    }
    
    return Promise.reject(error)
  }
)

export default apiClient
export { API_BASE_URL }
