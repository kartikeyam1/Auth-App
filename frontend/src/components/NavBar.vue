<template>
  <nav>
    <div class="container">
      <router-link to="/">Home</router-link>
      <router-link to="/login">Login</router-link>
      <router-link to="/register">Register</router-link>
      <router-link to="/user-dashboard">User Dashboard</router-link>
      <router-link to="/admin-dashboard">Admin Dashboard</router-link>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'NavBar'
}
</script>
