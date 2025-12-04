<template>
  <div class="modal-overlay" v-if="visible">
    <div class="modal">
      <h2 class="modal-title">Confirmar eliminación</h2>
      <p class="modal-text">
        Para eliminar el proyecto <strong>"{{ projectName }}"</strong>, escribe su nombre exacto:
      </p>
      <input
        v-model="typedName"
        class="modal-input"
        placeholder="Escribe el nombre exacto"
      />

      <div class="modal-buttons">
        <button
          class="btn btn-danger"
          :disabled="typedName !== projectName"
          @click="confirm"
        >
          Eliminar
        </button>
        <button class="btn btn-secondary" @click="$emit('cancel')">Cancelar</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConfirmDeleteModal",
  props: {
    visible: Boolean,
    projectName: String,
  },
  data() {
    return {
      typedName: "",
    };
  },
  methods: {
    confirm() {
      this.$emit("confirm", this.typedName);
    },
  },
  watch: {
    visible(newVal) {
      if (!newVal) {
        this.typedName = ""; // limpiar campo cuando se cierre
      }
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;       /* ✅ evita el espacio arriba */
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh; /* ✅ garantiza altura completa */
  background-color: rgba(0, 0, 0, 0.6); /* fondo oscuro */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* asegúrate de que esté encima */
}


.modal {
  background: #ffffff;
  padding: 25px;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.25s ease-in-out;
}

.modal-title {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.modal-text {
  font-size: 0.95rem;
  margin-bottom: 15px;
  color: #555;
}

.modal-input {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  margin-bottom: 20px;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-danger {
  background-color: #e53935;
  color: white;
}

.btn-danger:disabled {
  background-color: #ffcdd2;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #e0e0e0;
  color: #333;
}

.btn:hover:not(:disabled) {
  opacity: 0.9;
}

@keyframes fadeIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
