// User Store - Manages user data and operations
import { defineStore } from 'pinia'
import { userService, apiUtils } from '../services/apiService.js'

export const useUserStore = defineStore('user', {
  state: () => ({
    // Users list
    users: [],
    usersLoading: false,
    usersError: null,
    
    // Selected user for viewing/editing
    selectedUser: null,
    selectedUserLoading: false,
    selectedUserError: null,
    
    // Operation status
    operationLoading: false,
    operationError: null,
    operationSuccess: null
  }),

  getters: {
    // Get users count
    usersCount: (state) => state.users.length,
    
    // Get users by role
    usersByRole: (state) => (role) => {
      return state.users.filter(user => 
        user.roles && user.roles.includes(role)
      )
    },
    
    // Get admin users
    adminUsers: (state) => {
      return state.users.filter(user => 
        user.roles && user.roles.includes('ROLE_ADMIN')
      )
    },
    
    // Get regular users
    regularUsers: (state) => {
      return state.users.filter(user => 
        user.roles && user.roles.includes('ROLE_USER') && 
        (!user.roles.includes('ROLE_ADMIN'))
      )
    }
  },

  actions: {
    // Fetch all users
    async fetchUsers() {
      this.usersLoading = true
      this.usersError = null
      
      try {
        this.users = await userService.getAllUsers()
        
        console.log('👥 Users loaded:', this.users.length)
      } catch (error) {
        this.usersError = apiUtils.getErrorMessage(error)
        
        console.error('👥 Users fetch failed:', error)
      } finally {
        this.usersLoading = false
      }
    },

    // Fetch user by ID
    async fetchUser(id) {
      this.selectedUserLoading = true
      this.selectedUserError = null
      
      try {
        this.selectedUser = await userService.getUserById(id)
        
        console.log('👤 User loaded:', this.selectedUser.email)
      } catch (error) {
        this.selectedUserError = apiUtils.getErrorMessage(error)
        
        console.error('👤 User fetch failed:', error)
      } finally {
        this.selectedUserLoading = false
      }
    },

    // Fetch user by email
    async fetchUserByEmail(email) {
      this.selectedUserLoading = true
      this.selectedUserError = null
      
      try {
        this.selectedUser = await userService.getUserByEmail(email)
        
        console.log('👤 User loaded by email:', this.selectedUser.email)
      } catch (error) {
        this.selectedUserError = apiUtils.getErrorMessage(error)
        
        console.error('👤 User fetch by email failed:', error)
      } finally {
        this.selectedUserLoading = false
      }
    },

    // Create new user
    async createUser(userData) {
      this.operationLoading = true
      this.operationError = null
      this.operationSuccess = null
      
      try {
        const newUser = await userService.createUser(userData)
        
        // Add to local users list
        this.users.push(newUser)
        
        this.operationSuccess = `User ${newUser.email} created successfully`
        
        console.log('✅ User created:', newUser.email)
        return newUser
      } catch (error) {
        this.operationError = apiUtils.getErrorMessage(error)
        
        console.error('❌ User creation failed:', error)
        throw error
      } finally {
        this.operationLoading = false
      }
    },

    // Update existing user
    async updateUser(id, userData) {
      this.operationLoading = true
      this.operationError = null
      this.operationSuccess = null
      
      try {
        const updatedUser = await userService.updateUser(id, userData)
        
        // Update in local users list
        const index = this.users.findIndex(user => user.id === id)
        if (index !== -1) {
          this.users[index] = updatedUser
        }
        
        // Update selected user if it's the same
        if (this.selectedUser && this.selectedUser.id === id) {
          this.selectedUser = updatedUser
        }
        
        this.operationSuccess = `User ${updatedUser.email} updated successfully`
        
        console.log('✅ User updated:', updatedUser.email)
        return updatedUser
      } catch (error) {
        this.operationError = apiUtils.getErrorMessage(error)
        
        console.error('❌ User update failed:', error)
        throw error
      } finally {
        this.operationLoading = false
      }
    },

    // Delete user
    async deleteUser(id) {
      this.operationLoading = true
      this.operationError = null
      this.operationSuccess = null
      
      try {
        await userService.deleteUser(id)
        
        // Remove from local users list
        this.users = this.users.filter(user => user.id !== id)
        
        // Clear selected user if it's the deleted one
        if (this.selectedUser && this.selectedUser.id === id) {
          this.selectedUser = null
        }
        
        this.operationSuccess = 'User deleted successfully'
        
        console.log('✅ User deleted:', id)
      } catch (error) {
        this.operationError = apiUtils.getErrorMessage(error)
        
        console.error('❌ User deletion failed:', error)
        throw error
      } finally {
        this.operationLoading = false
      }
    },

    // Clear operation messages
    clearMessages() {
      this.operationError = null
      this.operationSuccess = null
    },

    // Clear selected user
    clearSelectedUser() {
      this.selectedUser = null
      this.selectedUserError = null
    },

    // Reset all state
    reset() {
      this.users = []
      this.usersLoading = false
      this.usersError = null
      this.selectedUser = null
      this.selectedUserLoading = false
      this.selectedUserError = null
      this.operationLoading = false
      this.operationError = null
      this.operationSuccess = null
    }
  }
})
