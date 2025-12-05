<script setup>
import { ref } from 'vue';
import { useToast } from '@/stores/toast';

// Props: título del modal y la función del servicio que hace el POST
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
const toast = useToast();

const fileInput = ref(null);
const selectedFile = ref(null);
const isLoading = ref(false);

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file && file.type === 'text/csv') {
    selectedFile.value = file;
  } else {
    toast.error('Por favor selecciona un archivo CSV válido.');
    event.target.value = null; // Reset input
    selectedFile.value = null;
  }
};

const handleUpload = async () => {
  if (!selectedFile.value) return;

  isLoading.value = true;
  try {
    // Ejecutamos la función que nos pasaron como prop (sea de proyectos o tareas)
    await props.uploadServiceFunction(selectedFile.value);
    
    toast.success('Carga masiva realizada con éxito');
    emit('success'); // Avisamos al padre para que recargue la lista
    closeModal();
  } catch (error) {
    console.error(error);
    toast.error(error.response?.data || 'Error en la carga masiva');
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
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4 overflow-hidden">
      
      <div class="bg-gray-50 px-6 py-4 border-b flex justify-between items-center">
        <h3 class="text-lg font-medium text-gray-900">{{ title }}</h3>
        <button @click="closeModal" class="text-gray-400 hover:text-gray-500">
          <span class="text-2xl">&times;</span>
        </button>
      </div>

      <div class="p-6">
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">Seleccionar archivo CSV</label>
          <div class="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-blue-500 transition-colors">
            <div class="space-y-1 text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <div class="flex text-sm text-gray-600 justify-center">
                <label class="relative cursor-pointer bg-white rounded-md font-medium text-blue-600 hover:text-blue-500 focus-within:outline-none">
                  <span>Subir un archivo</span>
                  <input ref="fileInput" type="file" class="sr-only" accept=".csv" @change="handleFileChange">
                </label>
              </div>
              <p class="text-xs text-gray-500">CSV hasta 10MB</p>
            </div>
          </div>
          <p v-if="selectedFile" class="mt-2 text-sm text-green-600 font-semibold">
            Archivo: {{ selectedFile.name }}
          </p>
        </div>

        <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
          <div class="flex">
            <div class="ml-3">
              <p class="text-xs text-yellow-700">
                Asegúrate que el CSV tenga las columnas correctas.
                <br>Proyectos: <i>name, description, objectives...</i>
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-gray-50 px-6 py-4 flex justify-end space-x-3">
        <button 
          @click="closeModal" 
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
          :disabled="isLoading"
        >
          Cancelar
        </button>
        <button 
          @click="handleUpload" 
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center"
          :disabled="!selectedFile || isLoading"
        >
          <span v-if="isLoading" class="mr-2">...</span>
          {{ isLoading ? 'Subiendo...' : 'Cargar Datos' }}
        </button>
      </div>
    </div>
  </div>
</template>