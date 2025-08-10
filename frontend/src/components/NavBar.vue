<template>
  <nav>
    <div class="container">
      <div class="nav-brand">
        <router-link to="/" class="brand-link">🔐 AuthApp</router-link>
      </div>
      
      <div class="nav-links">
        <!-- Public Links -->
        <router-link to="/" class="nav-link">Home</router-link>
        
        <!-- Guest Links (not authenticated) -->
        <template v-if="!authStore.isAuthenticated">
          <router-link to="/login" class="nav-link">Login</router-link>
          <router-link to="/register" class="nav-link">Register</router-link>
        </template>
        
        <!-- Authenticated User Links -->
        <template v-else>
          <!-- User Dashboard Link -->
          <router-link 
            v-if="!authStore.isAdmin" 
            to="/user-dashboard" 
            class="nav-link"
          >
            User Dashboard
          </router-link>
          
          <!-- Admin Dashboard Link -->
          <router-link 
            v-if="authStore.isAdmin" 
            to="/admin-dashboard" 
            class="nav-link"
          >
            Admin Dashboard
          </router-link>
          
          <!-- Change Password Link -->
          <router-link to="/change-password" class="nav-link">Change Password</router-link>
          
          <!-- User Info -->
          <span class="user-info">
            👤 {{ authStore.user.email }}
          </span>
          
          <!-- Logout Button -->
          <button @click="handleLogout" class="logout-btn">
            Logout
          </button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '../stores/authStore.js'

export default {
  name: 'NavBar',
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  mounted() {
    // Initialize auth store to check for existing session
    this.authStore.initialize()
  },
  methods: {
    async handleLogout() {
      console.log('🚪 Logging out from navbar')
      const success = await this.authStore.logout()
      
      if (success) {
        // Redirect to home page after logout
        this.$router.push('/')
        console.log('✅ Logged out successfully, redirected to home')
      }
    }
  }
}
</script>

<style scoped>
nav {
  background-color: #343a40;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand .brand-link {
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav-brand .brand-link:hover {
  color: #007bff;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.nav-link:hover {
  background-color: #495057;
}

.nav-link.router-link-active {
  background-color: #007bff;
}

.user-info {
  color: #adb5bd;
  font-size: 0.9rem;
  padding: 0.5rem;
}

.logout-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background-color: #c82333;
}

/* Responsive Design */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
    gap: 1rem;
  }
  
  .nav-link {
    padding: 0.25rem 0.75rem;
    font-size: 0.9rem;
  }
}
</style>
