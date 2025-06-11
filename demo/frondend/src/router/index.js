import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'login', // Nombre de la ruta
      component: LoginView, // Usa tu componente de login
    },
    { path: '/register', component: RegisterView },
    // Opcional: Redirige rutas no existentes al login
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
});

export default router;