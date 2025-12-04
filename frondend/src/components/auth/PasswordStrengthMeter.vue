<template>
  <div class="password-strength-meter">
    <div class="strength-bars flex space-x-1 mb-2">
      <div 
        v-for="i in 4" 
        :key="i"
        class="h-1.5 flex-1 rounded-full transition-all duration-500"
        :class="getBarColor(i)"
      ></div>
    </div>
    
    <div class="strength-feedback text-xs font-medium flex items-center">
      <span class="mr-1">Seguridad:</span>
      <span :class="feedbackTextClass">{{ strengthFeedback }}</span>
      <svg 
        v-if="strength >= 3" 
        xmlns="http://www.w3.org/2000/svg" 
        class="h-3 w-3 ml-1 text-green-500" 
        viewBox="0 0 20 20" 
        fill="currentColor"
      >
        <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
      </svg>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  password: {
    type: String,
    default: ''
  }
});

const strength = ref(0);

const strengthFeedback = computed(() => {
  if (!props.password) return 'No establecida';
  switch(strength.value) {
    case 1: return 'Débil';
    case 2: return 'Moderada';
    case 3: return 'Fuerte';
    case 4: return 'Muy fuerte';
    default: return 'Muy débil';
  }
});

const feedbackTextClass = computed(() => {
  return {
    'text-red-500': strength.value <= 1,
    'text-yellow-500': strength.value === 2,
    'text-green-500': strength.value >= 3
  };
});

const getBarColor = (index) => {
  if (!props.password) return 'bg-gray-200';
  
  if (index <= strength.value) {
    switch(strength.value) {
      case 1: return 'bg-red-500';
      case 2: return 'bg-yellow-500';
      case 3: return 'bg-green-400';
      case 4: return 'bg-green-600';
      default: return 'bg-gray-200';
    }
  }
  return 'bg-gray-200';
};

const calculateStrength = () => {
  let score = 0;
  const password = props.password;

  if (!password) {
    strength.value = 0;
    return;
  }

  // Requisitos básicos
  if (password.length >= 8) score++;
  if (password.length >= 12) score++;
  
  // Caracteres diversos
  if (/[A-Z]/.test(password)) score++; // Mayúsculas
  if (/\d/.test(password)) score++; // Números
  if (/[^A-Za-z0-9]/.test(password)) score++; // Caracteres especiales
  
  // Ajustar el score al rango 0-4
  strength.value = Math.min(4, score);
};

watch(() => props.password, () => {
  calculateStrength();
}, { immediate: true });
</script>

<style scoped>
.password-strength-meter {
  @apply mt-1;
}

.strength-feedback {
  @apply h-4;
}
</style>