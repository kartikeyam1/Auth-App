# 🔐 Secure Web Application

A modern, secure web application built with Vue.js 3 and Spring Boot, featuring JWT authentication and role-based access control (RBAC).

## 🚀 Live Demo

🌐 **Frontend Demo**: [https://kartikeya.github.io/Auth-App/](https://kartikeya.github.io/Auth-App/) *(Auto-deployed from main branch)*

## 📋 Project Overview

This is a full-stack secure web application that demonstrates modern authentication patterns and best practices for building production-ready applications.

### 🛠️ Technology Stack

**Frontend:**
- ⚡ Vue.js 3 with Composition API
- 🎨 Modern CSS with Responsive Design
- 🚦 Vue Router for Navigation
- 🗂️ Pinia for State Management
- 📡 Axios for HTTP Requests
- ⚡ Vite for Development & Build

**Backend:** *(Coming in Part 2)*
- ☕ Spring Boot 3.x with Java 21
- 🔒 Spring Security 6.x with JWT
- 🗄️ H2 File-based Database
- 🔐 BCrypt Password Encryption

**DevOps:**
- 🤖 GitHub Actions CI/CD
- 📦 Automated Testing & Building
- 🌐 GitHub Pages Deployment

### ✨ Features

#### Current (Part 1 - Frontend Foundation)
- ✅ **Modern UI/UX** - Professional gradient design with responsive layout
- ✅ **Navigation System** - Vue Router with 5 main routes
- ✅ **Form Components** - Login and Registration forms with validation
- ✅ **Dashboard Views** - Separate User and Admin dashboards
- ✅ **Component Architecture** - Reusable Vue.js components
- ✅ **State Management** - Pinia setup ready for authentication
- ✅ **CI/CD Pipeline** - GitHub Actions for automated deployment

#### Planned Features
- 🔄 **JWT Authentication** - Secure token-based authentication
- 🔒 **Role-Based Access** - User and Admin role management
- 🛡️ **Route Guards** - Protected routes based on authentication
- 📊 **RESTful API** - Spring Boot backend with secure endpoints
- 🔐 **Password Security** - BCrypt hashing and validation
- 🧪 **Testing Suite** - Unit and integration tests
- 📱 **Responsive Design** - Mobile-first approach

## 🏗️ Project Structure

```
Auth-App/
├── 📁 frontend/                 # Vue.js Application
│   ├── 📁 src/
│   │   ├── 📁 components/       # Reusable Vue Components
│   │   │   └── NavBar.vue
│   │   ├── 📁 views/           # Page Components
│   │   │   ├── Home.vue
│   │   │   ├── Login.vue
│   │   │   ├── Register.vue
│   │   │   ├── UserDashboard.vue
│   │   │   └── AdminDashboard.vue
│   │   ├── 📁 router/          # Vue Router Configuration
│   │   │   └── index.js
│   │   ├── App.vue             # Root Component
│   │   ├── main.js             # Application Entry Point
│   │   └── style.css           # Global Styles
│   ├── package.json
│   └── vite.config.js
├── 📁 backend/                  # Spring Boot API (Coming in Part 2)
├── 📁 .github/workflows/        # GitHub Actions CI/CD
│   └── frontend-ci.yml
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Git
- Modern web browser

### Frontend Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Auth-App.git
   cd Auth-App
   ```

2. **Install frontend dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Open in browser**
   ```
   http://localhost:5173
   ```

### Available Routes

| Route | Description | Status |
|-------|-------------|--------|
| `/` | Home page with welcome message | ✅ Complete |
| `/login` | User login form | ✅ UI Ready |
| `/register` | User registration form | ✅ UI Ready |
| `/user-dashboard` | User-specific dashboard | ✅ UI Ready |
| `/admin-dashboard` | Admin control panel | ✅ UI Ready |

## 🔧 Development Commands

```bash
# Frontend commands (run from /frontend directory)
npm run dev      # Start development server
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run linting (configured in later parts)
npm run test     # Run tests (configured in later parts)
```

## 🚀 Deployment

### Automatic Deployment
The application is automatically deployed to GitHub Pages when code is pushed to the `main` branch.

**GitHub Actions Workflow:**
- ✅ Runs tests and linting
- ✅ Builds the application
- ✅ Deploys to GitHub Pages
- ✅ Available at: `https://your-username.github.io/Auth-App/`

### Manual Deployment
```bash
cd frontend
npm run build
# Deploy the dist/ folder to your hosting provider
```

## 📖 Development Progress

### ✅ Part 1: Frontend Foundation (Complete)
- [x] Vue.js 3 project setup with Vite
- [x] Install dependencies (Vue Router, Pinia, Axios)
- [x] Create basic page components
- [x] Set up routing system
- [x] Design modern UI with responsive layout
- [x] GitHub Actions CI/CD pipeline

### 🔄 Part 2: Backend Foundation (Next)
- [ ] Spring Boot project setup
- [ ] H2 database configuration
- [ ] Basic entity and repository setup
- [ ] REST API endpoints

### 🔄 Upcoming Parts
- [ ] **Part 3**: JWT Authentication (Backend)
- [ ] **Part 4**: Protected Endpoints (Backend)
- [ ] **Part 5**: Frontend Authentication Integration
- [ ] **Part 6**: Route Protection (Frontend)
- [ ] **Part 7**: Testing & Error Handling
- [ ] **Part 8**: Production Readiness

## 🎨 Design Features

- **🎨 Modern UI**: Professional gradient design with blue-purple theme
- **📱 Responsive**: Mobile-first design that works on all devices
- **⚡ Fast**: Vite-powered development with hot module replacement
- **🧩 Component-based**: Modular Vue.js architecture
- **🎯 User-friendly**: Intuitive navigation and form interactions
- **🛡️ Secure**: Ready for JWT integration and role-based access

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact

For questions or support, please open an issue in this repository.

---

**Built with ❤️ using Vue.js and Spring Boot**
