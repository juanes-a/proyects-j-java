import api from '@/api'; // Usamos la instancia de axios configurada en tu proyecto

export default {
    // ... otros métodos existentes ...
    
    uploadCsv(file) {
        const formData = new FormData();
        formData.append('file', file);
        
        // Es importante el header multipart/form-data para enviar archivos
        return api.post('/projects/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
};