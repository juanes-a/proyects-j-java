<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">Registro en ProyectS-J</h1>
      
      <form @submit.prevent="handleRegister" class="auth-form">
        <!-- Full Name Input -->
        <div class="input-group">
          <label for="fullName">Nombre completo</label>
          <input
            type="text"
            id="fullName"
            v-model="registerForm.fullName"
            required
            placeholder="Juan Pérez"
            autocomplete="name"
          />
        </div>

        <div class="input-group">
          <label for="username">username</label>
          <input
            type="text"
            id="username"
            v-model="registerForm.username"
            required
            placeholder="juanes15"
            autocomplete="name"
          />
        </div>
        
        <!-- Email Input -->
        <div class="input-group">
          <label for="email">Correo electrónico</label>
          <input
            type="email"
            id="email"
            v-model="registerForm.email"
            required
            placeholder="tu@empresa.com"
            autocomplete="email"
          />
        </div>
        
        <!-- Password Input -->
        <div class="input-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="registerForm.password"
            required
            placeholder="••••••••"
            autocomplete="new-password"
          />
          <p class="input-hint">Mínimo 8 caracteres</p>
        </div>
        
        <!-- Confirm Password Input -->
        <div class="input-group">
          <label for="confirmPassword">Confirmar contraseña</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="registerForm.confirmPassword"
            required
            placeholder="••••••••"
            autocomplete="new-password"
          />
        </div>
        
        <!-- Submit Button -->
        <button type="submit" class="auth-button" :disabled="loading || !passwordsMatch">
          <span v-if="!loading">Crear cuenta</span>
          <span v-else>Procesando...</span>
        </button>
        
        <!-- Form Footer -->
        <div class="auth-footer">
          <p>¿Ya tienes una cuenta? <router-link to="/login">Inicia sesión</router-link></p>
        </div>
      </form>
       <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import '../../assets/auth.css';// Importar estilos específicos de autenticación
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Reactive form data
const registerForm = ref({
  fullName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: ''
});

const loading = ref(false);

// Computed property to check if passwords match
const passwordsMatch = computed(() => {
  return registerForm.value.password === registerForm.value.confirmPassword;
});
// Register handler
const handleRegister = async () => {
  if (!passwordsMatch.value) return;
  
  loading.value = true;
  
  try {
    const response = await fetch('http://localhost:8081/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: registerForm.value.fullName,
        email: registerForm.value.email,
        username: registerForm.value.username,
        password: registerForm.value.password
      })
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || 'Error al registrarse');
    }

    // Guardar el token
    localStorage.setItem('authToken', data.token);
    localStorage.setItem('user', JSON.stringify({
      name: data.name,
      username: data.username,
      email: data.email
    }));

    // Redirigir
    alert('✅ Registered successfully');

    router.push('/dashboard');

  } catch (error) {
    console.error('Error en el registro:', error.message);
    alert(error.message);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* Los estilos principales ya están definidos en LoginView.vue */
/* Agregamos solo estilos específicos para Register */

.input-hint {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
}

/* Estilo para mostrar cuando las contraseñas no coinciden */
.auth-button:disabled:not([loading]) {
  background-color: #e53e3e;
  position: relative;
}

.auth-button:disabled:not([loading])::after {
  content: "Las contraseñas no coinciden";
  position: absolute;
  top: -30px;
  left: 0;
  width: 100%;
  font-size: 12px;
  color: #e53e3e;
  background: white;
  padding: 4px;
  border-radius: 4px;
}
</style>