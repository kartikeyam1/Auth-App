<template>
  <main>
    <div class="page-content">
      <h1>🔑 Change Password</h1>
      
      <!-- Authentication Check -->
      <div v-if="!authStore.isAuthenticated" class="auth-required">
        <p>❌ You must be logged in to change your password.</p>
        <router-link to="/login" class="btn-primary">Login</router-link>
      </div>

      <div v-else>
        <!-- Loading State -->
        <div v-if="authStore.loading" class="loading-state">
          <div class="spinner"></div>
          <p>Updating your password...</p>
        </div>

        <!-- Error Message -->
        <div v-if="authStore.error" class="error-message">
          <p>❌ {{ authStore.error }}</p>
          <button @click="authStore.clearMessages()" class="btn-small">Dismiss</button>
        </div>

        <!-- Success Message -->
        <div v-if="authStore.successMessage" class="success-message">
          <p>✅ {{ authStore.successMessage }}</p>
          <div class="button-group">
            <button @click="resetForm" class="btn-primary">Change Another Password</button>
            <router-link to="/user-dashboard" class="btn-secondary">Back to Dashboard</router-link>
          </div>
        </div>

        <!-- Change Password Form -->
        <form v-if="!authStore.successMessage" @submit.prevent="handleChangePassword" :disabled="authStore.loading">
          <div class="form-section">
            <h3>Current User: {{ authStore.user.email }}</h3>
          </div>

          <div class="form-group">
            <label for="currentPassword">🔐 Current Password:</label>
            <input
              type="password"
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              :disabled="authStore.loading"
              :class="{ 'error': currentPasswordError }"
              required
              placeholder="Enter your current password"
              @blur="validateCurrentPassword"
            />
            <div v-if="currentPasswordError" class="field-error">{{ currentPasswordError }}</div>
          </div>

          <div class="form-group">
            <label for="newPassword">🔑 New Password:</label>
            <input
              type="password"
              id="newPassword"
              v-model="passwordForm.newPassword"
              :disabled="authStore.loading"
              :class="{ 'error': newPasswordError }"
              required
              placeholder="Enter your new password (min 6 characters)"
              @blur="validateNewPassword"
            />
            <div v-if="newPasswordError" class="field-error">{{ newPasswordError }}</div>
            <div class="password-strength">
              <div class="strength-bar" :class="passwordStrengthClass"></div>
              <span class="strength-text">{{ passwordStrengthText }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">✅ Confirm New Password:</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              :disabled="authStore.loading"
              :class="{ 'error': confirmPasswordError }"
              required
              placeholder="Confirm your new password"
              @blur="validateConfirmPassword"
            />
            <div v-if="confirmPasswordError" class="field-error">{{ confirmPasswordError }}</div>
          </div>

          <div class="form-section">
            <h4>📝 Password Requirements:</h4>
            <ul class="requirements-list">
              <li :class="{ 'met': passwordForm.newPassword.length >= 6 }">
                At least 6 characters long
              </li>
              <li :class="{ 'met': /[A-Z]/.test(passwordForm.newPassword) }">
                Contains an uppercase letter
              </li>
              <li :class="{ 'met': /[a-z]/.test(passwordForm.newPassword) }">
                Contains a lowercase letter
              </li>
              <li :class="{ 'met': /\d/.test(passwordForm.newPassword) }">
                Contains a number
              </li>
              <li :class="{ 'met': passwordForm.newPassword === passwordForm.confirmPassword && passwordForm.confirmPassword.length > 0 }">
                Passwords match
              </li>
            </ul>
          </div>

          <div class="text-center">
            <button 
              type="submit" 
              :disabled="authStore.loading || !isFormValid"
              class="btn-primary"
            >
              <span v-if="authStore.loading">Updating Password...</span>
              <span v-else>Change Password</span>
            </button>
          </div>
        </form>

        <!-- Additional Actions -->
        <div class="additional-actions">
          <p class="text-center">
            <router-link to="/user-dashboard" class="link">← Back to Dashboard</router-link>
          </p>
          <p class="text-center">
            <button @click="authStore.logout()" class="link-button">Logout</button>
          </p>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { useAuthStore } from '../stores/authStore.js'

export default {
  name: 'ChangePassword',
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      currentPasswordError: '',
      newPasswordError: '',
      confirmPasswordError: ''
    }
  },
  computed: {
    isFormValid() {
      return this.passwordForm.currentPassword && 
             this.passwordForm.newPassword && 
             this.passwordForm.confirmPassword &&
             !this.currentPasswordError && 
             !this.newPasswordError && 
             !this.confirmPasswordError &&
             this.passwordForm.newPassword === this.passwordForm.confirmPassword
    },
    
    passwordStrengthScore() {
      let score = 0
      if (this.passwordForm.newPassword.length >= 6) score++
      if (/[A-Z]/.test(this.passwordForm.newPassword)) score++
      if (/[a-z]/.test(this.passwordForm.newPassword)) score++
      if (/\d/.test(this.passwordForm.newPassword)) score++
      if (/[^A-Za-z0-9]/.test(this.passwordForm.newPassword)) score++
      return score
    },
    
    passwordStrengthClass() {
      const score = this.passwordStrengthScore
      if (score <= 1) return 'weak'
      if (score <= 2) return 'fair'
      if (score <= 3) return 'good'
      return 'strong'
    },
    
    passwordStrengthText() {
      const score = this.passwordStrengthScore
      if (score <= 1) return 'Weak'
      if (score <= 2) return 'Fair'
      if (score <= 3) return 'Good'
      return 'Strong'
    }
  },
  mounted() {
    // Initialize auth store
    this.authStore.initialize()
    
    // Clear any previous messages
    this.authStore.clearMessages()
    
    // Check if user is authenticated
    if (!this.authStore.isAuthenticated) {
      console.log('⚠️ User not authenticated, redirecting to login')
      this.$router.push('/login')
    }
  },
  methods: {
    async handleChangePassword() {
      if (!this.isFormValid) {
        this.validateForm()
        return
      }

      console.log('🔑 Attempting password change for:', this.authStore.user.email)

      const success = await this.authStore.changePassword({
        email: this.authStore.user.email,
        currentPassword: this.passwordForm.currentPassword,
        newPassword: this.passwordForm.newPassword
      })

      if (success) {
        console.log('✅ Password change successful')
        // Success message is handled by the template
        // Clear sensitive data
        this.passwordForm.currentPassword = ''
        this.passwordForm.newPassword = ''
        this.passwordForm.confirmPassword = ''
      } else {
        console.log('❌ Password change failed')
        // Error message is handled by the authStore
      }
    },

    validateForm() {
      this.validateCurrentPassword()
      this.validateNewPassword()
      this.validateConfirmPassword()
    },

    validateCurrentPassword() {
      this.currentPasswordError = ''
      if (!this.passwordForm.currentPassword) {
        this.currentPasswordError = 'Current password is required'
      } else if (this.passwordForm.currentPassword.length < 6) {
        this.currentPasswordError = 'Password must be at least 6 characters'
      }
    },

    validateNewPassword() {
      this.newPasswordError = ''
      if (!this.passwordForm.newPassword) {
        this.newPasswordError = 'New password is required'
      } else if (this.passwordForm.newPassword.length < 6) {
        this.newPasswordError = 'Password must be at least 6 characters'
      } else if (this.passwordForm.newPassword === this.passwordForm.currentPassword) {
        this.newPasswordError = 'New password must be different from current password'
      }
    },

    validateConfirmPassword() {
      this.confirmPasswordError = ''
      if (!this.passwordForm.confirmPassword) {
        this.confirmPasswordError = 'Please confirm your new password'
      } else if (this.passwordForm.confirmPassword !== this.passwordForm.newPassword) {
        this.confirmPasswordError = 'Passwords do not match'
      }
    },

    resetForm() {
      this.passwordForm = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.currentPasswordError = ''
      this.newPasswordError = ''
      this.confirmPasswordError = ''
      this.authStore.clearMessages()
      console.log('🔄 Password form reset')
    }
  }
}
</script>

<style scoped>
.password-strength {
  margin-top: 0.5rem;
}

.strength-bar {
  height: 4px;
  border-radius: 2px;
  transition: all 0.3s ease;
  margin-bottom: 0.25rem;
}

.strength-bar.weak {
  width: 25%;
  background-color: #ff4757;
}

.strength-bar.fair {
  width: 50%;
  background-color: #ffa502;
}

.strength-bar.good {
  width: 75%;
  background-color: #3742fa;
}

.strength-bar.strong {
  width: 100%;
  background-color: #2ed573;
}

.strength-text {
  font-size: 0.875rem;
  font-weight: 500;
}

.requirements-list {
  list-style: none;
  padding: 0;
  margin: 1rem 0;
}

.requirements-list li {
  padding: 0.25rem 0;
  color: #666;
  font-size: 0.875rem;
}

.requirements-list li:before {
  content: "❌ ";
  margin-right: 0.5rem;
}

.requirements-list li.met {
  color: #2ed573;
}

.requirements-list li.met:before {
  content: "✅ ";
}

.form-section {
  margin: 2rem 0;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.form-section h3, .form-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.auth-required {
  text-align: center;
  padding: 2rem;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  color: #856404;
}

.additional-actions {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

.link-button {
  background: none;
  border: none;
  color: #007bff;
  text-decoration: underline;
  cursor: pointer;
  font-size: inherit;
}

.link-button:hover {
  color: #0056b3;
}
</style>
