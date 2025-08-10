<template>
  <main>
    <div class="page-content">
      <h1>Welcome to the Secure Web App</h1>
      <p>This is a secure web application built with Vue.js 3 and Spring Boot.</p>
      
      <!-- Backend Connection Status -->
      <div class="system-status-card">
        <h3>🔌 Backend Status</h3>
        <div v-if="systemStore.healthLoading" class="status-loading">
          <span>⏳ Checking backend connection...</span>
        </div>
        <div v-else-if="systemStore.healthError" class="status-error">
          <span>❌ Backend Error: {{ systemStore.healthError }}</span>
          <button @click="systemStore.fetchHealth()" class="retry-btn">🔄 Retry</button>
        </div>
        <div v-else-if="systemStore.isHealthy" class="status-success">
          <span>✅ Backend Connected: {{ systemStore.healthStatus }}</span>
          <div class="backend-details">
            <p><strong>Database:</strong> {{ systemStore.databaseInfo }}</p>
            <p><strong>Total Users:</strong> {{ systemStore.totalUsers }}</p>
            <p><small>Last checked: {{ formatTime(systemStore.lastChecked) }}</small></p>
          </div>
        </div>
        <div v-else class="status-unknown">
          <span>❓ Backend Status Unknown</span>
          <button @click="systemStore.fetchHealth()" class="retry-btn">🔄 Check</button>
        </div>
      </div>

      <!-- App Features -->
      <div class="features-card">
        <h3>🚀 Features</h3>
        <ul>
          <li>JWT-based authentication (Coming Soon)</li>
          <li>Role-based access control</li>
          <li>Secure REST API</li>
          <li>Modern Vue.js frontend</li>
          <li>Real-time backend integration</li>
        </ul>
      </div>

      <!-- Quick Actions -->
      <div class="actions-card">
        <h3>⚡ Quick Actions</h3>
        <div class="action-buttons">
          <router-link to="/login">
            <button class="primary-btn">🔐 Login</button>
          </router-link>
          <router-link to="/register">
            <button class="secondary-btn">📝 Register</button>
          </router-link>
          <button @click="refreshStatus" class="refresh-btn" :disabled="systemStore.healthLoading">
            {{ systemStore.healthLoading ? '⏳' : '🔄' }} Refresh Status
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { useSystemStore } from '../stores/systemStore.js'
import { onMounted } from 'vue'

export default {
  name: 'Home',
  setup() {
    const systemStore = useSystemStore()

    // Initialize system status on component mount
    onMounted(async () => {
      await systemStore.initialize()
    })

    // Format timestamp for display
    const formatTime = (date) => {
      if (!date) return 'Never'
      return new Date(date).toLocaleTimeString()
    }

    // Refresh system status
    const refreshStatus = async () => {
      await systemStore.fetchHealth()
    }

    return {
      systemStore,
      formatTime,
      refreshStatus
    }
  }
}
</script>

<style scoped>
.system-status-card, .features-card, .actions-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 1rem 0;
}

.system-status-card h3, .features-card h3, .actions-card h3 {
  margin-top: 0;
  color: #495057;
}

.status-loading, .status-error, .status-success, .status-unknown {
  padding: 0.75rem;
  border-radius: 6px;
  margin: 0.5rem 0;
}

.status-loading {
  background: #fff3cd;
  color: #856404;
}

.status-error {
  background: #f8d7da;
  color: #721c24;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-success {
  background: #d1e7dd;
  color: #0f5132;
}

.status-unknown {
  background: #e2e3e5;
  color: #41464b;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.backend-details {
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.backend-details p {
  margin: 0.25rem 0;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.primary-btn, .secondary-btn, .refresh-btn, .retry-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.primary-btn {
  background: #007bff;
  color: white;
}

.primary-btn:hover {
  background: #0056b3;
}

.secondary-btn {
  background: #6c757d;
  color: white;
}

.secondary-btn:hover {
  background: #545b62;
}

.refresh-btn, .retry-btn {
  background: #28a745;
  color: white;
  font-size: 0.8rem;
}

.refresh-btn:hover, .retry-btn:hover {
  background: #1e7e34;
}

.refresh-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.features-card ul {
  margin: 0.5rem 0;
}

.features-card li {
  margin: 0.5rem 0;
}
</style>
