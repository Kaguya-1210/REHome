import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({ user: null }),
  actions: {
    setUser(u) { this.user = u },
    clear() { this.user = null }
  }
})

