<template>
  <main>
    <div class="page-content">
      <h1>Admin Dashboard</h1>
      <p>Welcome to the admin control panel!</p>
      
      <div class="dashboard-section">
        <h2>System Overview</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <h3>Total Users</h3>
            <p class="stat-number">150</p>
          </div>
          <div class="stat-card">
            <h3>Active Sessions</h3>
            <p class="stat-number">42</p>
          </div>
          <div class="stat-card">
            <h3>System Health</h3>
            <p class="stat-number">98%</p>
          </div>
        </div>
      </div>

      <div class="dashboard-section">
        <h2>Admin Actions</h2>
        <div class="action-buttons">
          <button class="mb-2">Manage Users</button>
          <button class="mb-2">View Logs</button>
          <button class="mb-2">System Settings</button>
          <button class="mb-2">Backup Data</button>
        </div>
      </div>

      <div class="dashboard-section">
        <h2>Recent Activity</h2>
        <ul class="activity-list">
          <li>User john@example.com registered - 2 minutes ago</li>
          <li>System backup completed - 1 hour ago</li>
          <li>Security scan completed - 3 hours ago</li>
        </ul>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'AdminDashboard'
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
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  margin: 0;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.activity-list {
  list-style: none;
  padding: 0;
}

.activity-list li {
  padding: 0.5rem;
  border-bottom: 1px solid #dee2e6;
  color: #666;
}

.activity-list li:last-child {
  border-bottom: none;
}
</style>
