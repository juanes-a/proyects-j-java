<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">Iniciar sesión en ProyectS-J</h1>
      
      <form @submit.prevent="handleLogin" class="auth-form">
        <!-- Email Input -->
        <div class="input-group">
          <label for="usernameOrEmail">Usuario o correo electrónico</label>
            <input
              type="text"
              id="usernameOrEmail"
              v-model="loginForm.usernameOrEmail"
              required
              placeholder="Usuario o correo electrónico"
              autocomplete="username"
            />
        </div>
        
        <!-- Password Input -->
        <div class="input-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            placeholder="••••••••"
            autocomplete="current-password"
          />
        </div>
        
        <!-- Submit Button -->
        <button type="submit" class="auth-button" :disabled="loading">
          <span v-if="!loading">Iniciar sesión</span>
          <span v-else>Procesando...</span>
        </button>
        
        <!-- Form Footer -->
        <div class="auth-footer">
          <p>¿No tienes una cuenta? <router-link to="/register">Regístrate</router-link></p>
          <p><router-link to="/forgot-password">¿Olvidaste tu contraseña?</router-link></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import '../../assets/auth.css'; // Importar estilos específicos de autenticación
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Reactive form data
const loginForm = ref({
  usernameOrEmail: '',
  password: ''
});

// 👉 Esta línea faltaba en tu nueva versión
const loading = ref(false);

const handleLogin = async () => {
  loading.value = true;

  try {
    const response = await fetch('http://localhost:8081/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginForm.value)
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || 'Error al iniciar sesión');
    }

    // 👉 Guardar el token
    const token = data.token;
    localStorage.setItem('authToken', token);

    // 👉 Obtener info del usuario logueado (incluye el rol)
    const meResponse = await fetch('http://localhost:8081/api/auth/me', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    const me = await meResponse.json();

    if (!meResponse.ok) {
      throw new Error('No se pudo obtener el rol del usuario');
    }

    // 👉 Guardar información del usuario (opcional)
    localStorage.setItem('user', JSON.stringify(me));
    
    alert('✅ Login successful');

    // 👉 Redirigir según rol
    switch (me.role) {
      case 'ADMIN_GLOBAL':
        router.push('/homeDepartaments');
        break;
      case 'ADMIN_DEPT':
        router.push('/departmentHome');
        break;
      case 'COLLAB':
        router.push('/collab/home');
        break;
      default:
        router.push('/access-denied');
        break;
    }

  } catch (error) {
    console.error('Error en el login:', error.message);
    alert(error.message);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* Responsive adjustments */
@media (max-width: 480px) {
  .auth-card {
    padding: 30px 20px;
  }
  
  .auth-title {
    font-size: 20px;
  }
}
</style>