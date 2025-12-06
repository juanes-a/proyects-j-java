<script setup>
import { ref } from 'vue';
import { useToastStore } from '@/stores/toast';

const props = defineProps({
  isOpen: Boolean,
  title: {
    type: String,
    default: 'Carga Masiva'
  },
  uploadServiceFunction: {
    type: Function,
    required: true
  }
});

const emit = defineEmits(['close', 'success']);
const toast = useToastStore();

const fileInput = ref(null);
const selectedFile = ref(null);
const isLoading = ref(false);

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
  } else {
    selectedFile.value = null;
  }
};

const handleUpload = async () => {
  if (!selectedFile.value) return;

  isLoading.value = true;
  try {
    // Ejecutamos la función que nos pasaron como prop (projects o tasks)
    await props.uploadServiceFunction(selectedFile.value);
    
    toast.showToast('Carga masiva realizada con éxito', 'success');
    emit('success'); // Avisamos al padre para que recargue la lista
    closeModal();
  } catch (error) {
    console.error(error);
    const msg = error.response?.data || error.message || 'Error en la carga masiva';
    // Aseguramos que el mensaje sea texto
    const text = typeof msg === 'object' ? (msg.message || JSON.stringify(msg)) : msg;
    toast.showToast(text, 'error');
  } finally {
    isLoading.value = false;
  }
};

const closeModal = () => {
  selectedFile.value = null;
  if (fileInput.value) fileInput.value.value = '';
  emit('close');
};
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl w-full max-w-md mx-4 overflow-hidden">
      
      <div class="bg-gray-50 dark:bg-gray-700 px-6 py-4 border-b dark:border-gray-600 flex justify-between items-center">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white">{{ title }}</h3>
        <button @click="closeModal" class="text-gray-400 hover:text-gray-500 dark:hover:text-gray-300 focus:outline-none">
          <span class="text-2xl">&times;</span>
        </button>
      </div>

      <div class="p-6">
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Seleccionar archivo (CSV o Excel)</label>
          
          <div class="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 dark:border-gray-600 border-dashed rounded-md hover:border-indigo-500 transition-colors">
            <div class="space-y-1 text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <div class="flex text-sm text-gray-600 dark:text-gray-400 justify-center">
                <label class="relative cursor-pointer bg-white dark:bg-gray-800 rounded-md font-medium text-indigo-600 hover:text-indigo-500 focus-within:outline-none">
                  <span>Subir un archivo</span>
                  <input ref="fileInput" type="file" class="sr-only" @change="handleFileChange" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                </label>
              </div>
              <p class="text-xs text-gray-500 dark:text-gray-500">Hasta 10MB</p>
            </div>
          </div>

          <p v-if="selectedFile" class="mt-2 text-sm text-green-600 font-semibold break-all">
            Archivo: {{ selectedFile.name }}
          </p>
        </div>

        <div class="bg-yellow-50 dark:bg-yellow-900/20 border-l-4 border-yellow-400 p-4">
          <div class="flex">
            <div class="ml-3">
              <p class="text-xs text-yellow-700 dark:text-yellow-400">
                Asegúrate que el archivo tenga las columnas correctas (name, description, etc).
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-gray-50 dark:bg-gray-700 px-6 py-4 flex justify-end space-x-3">
        <button 
          @click="closeModal" 
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white dark:bg-gray-600 dark:text-gray-200 border border-gray-300 dark:border-gray-500 rounded-md hover:bg-gray-50 dark:hover:bg-gray-500 focus:outline-none"
          :disabled="isLoading"
        >
          Cancelar
        </button>
        <button 
          @click="handleUpload" 
          class="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700 disabled:opacity-50 flex items-center focus:outline-none"
          :disabled="!selectedFile || isLoading"
        >
          <span v-if="isLoading" class="mr-2">...</span>
          {{ isLoading ? 'Procesando...' : 'Cargar Datos' }}
        </button>
      </div>
    </div>
  </div>
</template>