import { createRouter, createWebHistory } from 'vue-router'
import { check } from '@/services/auth'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/Home/Home.vue'),
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('@/views/Admin/Login.vue'),
  },
  {
    path: '/admin/home',
    name: 'admin-home',
    component: () => import('@/views/Admin/Home.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to, from) => {
  if (to.path === '/admin/home') {
    const auth = useAuthStore()
    if (auth.user) return true
    try {
      const res = await check()
      if (res?.data?.code === 2000) return true
      return '/admin'
    } catch {
      return '/admin'
    }
  }
  return true
})

export default router
