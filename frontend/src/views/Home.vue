<template>
  <main>
    <div class="page-content">
      <h1>Welcome to the Secure Web App</h1>
      <p>This is a secure web application built with Vue.js 3 and Spring Boot.</p>
      <p>Features:</p>
      <ul>
        <li>JWT-based authentication</li>
        <li>Role-based access control</li>
        <li>Secure REST API</li>
        <li>Modern Vue.js frontend</li>
      </ul>
      <div class="text-center mt-2">
        <router-link to="/login">
          <button>Get Started</button>
        </router-link>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  name: 'Home'
}
</script>
