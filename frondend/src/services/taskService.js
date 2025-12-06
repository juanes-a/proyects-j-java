import api from '@/api';

export default {
    // Obtener todas las tareas
    getAllTasks() {
        return api.get('/tasks');
    },

    // Crear tarea
    createTask(task) {
        return api.post('/tasks', task);
    },

    // Actualizar tarea
    updateTask(id, task) {
        return api.put(`/tasks/${id}`, task);
    },

    // Eliminar tarea
    deleteTask(id) {
        return api.delete(`/tasks/${id}`);
    },

    // Carga Masiva CSV
    uploadCsv(file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post('/tasks/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    // Carga Masiva Excel
    uploadExcel(file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post('/tasks/upload/excel', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
};