import api from '@/api';

export default {
    uploadCsv(file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post('/tasks/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
};