<template>
  <main>
    <div class="page-content">
      <h1>Register</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            type="email"
            id="email"
            v-model="registerForm.email"
            required
            placeholder="Enter your email"
          />
        </div>
        <div class="form-group">
          <label for="password">Password:</label>
          <input
            type="password"
            id="password"
            v-model="registerForm.password"
            required
            placeholder="Enter your password"
            minlength="6"
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">Confirm Password:</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="registerForm.confirmPassword"
            required
            placeholder="Confirm your password"
            minlength="6"
          />
        </div>
        <div class="text-center">
          <button type="submit">Register</button>
        </div>
      </form>
      <p class="text-center mt-2">
        Already have an account? 
        <router-link to="/login">Login here</router-link>
      </p>
    </div>
  </main>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      registerForm: {
        email: '',
        password: '',
        confirmPassword: ''
      }
    }
  },
  methods: {
    handleRegister() {
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        alert('Passwords do not match!')
        return
      }
      console.log('Register form submitted:', this.registerForm)
      // Registration logic will be implemented in Part 5
      alert('Registration functionality will be implemented in Part 5')
    }
  }
}
</script>
