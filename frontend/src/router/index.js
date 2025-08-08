import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserDashboard from '../views/UserDashboard.vue'
import AdminDashboard from '../views/AdminDashboard.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/user-dashboard', name: 'UserDashboard', component: UserDashboard },
  { path: '/admin-dashboard', name: 'AdminDashboard', component: AdminDashboard }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
