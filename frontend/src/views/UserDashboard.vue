<template>
  <main>
    <div class="page-content">
      <h1>User Dashboard</h1>
      <p>Welcome to your user dashboard!</p>
      <div class="dashboard-section">
        <h2>Your Profile</h2>
        <p>Email: user@example.com</p>
        <p>Role: User</p>
        <p>Last Login: Today</p>
      </div>
      <div class="dashboard-section">
        <h2>Quick Actions</h2>
        <button class="mb-2">Update Profile</button>
        <button class="mb-2">Change Password</button>
        <button class="mb-2">View Activity</button>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'UserDashboard'
}
</script>

<style scoped>
.dashboard-section {
  background: #f8f9fa;
  padding: 1.5rem;
  margin: 1rem 0;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.dashboard-section h2 {
  margin-bottom: 1rem;
  color: #495057;
}

button {
  display: block;
  width: 200px;
}
</style>
