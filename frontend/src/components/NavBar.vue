<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/login">Login</router-link> |
    <router-link to="/register">Register</router-link> |
    <router-link to="/user-dashboard">User Dashboard</router-link> |
    <router-link to="/admin-dashboard">Admin Dashboard</router-link>
  </nav>
</template>

<script>
export default {
  name: 'NavBar'
}
</script>

<style scoped>
nav {
  background-color: #f8f9fa;
  padding: 1rem;
}
a {
  margin-right: 1rem;
  text-decoration: none;
  color: #007bff;
}
a:hover {
  text-decoration: underline;
}
</style>
