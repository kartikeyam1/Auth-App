import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService, apiUtils } from '../services/apiService.js'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null)
  const sessionId = ref(null)
  const sessionExpiry = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const successMessage = ref(null)

  // Computed
  const isAuthenticated = computed(() => !!user.value && !!sessionId.value)
  const isAdmin = computed(() => user.value?.roles?.includes('ROLE_ADMIN') || false)
  const isSessionValid = computed(() => {
    if (!sessionExpiry.value) return false
    return new Date() < new Date(sessionExpiry.value)
  })

  // Actions
  async function login(credentials) {
    loading.value = true
    error.value = null
    successMessage.value = null

    try {
      console.log('🔐 Attempting login for:', credentials.email)
      const response = await authService.login(credentials)

      if (response.success) {
        // Store user data and session info
        user.value = {
          id: response.id,
          email: response.email,
          roles: response.roles,
          enabled: response.enabled,
          lastLogin: response.lastLogin
        }
        sessionId.value = response.sessionId
        sessionExpiry.value = response.sessionExpiry

        // Store in localStorage for persistence
        localStorage.setItem('auth_user', JSON.stringify(user.value))
        localStorage.setItem('auth_session_id', sessionId.value)
        localStorage.setItem('auth_session_expiry', sessionExpiry.value)

        successMessage.value = 'Login successful! Welcome back.'
        console.log('✅ Login successful for user:', user.value.email)
        return true
      } else {
        error.value = response.message || 'Login failed'
        console.warn('❌ Login failed:', error.value)
        return false
      }
    } catch (err) {
      error.value = apiUtils.getErrorMessage(err)
      console.error('❌ Login error:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    loading.value = true
    error.value = null

    try {
      console.log('🚪 Logging out user:', user.value?.email)
      
      if (sessionId.value) {
        await authService.logout(sessionId.value)
      }

      // Clear state
      user.value = null
      sessionId.value = null
      sessionExpiry.value = null
      successMessage.value = 'Logged out successfully'

      // Clear localStorage
      localStorage.removeItem('auth_user')
      localStorage.removeItem('auth_session_id')
      localStorage.removeItem('auth_session_expiry')

      console.log('✅ Logout successful')
      return true
    } catch (err) {
      error.value = apiUtils.getErrorMessage(err)
      console.error('❌ Logout error:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  async function changePassword(passwordData) {
    loading.value = true
    error.value = null
    successMessage.value = null

    try {
      console.log('🔑 Changing password for user:', passwordData.email)
      const response = await authService.changePassword(passwordData)

      if (response.success) {
        successMessage.value = 'Password changed successfully!'
        console.log('✅ Password change successful')
        return true
      } else {
        error.value = response.message || 'Password change failed'
        console.warn('❌ Password change failed:', error.value)
        return false
      }
    } catch (err) {
      error.value = apiUtils.getErrorMessage(err)
      console.error('❌ Password change error:', err)
      return false
    } finally {
      loading.value = false
    }
  }

  async function validateSession() {
    if (!sessionId.value) return false

    try {
      const response = await authService.validateSession(sessionId.value)
      return response.valid || false
    } catch (err) {
      console.error('❌ Session validation error:', err)
      return false
    }
  }

  function restoreSession() {
    try {
      const storedUser = localStorage.getItem('auth_user')
      const storedSessionId = localStorage.getItem('auth_session_id')
      const storedSessionExpiry = localStorage.getItem('auth_session_expiry')

      if (storedUser && storedSessionId && storedSessionExpiry) {
        const expiryDate = new Date(storedSessionExpiry)
        
        // Check if session is still valid
        if (new Date() < expiryDate) {
          user.value = JSON.parse(storedUser)
          sessionId.value = storedSessionId
          sessionExpiry.value = storedSessionExpiry
          console.log('✅ Session restored for user:', user.value.email)
          return true
        } else {
          // Session expired, clear storage
          clearStoredSession()
          console.log('⏰ Session expired, cleared storage')
        }
      }
    } catch (err) {
      console.error('❌ Error restoring session:', err)
      clearStoredSession()
    }
    return false
  }

  function clearStoredSession() {
    localStorage.removeItem('auth_user')
    localStorage.removeItem('auth_session_id')
    localStorage.removeItem('auth_session_expiry')
  }

  function clearMessages() {
    error.value = null
    successMessage.value = null
  }

  // Initialize store
  function initialize() {
    restoreSession()
  }

  return {
    // State
    user,
    sessionId,
    sessionExpiry,
    loading,
    error,
    successMessage,
    
    // Computed
    isAuthenticated,
    isAdmin,
    isSessionValid,
    
    // Actions
    login,
    logout,
    changePassword,
    validateSession,
    restoreSession,
    clearMessages,
    initialize
  }
})
