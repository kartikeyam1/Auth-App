<template>
  <main>
    <div class="page-content">
      <h1>User Dashboard</h1>
      <p>Welcome to your user dashboard. Here you can view system users and their information.</p>
      
      <!-- Users Overview -->
      <div class="users-overview">
        <h3>👥 Users Overview</h3>
        <div v-if="userStore.usersLoading" class="loading">
          <span>⏳ Loading users...</span>
        </div>
        <div v-else-if="userStore.usersError" class="error">
          <span>❌ Error: {{ userStore.usersError }}</span>
          <button @click="loadUsers" class="retry-btn">🔄 Retry</button>
        </div>
        <div v-else class="users-stats">
          <div class="stat-card">
            <h4>Total Users</h4>
            <span class="stat-number">{{ userStore.usersCount }}</span>
          </div>
          <div class="stat-card">
            <h4>Admin Users</h4>
            <span class="stat-number">{{ userStore.adminUsers.length }}</span>
          </div>
          <div class="stat-card">
            <h4>Regular Users</h4>
            <span class="stat-number">{{ userStore.regularUsers.length }}</span>
          </div>
        </div>
      </div>

      <!-- Users List -->
      <div class="users-list-section">
        <div class="section-header">
          <h3>📋 Users List</h3>
          <button @click="loadUsers" class="refresh-btn" :disabled="userStore.usersLoading">
            {{ userStore.usersLoading ? '⏳' : '🔄' }} Refresh
          </button>
        </div>
        
        <div v-if="userStore.users.length === 0 && !userStore.usersLoading" class="no-users">
          <p>No users found.</p>
        </div>
        
        <div v-else class="users-grid">
          <div 
            v-for="user in userStore.users" 
            :key="user.id" 
            class="user-card"
            :class="{ 'admin-user': isAdmin(user) }"
          >
            <div class="user-header">
              <h4>{{ user.email }}</h4>
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
            
            <div class="user-details">
              <p><strong>ID:</strong> {{ user.id }}</p>
              <p><strong>Created:</strong> {{ formatDate(user.createdAt) }}</p>
              <p><strong>Updated:</strong> {{ formatDate(user.updatedAt) }}</p>
              <p><strong>Status:</strong> 
                <span :class="{ 'status-enabled': user.enabled, 'status-disabled': !user.enabled }">
                  {{ user.enabled ? '✅ Enabled' : '❌ Disabled' }}
                </span>
              </p>
            </div>
            
            <div class="user-actions">
              <button @click="viewUser(user)" class="view-btn">👁️ View</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Selected User Details -->
      <div v-if="userStore.selectedUser" class="user-details-section">
        <h3>👤 User Details</h3>
        <div class="selected-user-card">
          <div class="user-info">
            <h4>{{ userStore.selectedUser.email }}</h4>
            <div class="detail-grid">
              <div><strong>ID:</strong> {{ userStore.selectedUser.id }}</div>
              <div><strong>Email:</strong> {{ userStore.selectedUser.email }}</div>
              <div><strong>Enabled:</strong> {{ userStore.selectedUser.enabled ? 'Yes' : 'No' }}</div>
              <div><strong>Account Non Expired:</strong> {{ userStore.selectedUser.accountNonExpired ? 'Yes' : 'No' }}</div>
              <div><strong>Account Non Locked:</strong> {{ userStore.selectedUser.accountNonLocked ? 'Yes' : 'No' }}</div>
              <div><strong>Credentials Non Expired:</strong> {{ userStore.selectedUser.credentialsNonExpired ? 'Yes' : 'No' }}</div>
              <div><strong>Created:</strong> {{ formatDateTime(userStore.selectedUser.createdAt) }}</div>
              <div><strong>Updated:</strong> {{ formatDateTime(userStore.selectedUser.updatedAt) }}</div>
            </div>
            <div class="user-roles-section">
              <strong>Roles:</strong>
              <div class="roles-list">
                <span 
                  v-for="role in userStore.selectedUser.roles" 
                  :key="role" 
                  class="role-badge"
                  :class="{ 'admin-role': role === 'ROLE_ADMIN' }"
                >
                  {{ formatRole(role) }}
                </span>
              </div>
            </div>
          </div>
          <button @click="clearSelectedUser" class="close-btn">✖️ Close</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { useUserStore } from '../stores/userStore.js'
import { onMounted } from 'vue'

export default {
  name: 'UserDashboard',
  setup() {
    const userStore = useUserStore()

    // Load users on component mount
    onMounted(async () => {
      await loadUsers()
    })

    // Load users function
    const loadUsers = async () => {
      await userStore.fetchUsers()
    }

    // View user details
    const viewUser = async (user) => {
      await userStore.fetchUser(user.id)
    }

    // Clear selected user
    const clearSelectedUser = () => {
      userStore.clearSelectedUser()
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

    // Format date and time for display
    const formatDateTime = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleString()
    }

    return {
      userStore,
      loadUsers,
      viewUser,
      clearSelectedUser,
      isAdmin,
      formatRole,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.users-overview {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 1rem 0;
}

.users-overview h3 {
  margin-top: 0;
  color: #495057;
}

.loading, .error {
  padding: 1rem;
  border-radius: 6px;
  margin: 0.5rem 0;
}

.loading {
  background: #fff3cd;
  color: #856404;
}

.error {
  background: #f8d7da;
  color: #721c24;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.users-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.stat-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 1rem;
  text-align: center;
}

.stat-card h4 {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #6c757d;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #007bff;
}

.users-list-section {
  margin: 2rem 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h3 {
  margin: 0;
  color: #495057;
}

.refresh-btn, .retry-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.refresh-btn:hover, .retry-btn:hover {
  background: #1e7e34;
}

.refresh-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.no-users {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
}

.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1rem;
}

.user-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 1rem;
  transition: box-shadow 0.2s;
}

.user-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.user-card.admin-user {
  border-left: 4px solid #dc3545;
}

.user-header h4 {
  margin: 0 0 0.5rem 0;
  color: #495057;
}

.user-roles {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.role-badge {
  background: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.role-badge.admin-role {
  background: #dc3545;
  color: white;
}

.user-details {
  margin: 1rem 0;
  font-size: 0.9rem;
}

.user-details p {
  margin: 0.25rem 0;
}

.status-enabled {
  color: #28a745;
}

.status-disabled {
  color: #dc3545;
}

.user-actions {
  display: flex;
  gap: 0.5rem;
}

.view-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.view-btn:hover {
  background: #0056b3;
}

.user-details-section {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 2rem 0;
}

.user-details-section h3 {
  margin-top: 0;
  color: #495057;
}

.selected-user-card {
  background: white;
  border-radius: 6px;
  padding: 1.5rem;
  position: relative;
}

.user-info h4 {
  margin-top: 0;
  color: #495057;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 0.5rem;
  margin: 1rem 0;
  font-size: 0.9rem;
}

.user-roles-section {
  margin: 1rem 0;
}

.roles-list {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 0.5rem;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.close-btn:hover {
  background: #c82333;
}
</style>