<template>
  <main>
    <div class="page-content">
      <h1>Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            type="email"
            id="email"
            v-model="loginForm.email"
            required
            placeholder="Enter your email"
          />
        </div>
        <div class="form-group">
          <label for="password">Password:</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            placeholder="Enter your password"
          />
        </div>
        <div class="text-center">
          <button type="submit">Login</button>
        </div>
      </form>
      <p class="text-center mt-2">
        Don't have an account? 
        <router-link to="/register">Register here</router-link>
      </p>
    </div>
  </main>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        email: '',
        password: ''
      }
    }
  },
  methods: {
    handleLogin() {
      console.log('Login form submitted:', this.loginForm)
      // Login logic will be implemented in Part 5
      alert('Login functionality will be implemented in Part 5')
    }
  }
}
</script>
