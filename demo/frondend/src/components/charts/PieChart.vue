<template>
  <canvas ref="chartCanvas"></canvas>
</template>

<script>
import { Chart, registerables } from 'chart.js'
import { defineComponent, ref, onMounted, watch } from 'vue'

Chart.register(...registerables)

export default defineComponent({
  name: 'PieChart',
  props: {
    data: {
      type: Object,
      required: true
    },
    options: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    const chartCanvas = ref(null)
    let chartInstance = null

    const renderChart = () => {
      if (chartInstance) {
        chartInstance.destroy()
      }

      if (chartCanvas.value) {
        chartInstance = new Chart(chartCanvas.value, {
          type: 'pie',
          data: props.data,
          options: props.options
        })
      }
    }

    onMounted(() => {
      renderChart()
    })

    watch(
      () => [props.data, props.options],
      () => {
        renderChart()
      },
      { deep: true }
    )

    return { chartCanvas }
  }
})
</script>