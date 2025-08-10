<template>
  <main>
    <div class="page-content">
      <h1>🔐 Login</h1>
      
      <!-- Loading State -->
      <div v-if="authStore.loading" class="loading-state">
        <div class="spinner"></div>
        <p>Logging you in...</p>
      </div>

      <!-- Error Message -->
      <div v-if="authStore.error" class="error-message">
        <p>❌ {{ authStore.error }}</p>
        <button @click="authStore.clearMessages()" class="btn-small">Dismiss</button>
      </div>

      <!-- Success Message -->
      <div v-if="authStore.successMessage" class="success-message">
        <p>✅ {{ authStore.successMessage }}</p>
        <button @click="redirectToDashboard" class="btn-primary">Go to Dashboard</button>
      </div>

      <!-- Login Form -->
      <form v-if="!authStore.isAuthenticated" @submit.prevent="handleLogin" :disabled="authStore.loading">
        <div class="form-group">
          <label for="email">📧 Email:</label>
          <input
            type="email"
            id="email"
            v-model="loginForm.email"
            :disabled="authStore.loading"
            :class="{ 'error': emailError }"
            required
            placeholder="Enter your email"
            @blur="validateEmail"
          />
          <div v-if="emailError" class="field-error">{{ emailError }}</div>
        </div>

        <div class="form-group">
          <label for="password">🔑 Password:</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            :disabled="authStore.loading"
            :class="{ 'error': passwordError }"
            required
            placeholder="Enter your password"
            @blur="validatePassword"
          />
          <div v-if="passwordError" class="field-error">{{ passwordError }}</div>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="loginForm.rememberMe" :disabled="authStore.loading">
            <span class="checkmark"></span>
            Remember me
          </label>
        </div>

        <div class="text-center">
          <button 
            type="submit" 
            :disabled="authStore.loading || !isFormValid"
            class="btn-primary"
          >
            <span v-if="authStore.loading">Logging in...</span>
            <span v-else>Login</span>
          </button>
        </div>
      </form>

      <!-- Already Logged In -->
      <div v-if="authStore.isAuthenticated && !authStore.successMessage" class="logged-in-state">
        <p>✅ You are already logged in as: <strong>{{ authStore.user.email }}</strong></p>
        <div class="button-group">
          <button @click="redirectToDashboard" class="btn-primary">Go to Dashboard</button>
          <button @click="authStore.logout()" class="btn-secondary">Logout</button>
        </div>
      </div>

      <!-- Additional Links -->
      <div class="auth-links">
        <p class="text-center">
          Don't have an account? 
          <router-link to="/register" class="link">Register here</router-link>
        </p>
        <p class="text-center">
          <router-link to="/forgot-password" class="link">Forgot your password?</router-link>
        </p>
      </div>

      <!-- Demo Credentials -->
      <div class="demo-section">
        <h3>🧪 Demo Credentials</h3>
        <div class="demo-credentials">
          <div class="credential-item">
            <strong>Admin:</strong> admin@authapp.com / password123
            <button @click="fillDemoCredentials('admin')" class="btn-small">Use</button>
          </div>
          <div class="credential-item">
            <strong>User:</strong> user@authapp.com / password123
            <button @click="fillDemoCredentials('user')" class="btn-small">Use</button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { useAuthStore } from '../stores/authStore.js'

export default {
  name: 'Login',
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      loginForm: {
        email: '',
        password: '',
        rememberMe: false
      },
      emailError: '',
      passwordError: ''
    }
  },
  computed: {
    isFormValid() {
      return this.loginForm.email && 
             this.loginForm.password && 
             !this.emailError && 
             !this.passwordError
    }
  },
  mounted() {
    // Initialize auth store and check for existing session
    this.authStore.initialize()
    
    // Clear any previous messages
    this.authStore.clearMessages()
  },
  methods: {
    async handleLogin() {
      if (!this.isFormValid) {
        this.validateForm()
        return
      }

      console.log('🔐 Attempting login for:', this.loginForm.email)

      const success = await this.authStore.login({
        email: this.loginForm.email,
        password: this.loginForm.password
      })

      if (success) {
        // Login successful - the success message is handled by the template
        console.log('✅ Login successful, user authenticated')
        
        // Clear form
        this.loginForm.password = '' // Clear password for security
        
        // Redirect after a short delay to show success message
        setTimeout(() => {
          this.redirectToDashboard()
        }, 2000)
      } else {
        // Error message is handled by the authStore
        console.log('❌ Login failed')
      }
    },

    validateForm() {
      this.validateEmail()
      this.validatePassword()
    },

    validateEmail() {
      this.emailError = ''
      if (!this.loginForm.email) {
        this.emailError = 'Email is required'
      } else if (!this.isValidEmail(this.loginForm.email)) {
        this.emailError = 'Please enter a valid email address'
      }
    },

    validatePassword() {
      this.passwordError = ''
      if (!this.loginForm.password) {
        this.passwordError = 'Password is required'
      } else if (this.loginForm.password.length < 6) {
        this.passwordError = 'Password must be at least 6 characters'
      }
    },

    isValidEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    fillDemoCredentials(type) {
      if (type === 'admin') {
        this.loginForm.email = 'admin@authapp.com'
        this.loginForm.password = 'password123'
      } else if (type === 'user') {
        this.loginForm.email = 'user@authapp.com'
        this.loginForm.password = 'password123'
      }
      
      // Clear any validation errors
      this.emailError = ''
      this.passwordError = ''
      
      console.log(`🧪 Demo credentials filled for ${type}`)
    },

    redirectToDashboard() {
      if (this.authStore.isAdmin) {
        this.$router.push('/admin-dashboard')
        console.log('🚀 Redirecting to admin dashboard')
      } else {
        this.$router.push('/user-dashboard')
        console.log('🚀 Redirecting to user dashboard')
      }
    }
  }
}
</script>
