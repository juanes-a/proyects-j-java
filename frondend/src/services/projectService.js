import api from '@/api'; // Usamos la instancia de axios configurada en tu proyecto

export default {
    // ... otros métodos existentes ...
    // Obtener todos los proyectos
    getAllProjects() {
        return api.get('/projects');
    },
    
    uploadCsv(file) {
        const formData = new FormData();
        formData.append('file', file);
        
        // Es importante el header multipart/form-data para enviar archivos
        return api.post('/projects/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
        uploadExcel(file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post('/projects/upload/excel', formData, { // Ruta específica para Excel
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
};