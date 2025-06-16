import { defineStore } from "pinia"
import { ref } from "vue"

export const useThemeStore = defineStore("theme", () => {
  const isDark = ref(false)

  const initTheme = () => {
    const savedTheme = localStorage.getItem("theme")
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches

    isDark.value = savedTheme ? savedTheme === "dark" : prefersDark
    updateTheme()
  }

  const toggleTheme = () => {
    isDark.value = !isDark.value
    updateTheme()
  }

  const updateTheme = () => {
    if (isDark.value) {
      document.documentElement.classList.add("dark")
      localStorage.setItem("theme", "dark")
    } else {
      document.documentElement.classList.remove("dark")
      localStorage.setItem("theme", "light")
    }
  }

  return {
    isDark,
    initTheme,
    toggleTheme,
  }
})
