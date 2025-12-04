import { createRouter, createWebHistory } from 'vue-router';

import index from '../views/index.vue';

import LoginView from '../views/auth/LoginView.vue';
import RegisterView from '../views/auth/RegisterView.vue';

import homeDepartaments from '../views/adminG/HomeView.vue';
import departamentsView from '../views/adminG/DepartmentsView.vue';
import ProjectsView from '../views/ProjectsView.vue';

import DepartmentHome from '../views/adminD/DepartmentHome.vue';
import DepartmentProjects from '../views/adminD/DepartmentProjectsView.vue';
import DepartmentStats from '../views/adminD/DepartmentStats.vue';

// (opcional) Vista para acceso denegado
import UnauthorizedView from '../views/Unauthorized.vue';
import SettingsView from '../views/SettingsView.vue';

const routes = [
  {
    path: '/',
    name: 'index',
    component: index,
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/register',
    component: RegisterView,
  },

  {
    path: '/unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/settings',
    name: 'settings',
    component: SettingsView,
    meta: { 
      requiresAuth: true, 
      // Si dejas 'roles' vacío o lo omites, asegúrate que tu lógica en beforeEach lo permita.
      // Si tu lógica requiere explícitamente roles, agrégalos todos:
      roles: ['ADMIN_GLOBAL', 'ADMIN_DEPT', 'ADMIN_COLLAB', 'COLLAB']
    }
  },

  // Admin Global
  {
    path: '/homeDepartaments',
    component: homeDepartaments,
    meta: { requiresAuth: true, roles: ['ADMIN_GLOBAL'] }
  },
  {
    path: '/departments',
    component: departamentsView,
    meta: { requiresAuth: true, roles: ['ADMIN_GLOBAL'] }
  },
  {
    path: '/projects',
    name: 'projects',
    component: ProjectsView,
    meta: { requiresAuth: true, roles: ['ADMIN_GLOBAL'] }
  },


  // Admin Departamento
  {
    path: '/departmentHome',
    component: DepartmentHome,
    meta: { requiresAuth: true, roles: ['ADMIN_DEPT'] }
  },
  {
    path: '/projectsDept',
    component: DepartmentProjects,
    meta: { requiresAuth: true, roles: ['ADMIN_DEPT'] }
  },

  {
    path: '/deptStats',
    name: 'deptStats',
    component: DepartmentStats,
    meta: { requiresAuth: true, roles: ['ADMIN_DEPT'] }
  },

  // Admin Departamento

  {
    path: '/tasks',
    name: 'TasksGlobal',
    component: () => import('@/views/adminC/TaskView.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN_COLLAB','ADMIN_GLOBAL']}
  },
    {
    path: '/dashTask',
    name: 'taskdash',
    component: () => import('@/views/adminC/DashboardView.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN_COLLAB']}
  },

  // Collab

  {
      path: '/tasksDash',
      name: 'TasksDashboard',
      component: () => import('../views/Collab/TaskDash.vue'),
      meta: { requiresAuth: true, roles: ['COLLAB']}
  },
  {
      path: '/homeTask',
      name: 'homeTask',
      component: () => import('../views/Collab/home.vue'),
      meta: { requiresAuth: true, roles: ['COLLAB']}
  },


  // Catch-all: Redirige a login
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken');
  const user = JSON.parse(localStorage.getItem('user'));

  if (to.meta.requiresAuth) {
    if (!token || !user) {
      return next('/');
    }

    // Verificar si el usuario tiene un rol permitido
    if (to.meta.roles && !to.meta.roles.includes(user.role)) {
      return next('/unauthorized');
    }
  }

  next();
});


export default router;
