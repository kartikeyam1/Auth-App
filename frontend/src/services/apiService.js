// API Service Layer
import apiClient from '../config/api.js'

// Health Check and System Status
export const healthService = {
  // Get backend health status
  async getHealth() {
    const response = await apiClient.get('/test/health')
    return response.data
  },

  // Get database statistics
  async getStats() {
    const response = await apiClient.get('/test/stats')
    return response.data
  }
}

// Authentication Service
export const authService = {
  // User login
  async login(credentials) {
    const response = await apiClient.post('/auth/login', credentials)
    return response.data
  },

  // User logout
  async logout(sessionId) {
    const response = await apiClient.post('/auth/logout', {}, {
      headers: {
        'X-Session-ID': sessionId
      }
    })
    return response.data
  },

  // Change password
  async changePassword(passwordData) {
    const response = await apiClient.post('/auth/change-password', passwordData)
    return response.data
  },

  // Validate session
  async validateSession(sessionId) {
    const response = await apiClient.get('/auth/validate', {
      headers: {
        'X-Session-ID': sessionId
      }
    })
    return response.data
  }
}

// User Management Service
export const userService = {
  // Get all users
  async getAllUsers() {
    const response = await apiClient.get('/test/users')
    return response.data
  },

  // Get user by ID
  async getUserById(id) {
    const response = await apiClient.get(`/test/users/${id}`)
    return response.data
  },

  // Get user by email
  async getUserByEmail(email) {
    const response = await apiClient.get(`/test/users/email/${email}`)
    return response.data
  },

  // Create new user
  async createUser(userData) {
    const response = await apiClient.post('/test/users', userData)
    return response.data
  },

  // Update user
  async updateUser(id, userData) {
    const response = await apiClient.put(`/test/users/${id}`, userData)
    return response.data
  },

  // Delete user
  async deleteUser(id) {
    const response = await apiClient.delete(`/test/users/${id}`)
    return response.data
  }
}

// Utility functions for API error handling
export const apiUtils = {
  // Extract error message from API error response
  getErrorMessage(error) {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    if (error.response?.data?.error) {
      return error.response.data.error
    }
    if (error.message) {
      return error.message
    }
    return 'An unexpected error occurred'
  },

  // Check if error is a network error
  isNetworkError(error) {
    return !error.response && error.code === 'NETWORK_ERROR'
  },

  // Check if error is a timeout
  isTimeoutError(error) {
    return error.code === 'ECONNABORTED'
  }
}
