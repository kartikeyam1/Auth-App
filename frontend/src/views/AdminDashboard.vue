<template>
  <main>
    <div class="page-content">
      <h1>Admin Dashboard</h1>
      <p>Welcome to the admin control panel! Manage users and monitor system status.</p>
      
      <!-- System Status Overview -->
      <div class="dashboard-section">
        <h2>🔌 System Status</h2>
        <div v-if="systemStore.healthLoading" class="loading">
          <span>⏳ Loading system status...</span>
        </div>
        <div v-else-if="systemStore.healthError" class="error">
          <span>❌ System Error: {{ systemStore.healthError }}</span>
          <button @click="systemStore.fetchHealth()" class="retry-btn">🔄 Retry</button>
        </div>
        <div v-else class="stats-grid">
          <div class="stat-card" :class="{ 'healthy': systemStore.isHealthy, 'unhealthy': !systemStore.isHealthy }">
            <h3>Backend Status</h3>
            <p class="stat-number">{{ systemStore.healthStatus }}</p>
            <small>{{ systemStore.databaseInfo }}</small>
          </div>
          <div class="stat-card">
            <h3>Total Users</h3>
            <p class="stat-number">{{ systemStore.totalUsers }}</p>
            <small>Registered accounts</small>
          </div>
          <div class="stat-card">
            <h3>Admin Users</h3>
            <p class="stat-number">{{ userStore.adminUsers.length }}</p>
            <small>Administrator accounts</small>
          </div>
          <div class="stat-card">
            <h3>Regular Users</h3>
            <p class="stat-number">{{ userStore.regularUsers.length }}</p>
            <small>Standard accounts</small>
          </div>
        </div>
      </div>

      <!-- User Management -->
      <div class="dashboard-section">
        <div class="section-header">
          <h2>👥 User Management</h2>
          <div class="action-buttons">
            <button @click="loadAllData" class="refresh-btn" :disabled="isLoading">
              {{ isLoading ? '⏳' : '🔄' }} Refresh Data
            </button>
            <button @click="showCreateUser = true" class="create-btn">
              ➕ Create User
            </button>
          </div>
        </div>

        <!-- Quick Stats -->
        <div class="user-stats" v-if="userStore.users.length > 0">
          <div class="quick-stat">
            <span class="stat-label">Total:</span>
            <span class="stat-value">{{ userStore.usersCount }}</span>
          </div>
          <div class="quick-stat">
            <span class="stat-label">Enabled:</span>
            <span class="stat-value">{{ enabledUsersCount }}</span>
          </div>
          <div class="quick-stat">
            <span class="stat-label">Disabled:</span>
            <span class="stat-value">{{ disabledUsersCount }}</span>
          </div>
        </div>

        <!-- Users Table -->
        <div class="users-table-container">
          <div v-if="userStore.usersLoading" class="loading">
            <span>⏳ Loading users...</span>
          </div>
          <div v-else-if="userStore.usersError" class="error">
            <span>❌ Error: {{ userStore.usersError }}</span>
            <button @click="userStore.fetchUsers()" class="retry-btn">🔄 Retry</button>
          </div>
          <div v-else-if="userStore.users.length === 0" class="no-data">
            <span>📭 No users found</span>
          </div>
          <table v-else class="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Email</th>
                <th>Roles</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in userStore.users" :key="user.id" :class="{ 'admin-row': isAdmin(user) }">
                <td>{{ user.id }}</td>
                <td class="email-cell">{{ user.email }}</td>
                <td class="roles-cell">
                  <span 
                    v-for="role in user.roles" 
                    :key="role" 
                    class="role-badge"
                    :class="{ 'admin-role': role === 'ROLE_ADMIN' }"
                  >
                    {{ formatRole(role) }}
                  </span>
                </td>
                <td class="status-cell">
                  <span :class="{ 'status-enabled': user.enabled, 'status-disabled': !user.enabled }">
                    {{ user.enabled ? '✅ Enabled' : '❌ Disabled' }}
                  </span>
                </td>
                <td class="date-cell">{{ formatDate(user.createdAt) }}</td>
                <td class="actions-cell">
                  <button @click="viewUser(user)" class="view-btn">👁️</button>
                  <button @click="editUser(user)" class="edit-btn">✏️</button>
                  <button @click="deleteUser(user)" class="delete-btn" :disabled="userStore.operationLoading">
                    🗑️
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Create User Modal -->
      <div v-if="showCreateUser" class="modal-overlay" @click="closeCreateUser">
        <div class="modal-content" @click.stop>
          <h3>➕ Create New User</h3>
          <form @submit.prevent="handleCreateUser">
            <div class="form-group">
              <label for="email">Email:</label>
              <input 
                type="email" 
                id="email" 
                v-model="newUser.email" 
                required 
                placeholder="user@example.com"
              >
            </div>
            <div class="form-group">
              <label for="password">Password:</label>
              <input 
                type="password" 
                id="password" 
                v-model="newUser.password" 
                required 
                placeholder="Minimum 6 characters"
                minlength="6"
              >
            </div>
            <div class="form-group">
              <label>
                <input type="checkbox" v-model="newUser.isAdmin">
                Grant Admin Role
              </label>
            </div>
            <div class="form-actions">
              <button type="button" @click="closeCreateUser" class="cancel-btn">Cancel</button>
              <button type="submit" class="submit-btn" :disabled="userStore.operationLoading">
                {{ userStore.operationLoading ? '⏳ Creating...' : '✅ Create User' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Operation Messages -->
      <div v-if="userStore.operationSuccess" class="success-message">
        ✅ {{ userStore.operationSuccess }}
        <button @click="userStore.clearMessages()" class="close-msg">✖️</button>
      </div>
      <div v-if="userStore.operationError" class="error-message">
        ❌ {{ userStore.operationError }}
        <button @click="userStore.clearMessages()" class="close-msg">✖️</button>
      </div>
    </div>
  </main>
</template>

<script>
import { useSystemStore } from '../stores/systemStore.js'
import { useUserStore } from '../stores/userStore.js'
import { onMounted, ref, computed } from 'vue'

export default {
  name: 'AdminDashboard',
  setup() {
    const systemStore = useSystemStore()
    const userStore = useUserStore()
    
    // Create user modal
    const showCreateUser = ref(false)
    const newUser = ref({
      email: '',
      password: '',
      isAdmin: false
    })

    // Load all data on mount
    onMounted(async () => {
      await loadAllData()
    })

    // Computed properties
    const isLoading = computed(() => 
      systemStore.healthLoading || userStore.usersLoading
    )

    const enabledUsersCount = computed(() => 
      userStore.users.filter(user => user.enabled).length
    )

    const disabledUsersCount = computed(() => 
      userStore.users.filter(user => !user.enabled).length
    )

    // Load all system and user data
    const loadAllData = async () => {
      await Promise.all([
        systemStore.initialize(),
        userStore.fetchUsers()
      ])
    }

    // Check if user is admin
    const isAdmin = (user) => {
      return user.roles && user.roles.includes('ROLE_ADMIN')
    }

    // Format role for display
    const formatRole = (role) => {
      return role.replace('ROLE_', '').toLowerCase()
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ')
    }

    // Format date for display
    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString()
    }

    // View user details
    const viewUser = async (user) => {
      await userStore.fetchUser(user.id)
    }

    // Edit user (placeholder for now)
    const editUser = (user) => {
      console.log('Edit user:', user.email)
      // TODO: Implement edit user modal
    }

    // Delete user with confirmation
    const deleteUser = async (user) => {
      if (confirm(`Are you sure you want to delete user ${user.email}?`)) {
        try {
          await userStore.deleteUser(user.id)
        } catch (error) {
          console.error('Delete failed:', error)
        }
      }
    }

    // Handle create user form submission
    const handleCreateUser = async () => {
      try {
        const userData = {
          email: newUser.value.email,
          password: newUser.value.password
        }

        const createdUser = await userStore.createUser(userData)
        
        // Add admin role if selected
        if (newUser.value.isAdmin) {
          // TODO: Implement role assignment API
          console.log('Admin role should be assigned to:', createdUser.email)
        }

        closeCreateUser()
      } catch (error) {
        console.error('User creation failed:', error)
      }
    }

    // Close create user modal
    const closeCreateUser = () => {
      showCreateUser.value = false
      newUser.value = {
        email: '',
        password: '',
        isAdmin: false
      }
      userStore.clearMessages()
    }

    return {
      systemStore,
      userStore,
      showCreateUser,
      newUser,
      isLoading,
      enabledUsersCount,
      disabledUsersCount,
      loadAllData,
      isAdmin,
      formatRole,
      formatDate,
      viewUser,
      editUser,
      deleteUser,
      handleCreateUser,
      closeCreateUser
    }
  }
}
</script>

<style scoped>
.dashboard-section {
  background: #f8f9fa;
  padding: 1.5rem;
  margin: 1rem 0;
  border-radius: 8px;
  border-left: 4px solid #dc3545;
}

.dashboard-section h2 {
  margin-bottom: 1rem;
  color: #495057;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.loading, .error, .no-data {
  padding: 1rem;
  border-radius: 6px;
  margin: 0.5rem 0;
  text-align: center;
}

.loading {
  background: #fff3cd;
  color: #856404;
}

.error {
  background: #f8d7da;
  color: #721c24;
}

.no-data {
  background: #e2e3e5;
  color: #41464b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.stat-card {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border: 2px solid transparent;
}

.stat-card.healthy {
  border-color: #28a745;
}

.stat-card.unhealthy {
  border-color: #dc3545;
}

.stat-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #6c757d;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #007bff;
  margin: 0;
}

.stat-card small {
  color: #6c757d;
  font-size: 0.8rem;
}

.user-stats {
  display: flex;
  gap: 1.5rem;
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
}

.quick-stat {
  display: flex;
  flex-direction: column;
  text-align: center;
}

.stat-label {
  font-size: 0.8rem;
  color: #6c757d;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #007bff;
}

.users-table-container {
  background: white;
  border-radius: 6px;
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #dee2e6;
}

.users-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
}

.users-table tr:hover {
  background: #f8f9fa;
}

.admin-row {
  background: #fff5f5;
}

.email-cell {
  font-weight: 500;
}

.roles-cell {
  white-space: nowrap;
}

.role-badge {
  background: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  margin-right: 0.25rem;
}

.role-badge.admin-role {
  background: #dc3545;
  color: white;
}

.status-enabled {
  color: #28a745;
}

.status-disabled {
  color: #dc3545;
}

.date-cell {
  font-size: 0.9rem;
  color: #6c757d;
}

.actions-cell {
  white-space: nowrap;
}

.view-btn, .edit-btn, .delete-btn {
  background: none;
  border: none;
  padding: 0.25rem 0.5rem;
  margin: 0 0.125rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.view-btn {
  background: #007bff;
  color: white;
}

.edit-btn {
  background: #ffc107;
  color: #212529;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.view-btn:hover, .edit-btn:hover, .delete-btn:hover {
  opacity: 0.8;
}

.refresh-btn, .create-btn, .retry-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.refresh-btn, .retry-btn {
  background: #28a745;
  color: white;
}

.create-btn {
  background: #007bff;
  color: white;
}

.refresh-btn:hover, .create-btn:hover, .retry-btn:hover {
  opacity: 0.8;
}

.refresh-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  min-width: 400px;
  max-width: 90vw;
}

.modal-content h3 {
  margin-top: 0;
  color: #495057;
}

.form-group {
  margin: 1rem 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #495057;
}

.form-group input[type="email"],
.form-group input[type="password"] {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input[type="checkbox"] {
  margin-right: 0.5rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.cancel-btn, .submit-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.submit-btn {
  background: #28a745;
  color: white;
}

.cancel-btn:hover, .submit-btn:hover {
  opacity: 0.8;
}

.submit-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

/* Message Styles */
.success-message, .error-message {
  position: fixed;
  top: 2rem;
  right: 2rem;
  padding: 1rem 1.5rem;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 1rem;
  z-index: 1001;
  min-width: 300px;
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

.close-msg {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  opacity: 0.7;
}

.close-msg:hover {
  opacity: 1;
}
</style>