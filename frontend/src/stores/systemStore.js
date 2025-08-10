// System Store - Manages backend health, stats, and system information
import { defineStore } from 'pinia'
import { healthService, apiUtils } from '../services/apiService.js'

export const useSystemStore = defineStore('system', {
  state: () => ({
    // Health status
    health: null,
    healthLoading: false,
    healthError: null,
    
    // Database statistics
    stats: null,
    statsLoading: false,
    statsError: null,
    
    // Backend connection status
    isConnected: false,
    lastChecked: null
  }),

  getters: {
    // Check if backend is healthy
    isHealthy: (state) => {
      return state.health?.status === 'UP'
    },
    
    // Get formatted health status
    healthStatus: (state) => {
      if (state.healthLoading) return 'Checking...'
      if (state.healthError) return 'Error'
      if (state.health?.status === 'UP') return 'Healthy'
      return 'Unknown'
    },
    
    // Get total users count
    totalUsers: (state) => {
      return state.health?.totalUsers || state.stats?.totalUsers || 0
    },
    
    // Get database info
    databaseInfo: (state) => {
      return state.health?.database || 'Unknown'
    }
  },

  actions: {
    // Fetch backend health status
    async fetchHealth() {
      this.healthLoading = true
      this.healthError = null
      
      try {
        this.health = await healthService.getHealth()
        this.isConnected = true
        this.lastChecked = new Date()
        
        console.log('🏥 Backend health:', this.health)
      } catch (error) {
        this.healthError = apiUtils.getErrorMessage(error)
        this.isConnected = false
        
        console.error('🏥 Health check failed:', error)
      } finally {
        this.healthLoading = false
      }
    },

    // Fetch database statistics
    async fetchStats() {
      this.statsLoading = true
      this.statsError = null
      
      try {
        this.stats = await healthService.getStats()
        
        console.log('📊 Backend stats:', this.stats)
      } catch (error) {
        this.statsError = apiUtils.getErrorMessage(error)
        
        console.error('📊 Stats fetch failed:', error)
      } finally {
        this.statsLoading = false
      }
    },

    // Initialize system - fetch health and stats
    async initialize() {
      console.log('🚀 Initializing system store...')
      await Promise.all([
        this.fetchHealth(),
        this.fetchStats()
      ])
    },

    // Reset all state
    reset() {
      this.health = null
      this.healthLoading = false
      this.healthError = null
      this.stats = null
      this.statsLoading = false
      this.statsError = null
      this.isConnected = false
      this.lastChecked = null
    }
  }
})
