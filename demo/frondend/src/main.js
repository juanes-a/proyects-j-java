import './assets/main.css' // Importa tus estilos globales
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Crea la aplicación Vue
const app = createApp(App)

// Configura Pinia para state management
const pinia = createPinia()
app.use(pinia)

// Configura el router
app.use(router)

// Monta la aplicación
app.mount('#app')