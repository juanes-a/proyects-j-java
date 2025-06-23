import { defineStore } from "pinia"
import { ref, watchEffect } from "vue"

export const useThemeStore = defineStore("theme", () => {
  const isDark = ref(false)
  const theme = ref('system') // Puede ser 'light', 'dark' o 'system'
  const availableThemes = ['light', 'dark', 'system']

  // Inicializar el tema
  const initTheme = () => {
    const savedTheme = localStorage.getItem("theme")
    const systemPrefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
    
    if (savedTheme) {
      theme.value = savedTheme
      isDark.value = savedTheme === 'dark' || (savedTheme === 'system' && systemPrefersDark)
    } else {
      theme.value = 'system'
      isDark.value = systemPrefersDark
    }
    
    updateTheme()
    setupThemeListener()
  }

  // Cambiar entre temas
  const setTheme = (newTheme) => {
    if (!availableThemes.includes(newTheme)) return
    
    theme.value = newTheme
    localStorage.setItem("theme", newTheme)
    
    if (newTheme === 'system') {
      const systemPrefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
      isDark.value = systemPrefersDark
    } else {
      isDark.value = newTheme === 'dark'
    }
    
    updateTheme()
  }

  // Alternar entre claro/oscuro (manteniendo la preferencia de sistema si estaba en 'system')
  const toggleTheme = () => {
    if (theme.value === 'system') {
      // Si estaba en sistema, lo fijamos al tema opuesto al actual
      setTheme(isDark.value ? 'light' : 'dark')
    } else {
      // Si ya estaba en light/dark, simplemente alternamos
      setTheme(isDark.value ? 'light' : 'dark')
    }
  }

  // Actualizar las clases en el DOM
  const updateTheme = () => {
    if (isDark.value) {
      document.documentElement.classList.add("dark")
    } else {
      document.documentElement.classList.remove("dark")
    }
  }

  // Escuchar cambios en las preferencias del sistema
  const setupThemeListener = () => {
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
    
    const listener = (e) => {
      if (theme.value === 'system') {
        isDark.value = e.matches
        updateTheme()
      }
    }
    
    mediaQuery.addEventListener('change', listener)
    
    // Limpiar al desmontar
    return () => mediaQuery.removeEventListener('change', listener)
  }

  // Auto-inicialización cuando el store es creado
  initTheme()

  return {
    isDark,
    theme,
    availableThemes,
    initTheme,
    setTheme,
    toggleTheme,
  }
})