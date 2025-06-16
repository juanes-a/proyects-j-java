import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/auth/LoginView.vue';
import RegisterView from '../views/auth/RegisterView.vue';
import homeDepartaments from '../views/adminG/HomeView.vue';
import departamentsView from '../views/adminG/DepartmentsView.vue';

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
    { path: '/homeDepartaments', component: homeDepartaments },
    { path: '/departments', component: departamentsView },


  ],
});

export default router;