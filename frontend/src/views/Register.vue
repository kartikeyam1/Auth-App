<template>
  <main>
    <div class="page-content">
      <h1>📝 Create New Account</h1>
      <p class="subtitle">Join our secure platform by creating your account</p>
      
      <!-- Registration Form -->
      <div class="form-container">
        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-group">
            <label for="email">📧 Email Address:</label>
            <input
              type="email"
              id="email"
              v-model="registerForm.email"
              required
              placeholder="Enter your email address"
              :disabled="isLoading"
              class="form-input"
            />
          </div>
          
          <div class="form-group">
            <label for="password">🔐 Password:</label>
            <input
              type="password"
              id="password"
              v-model="registerForm.password"
              required
              placeholder="Enter your password (min. 6 characters)"
              minlength="6"
              :disabled="isLoading"
              class="form-input"
            />
            <small class="form-hint">Password must be at least 6 characters long</small>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">🔐 Confirm Password:</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="registerForm.confirmPassword"
              required
              placeholder="Confirm your password"
              minlength="6"
              :disabled="isLoading"
              class="form-input"
              :class="{ 'password-mismatch': passwordMismatch }"
            />
            <small v-if="passwordMismatch" class="error-hint">Passwords do not match</small>
          </div>

          <!-- Role Selection (for demo purposes) -->
          <div class="form-group">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="registerForm.requestAdminRole"
                :disabled="isLoading"
              />
              🔑 Request Administrator Role (for testing)
            </label>
            <small class="form-hint">Check this to create an admin account for testing purposes</small>
          </div>
          
          <div class="form-actions">
            <button 
              type="submit" 
              class="register-btn"
              :disabled="isLoading || passwordMismatch || !isFormValid"
            >
              {{ isLoading ? '⏳ Creating Account...' : '✅ Create Account' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Success Message -->
      <div v-if="successMessage" class="success-message">
        ✅ {{ successMessage }}
        <div class="success-actions">
          <router-link to="/login" class="login-link">🔐 Go to Login</router-link>
          <button @click="clearMessages" class="close-btn">✖️</button>
        </div>
      </div>

      <!-- Error Message -->
      <div v-if="errorMessage" class="error-message">
        ❌ {{ errorMessage }}
        <button @click="clearMessages" class="close-btn">✖️</button>
      </div>

      <!-- Alternative Actions -->
      <div class="alternative-actions">
        <p class="text-center">
          Already have an account? 
          <router-link to="/login" class="link">🔐 Login here</router-link>
        </p>
        <p class="text-center">
          <router-link to="/" class="link">🏠 Back to Home</router-link>
        </p>
      </div>

      <!-- Current Users Preview (for demo) -->
      <div v-if="userStore.users.length > 0" class="users-preview">
        <h3>👥 Current Users ({{ userStore.usersCount }} total)</h3>
        <div class="users-grid">
          <div 
            v-for="user in userStore.users.slice(0, 6)" 
            :key="user.id" 
            class="user-preview-card"
          >
            <span class="user-email">{{ user.email }}</span>
            <div class="user-roles">
              <span 
                v-for="role in user.roles" 
                :key="role" 
                class="role-badge"
                :class="{ 'admin-role': role === 'ROLE_ADMIN' }"
              >
                {{ formatRole(role) }}
              </span>
            </div>
          </div>
        </div>
        <p class="preview-note">
          <small>👆 These are existing users in the system. Your new account will appear here after registration.</small>
        </p>
      </div>
    </div>
  </main>
</template>

<script>
import { useUserStore } from '../stores/userStore.js'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Register',
  setup() {
    const userStore = useUserStore()
    const router = useRouter()
    
    // Form data
    const registerForm = ref({
      email: '',
      password: '',
      confirmPassword: '',
      requestAdminRole: false
    })

    // State
    const isLoading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')

    // Load users on mount to show current users
    onMounted(async () => {
      await userStore.fetchUsers()
    })

    // Computed properties
    const passwordMismatch = computed(() => {
      return registerForm.value.password && 
             registerForm.value.confirmPassword && 
             registerForm.value.password !== registerForm.value.confirmPassword
    })

    const isFormValid = computed(() => {
      return registerForm.value.email && 
             registerForm.value.password.length >= 6 && 
             registerForm.value.confirmPassword.length >= 6 &&
             !passwordMismatch.value
    })

    // Handle registration
    const handleRegister = async () => {
      if (passwordMismatch.value) {
        errorMessage.value = 'Passwords do not match!'
        return
      }

      if (!isFormValid.value) {
        errorMessage.value = 'Please fill in all fields correctly'
        return
      }

      isLoading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        // Create user data for API
        const userData = {
          email: registerForm.value.email,
          password: registerForm.value.password
        }

        console.log('🚀 Creating user:', userData.email)

        // Call the API to create user
        const newUser = await userStore.createUser(userData)

        console.log('✅ User created successfully:', newUser)

        // Show success message
        successMessage.value = `Account created successfully for ${newUser.email}! ${
          registerForm.value.requestAdminRole ? 
          'Note: Admin role assignment will be implemented in the next authentication phase.' : 
          'You can now log in with your credentials.'
        }`

        // Clear form
        registerForm.value = {
          email: '',
          password: '',
          confirmPassword: '',
          requestAdminRole: false
        }

        // Refresh users list to show the new user
        await userStore.fetchUsers()

      } catch (error) {
        console.error('❌ Registration failed:', error)
        
        // Extract error message
        if (error.response?.data?.message) {
          errorMessage.value = error.response.data.message
        } else if (error.response?.data?.error) {
          errorMessage.value = error.response.data.error
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = 'Registration failed. Please try again.'
        }

        // If it's a duplicate email error, provide helpful message
        if (errorMessage.value.toLowerCase().includes('email') || 
            errorMessage.value.toLowerCase().includes('duplicate') ||
            errorMessage.value.toLowerCase().includes('exists')) {
          errorMessage.value = `Email ${registerForm.value.email} is already registered. Please use a different email or login instead.`
        }
      } finally {
        isLoading.value = false
      }
    }

    // Clear messages
    const clearMessages = () => {
      successMessage.value = ''
      errorMessage.value = ''
      userStore.clearMessages()
    }

    // Format role for display
    const formatRole = (role) => {
      return role.replace('ROLE_', '').toLowerCase()
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ')
    }

    return {
      userStore,
      registerForm,
      isLoading,
      successMessage,
      errorMessage,
      passwordMismatch,
      isFormValid,
      handleRegister,
      clearMessages,
      formatRole
    }
  }
}
</script>

<style scoped>
.page-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.subtitle {
  text-align: center;
  color: #6c757d;
  margin-bottom: 2rem;
}

.form-container {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 2rem;
  margin-bottom: 2rem;
  border: 1px solid #e9ecef;
}

.register-form {
  max-width: 100%;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #495057;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #ced4da;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.form-input:disabled {
  background-color: #e9ecef;
  opacity: 0.6;
}

.password-mismatch {
  border-color: #dc3545 !important;
}

.form-hint {
  display: block;
  margin-top: 0.25rem;
  color: #6c757d;
  font-size: 0.875rem;
}

.error-hint {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
  margin: 0;
}

.form-actions {
  text-align: center;
  margin-top: 2rem;
}

.register-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 0.875rem 2rem;
  border-radius: 6px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
  min-width: 200px;
}

.register-btn:hover:not(:disabled) {
  background: #218838;
}

.register-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.success-message, .error-message {
  padding: 1rem 1.5rem;
  border-radius: 6px;
  margin: 1rem 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.success-message {
  background: #d1e7dd;
  color: #0f5132;
  border: 1px solid #badbcc;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.success-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.login-link {
  background: #007bff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  font-size: 0.9rem;
}

.login-link:hover {
  background: #0056b3;
  color: white;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  opacity: 0.7;
  padding: 0.25rem;
}

.close-btn:hover {
  opacity: 1;
}

.alternative-actions {
  text-align: center;
  margin: 2rem 0;
}

.alternative-actions p {
  margin: 0.5rem 0;
}

.link {
  color: #007bff;
  text-decoration: none;
  font-weight: 500;
}

.link:hover {
  color: #0056b3;
  text-decoration: underline;
}

.users-preview {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  margin-top: 2rem;
  border: 1px solid #e9ecef;
}

.users-preview h3 {
  margin-top: 0;
  color: #495057;
  text-align: center;
}

.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin: 1rem 0;
}

.user-preview-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 1rem;
  text-align: center;
}

.user-email {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #495057;
}

.user-roles {
  display: flex;
  gap: 0.25rem;
  justify-content: center;
  flex-wrap: wrap;
}

.role-badge {
  background: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}

.role-badge.admin-role {
  background: #dc3545;
  color: white;
}

.preview-note {
  text-align: center;
  margin-top: 1rem;
  color: #6c757d;
}
</style>
