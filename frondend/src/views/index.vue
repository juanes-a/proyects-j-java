<template>
  <div class="min-h-screen bg-white overflow-hidden">
    <!-- Dynamic Background -->
    <div class="fixed inset-0 z-0">
      <!-- Animated Grid -->
      <div 
        v-for="i in 15" 
        :key="i"
        class="absolute w-px h-full bg-gradient-to-b from-transparent via-orange-200/40 to-transparent"
        :style="{
          left: (i * 6.67) + '%',
          transform: `translateY(${Math.sin(Date.now() * 0.001 + i) * 30}px)`,
          opacity: 0.3 + Math.sin(Date.now() * 0.002 + i) * 0.2
        }"
      ></div>
      <div 
        v-for="i in 10" 
        :key="'h' + i"
        class="absolute w-full h-px bg-gradient-to-r from-transparent via-orange-200/30 to-transparent"
        :style="{
          top: (i * 10) + '%',
          transform: `translateX(${Math.cos(Date.now() * 0.0008 + i) * 40}px)`,
          opacity: 0.2 + Math.cos(Date.now() * 0.003 + i) * 0.1
        }"
      ></div>
      
      <!-- Floating Orbs -->
      <div 
        v-for="i in 6" 
        :key="'orb' + i"
        class="absolute rounded-full blur-3xl opacity-20"
        :style="{
          width: (100 + i * 50) + 'px',
          height: (100 + i * 50) + 'px',
          left: (i * 16) + '%',
          top: (20 + i * 15) + '%',
          background: `radial-gradient(circle, ${orbColors[i % orbColors.length]}, transparent)`,
          transform: `translate(${Math.sin(Date.now() * 0.0005 + i) * 100}px, ${Math.cos(Date.now() * 0.0003 + i) * 80}px)`
        }"
      ></div>
    </div>

    <!-- Navigation -->
    <nav class="relative z-50 px-6 py-6">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-gradient-to-br from-orange-500 to-yellow-500 rounded-xl flex items-center justify-center">
            <span class="text-white font-bold text-lg">P</span>
          </div>
          <div class="text-2xl font-bold bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">
            ProjecS-J
          </div>
        </div>
        
        <div class="hidden md:flex space-x-8 text-sm font-medium text-gray-600">
          <a href="#features" class="hover:text-orange-500 transition-colors duration-300 relative group">
            Características
            <span class="absolute -bottom-1 left-0 w-0 h-0.5 bg-orange-500 transition-all duration-300 group-hover:w-full"></span>
          </a>
          <a href="#ai" class="hover:text-orange-500 transition-colors duration-300 relative group">
            IA
            <span class="absolute -bottom-1 left-0 w-0 h-0.5 bg-orange-500 transition-all duration-300 group-hover:w-full"></span>
          </a>
          <a href="#pricing" class="hover:text-orange-500 transition-colors duration-300 relative group">
            Precios
            <span class="absolute -bottom-1 left-0 w-0 h-0.5 bg-orange-500 transition-all duration-300 group-hover:w-full"></span>
          </a>
        </div>
        
        <div class="flex items-center space-x-4">
          <router-link 
            to="/login"
            class="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors duration-300 px-4 py-2 rounded-lg hover:bg-gray-50"
          >
            Iniciar Sesión
          </router-link>
          <router-link 
            to="/register"
            class="bg-gradient-to-r from-orange-500 to-yellow-500 hover:from-orange-600 hover:to-yellow-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl"
          >
            Prueba Gratis
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="relative z-10 px-6 py-16 lg:py-24">
      <div class="max-w-7xl mx-auto">
        <div class="grid lg:grid-cols-2 gap-16 items-center">
          <!-- Left Content -->
          <div>
            <!-- Badge -->
            <div 
              class="inline-flex items-center space-x-2 bg-gradient-to-r from-orange-100 to-yellow-100 text-orange-700 px-4 py-2 rounded-full text-sm font-semibold mb-8 transform transition-all duration-500"
              :style="{
                transform: `translateY(${badgeOffset}px)`,
                opacity: badgeOpacity
              }"
            >
              <span class="w-2 h-2 bg-orange-500 rounded-full animate-pulse"></span>
              <span>Gestión de Proyectos Inteligente</span>
            </div>

            <!-- Main Headline -->
            <div class="mb-8">
              <h1 
                class="text-5xl lg:text-7xl font-bold text-gray-900 mb-6 leading-tight"
                :style="{
                  transform: `translateY(${titleOffset}px)`,
                  opacity: titleOpacity
                }"
              >
                Organiza.
                <span 
                  class="block bg-gradient-to-r from-orange-500 to-yellow-500 bg-clip-text text-transparent relative"
                  :style="{
                    transform: `translateX(${accentOffset}px)`
                  }"
                >
                  Colabora.
                  <!-- Creative underline -->
                  <svg 
                    class="absolute -bottom-2 left-0 w-full h-4" 
                    viewBox="0 0 300 20"
                    :style="{ opacity: underlineOpacity }"
                  >
                    <path 
                      d="M5,15 Q150,5 295,15" 
                      stroke="url(#gradient)" 
                      stroke-width="3" 
                      fill="none"
                      :stroke-dasharray="pathLength"
                      :stroke-dashoffset="pathOffset"
                    />
                    <defs>
                      <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" style="stop-color:#f97316"/>
                        <stop offset="100%" style="stop-color:#eab308"/>
                      </linearGradient>
                    </defs>
                  </svg>
                </span>
                <span 
                  class="block text-gray-900"
                  :style="{
                    transform: `translateY(${executeOffset}px)`,
                    opacity: executeOpacity
                  }"
                >
                  Ejecuta.
                </span>
              </h1>
              
              <p 
                class="text-xl lg:text-2xl text-gray-600 leading-relaxed max-w-xl"
                :style="{
                  transform: `translateY(${subtitleOffset}px)`,
                  opacity: subtitleOpacity
                }"
              >
                La plataforma que transforma equipos dispersos en 
                <span class="font-semibold text-gray-900">máquinas de resultados</span> 
                con inteligencia artificial integrada.
              </p>
            </div>

            <!-- CTA Buttons -->
            <div 
              class="flex flex-col sm:flex-row gap-4 mb-12"
              :style="{
                transform: `translateY(${ctaOffset}px)`,
                opacity: ctaOpacity
              }"
            >
              <router-link
                to="/register"
                @mouseenter="primaryHover = true"
                @mouseleave="primaryHover = false"
                class="group relative bg-gradient-to-r from-orange-500 to-yellow-500 hover:from-orange-600 hover:to-yellow-600 text-white px-8 py-4 rounded-2xl text-lg font-bold transition-all duration-300 transform hover:scale-105 shadow-xl hover:shadow-2xl overflow-hidden"
              >
                <!-- Animated background -->
                <div 
                  class="absolute inset-0 bg-gradient-to-r from-yellow-400 to-orange-400 transform transition-transform duration-500"
                  :class="primaryHover ? 'scale-100' : 'scale-0'"
                ></div>
                <span class="relative z-10 flex items-center justify-center">
                  Comenzar Gratis
                  <svg class="w-5 h-5 ml-2 transform transition-transform group-hover:translate-x-1" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                  </svg>
                </span>
              </router-link>
              
              <button
                @mouseenter="secondaryHover = true"
                @mouseleave="secondaryHover = false"
                class="group border-2 border-gray-300 hover:border-orange-500 text-gray-700 hover:text-orange-500 px-8 py-4 rounded-2xl text-lg font-semibold transition-all duration-300 transform hover:scale-105 backdrop-blur-sm bg-white/80"
              >
                <span class="flex items-center justify-center">
                  Ver Demo
                  <svg class="w-5 h-5 ml-2 transform transition-transform group-hover:scale-110" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd"/>
                  </svg>
                </span>
              </button>
            </div>

            <!-- Trust Indicators -->
            <div 
              class="flex items-center space-x-8 text-sm text-gray-500"
              :style="{
                opacity: trustOpacity
              }"
            >
              <div class="flex items-center space-x-2">
                <div class="flex -space-x-2">
                  <div class="w-8 h-8 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full border-2 border-white"></div>
                  <div class="w-8 h-8 bg-gradient-to-br from-green-400 to-green-600 rounded-full border-2 border-white"></div>
                  <div class="w-8 h-8 bg-gradient-to-br from-purple-400 to-purple-600 rounded-full border-2 border-white"></div>
                </div>
                <span>+10,000 equipos</span>
              </div>
              <div class="flex items-center space-x-1">
                <div class="flex space-x-1">
                  <svg v-for="i in 5" :key="i" class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                  </svg>
                </div>
                <span>4.9/5 rating</span>
              </div>
            </div>
          </div>

          <!-- Right Visual -->
          <div class="relative">
            <!-- Main Dashboard Mockup -->
            <div 
              class="relative bg-white rounded-3xl shadow-2xl p-8 transform transition-all duration-1000"
              :style="{
                transform: `translateY(${dashboardOffset}px) rotateY(${dashboardRotateY}deg)`,
                opacity: dashboardOpacity
              }"
            >
              <!-- Header -->
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center space-x-3">
                  <div class="w-3 h-3 bg-red-400 rounded-full"></div>
                  <div class="w-3 h-3 bg-yellow-400 rounded-full"></div>
                  <div class="w-3 h-3 bg-green-400 rounded-full"></div>
                </div>
                <div class="text-sm font-medium text-gray-500">Dashboard</div>
              </div>
              
              <!-- Content -->
              <div class="space-y-4">
                <!-- Progress bars -->
                <div v-for="(bar, i) in progressBars" :key="i" class="space-y-2">
                  <div class="flex justify-between text-sm">
                    <span class="font-medium text-gray-700">{{ bar.label }}</span>
                    <span class="text-gray-500">{{ bar.percentage }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="h-2 rounded-full transition-all duration-1000 ease-out"
                      :style="{ 
                        width: bar.animatedWidth + '%',
                        background: bar.gradient
                      }"
                    ></div>
                  </div>
                </div>
                
                <!-- Chart placeholder -->
                <div class="mt-6 h-32 bg-gradient-to-br from-orange-50 to-yellow-50 rounded-xl flex items-center justify-center">
                  <svg class="w-full h-full p-4" viewBox="0 0 300 100">
                    <path 
                      v-for="(path, i) in chartPaths" 
                      :key="i"
                      :d="path.d"
                      :stroke="path.color"
                      stroke-width="3"
                      fill="none"
                      :stroke-dasharray="path.length"
                      :stroke-dashoffset="path.offset"
                      class="transition-all duration-2000 ease-out"
                    />
                  </svg>
                </div>
              </div>
            </div>

            <!-- Floating Cards -->
            <div 
              v-for="(card, i) in floatingCards" 
              :key="i"
              class="absolute bg-white rounded-2xl shadow-xl p-4 transform transition-all duration-1000"
              :style="{
                ...card.position,
                transform: `translate(${card.x}px, ${card.y}px) scale(${card.scale})`,
                opacity: card.opacity
              }"
            >
              <div class="flex items-center space-x-3">
                <div 
                  class="w-10 h-10 rounded-xl flex items-center justify-center text-white text-sm font-bold"
                  :style="{ background: card.gradient }"
                >
                  {{ card.icon }}
                </div>
                <div>
                  <div class="font-semibold text-gray-900 text-sm">{{ card.title }}</div>
                  <div class="text-gray-500 text-xs">{{ card.subtitle }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="relative z-10 px-6 py-20 bg-gradient-to-br from-gray-50 to-orange-50/30">
      <div class="max-w-7xl mx-auto">
        <div class="text-center mb-16">
          <h2 class="text-4xl lg:text-6xl font-bold text-gray-900 mb-6">
            Funcionalidades que
            <span class="bg-gradient-to-r from-orange-500 to-yellow-500 bg-clip-text text-transparent">
              Marcan la Diferencia
            </span>
          </h2>
          <p class="text-xl text-gray-600 max-w-3xl mx-auto">
            Cada característica está diseñada para eliminar fricciones y potenciar la productividad de tu equipo.
          </p>
        </div>

        <!-- Feature Grid -->
        <div class="grid lg:grid-cols-3 gap-8">
          <div 
            v-for="(feature, index) in features" 
            :key="index"
            @mouseenter="hoverFeature(index)"
            @mouseleave="unhoverFeature(index)"
            class="group relative bg-white/80 backdrop-blur-sm p-8 rounded-3xl shadow-lg hover:shadow-2xl transition-all duration-500 transform hover:-translate-y-4 border border-white/50"
            :style="{
              transform: `translateY(${feature.offset}px) scale(${feature.scale})`,
              opacity: feature.opacity,
              background: feature.isHovered ? 
                'linear-gradient(135deg, rgba(249, 115, 22, 0.05), rgba(251, 146, 60, 0.05))' : 
                'rgba(255, 255, 255, 0.8)'
            }"
          >
            <!-- Gradient border effect -->
            <div 
              class="absolute inset-0 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"
              style="background: linear-gradient(135deg, #f97316, #eab308); padding: 2px;"
            >
              <div class="w-full h-full bg-white rounded-3xl"></div>
            </div>
            
            <div class="relative z-10">
              <!-- Icon -->
              <div 
                class="w-16 h-16 rounded-2xl mb-6 flex items-center justify-center text-2xl transition-all duration-500 relative overflow-hidden"
                :style="{
                  background: feature.isHovered ? 
                    'linear-gradient(135deg, #f97316, #eab308)' : 
                    'linear-gradient(135deg, #f3f4f6, #e5e7eb)',
                  transform: `scale(${feature.iconScale}) rotate(${feature.iconRotation}deg)`
                }"
              >
                <!-- Animated background -->
                <div 
                  v-if="feature.isHovered"
                  class="absolute inset-0 bg-gradient-to-r from-yellow-400 to-orange-400 animate-pulse"
                ></div>
                <span 
                  class="relative z-10 transition-colors duration-300"
                  :style="{ color: feature.isHovered ? 'white' : '#6b7280' }"
                >
                  {{ feature.icon }}
                </span>
              </div>
              
              <h3 class="text-xl font-bold text-gray-900 mb-3 group-hover:text-orange-600 transition-colors duration-300">
                {{ feature.title }}
              </h3>
              <p class="text-gray-600 leading-relaxed mb-4">{{ feature.description }}</p>
              
              <!-- Feature highlights -->
              <ul class="space-y-2">
                <li 
                  v-for="highlight in feature.highlights" 
                  :key="highlight"
                  class="flex items-center text-sm text-gray-500"
                >
                  <svg class="w-4 h-4 text-green-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
                  </svg>
                  {{ highlight }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI Section -->
    <section id="ai" class="relative z-10 px-6 py-20 bg-gradient-to-br from-gray-900 to-gray-800 text-white overflow-hidden">
      <!-- Background Effects -->
      <div class="absolute inset-0">
        <div 
          v-for="i in 20" 
          :key="i"
          class="absolute w-1 h-1 bg-orange-400 rounded-full animate-pulse opacity-30"
          :style="{
            left: Math.random() * 100 + '%',
            top: Math.random() * 100 + '%',
            animationDelay: Math.random() * 3 + 's'
          }"
        ></div>
      </div>

      <div class="max-w-7xl mx-auto relative z-10">
        <div class="grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <div class="inline-flex items-center space-x-2 bg-gradient-to-r from-orange-500/20 to-yellow-500/20 border border-orange-500/30 text-orange-300 px-4 py-2 rounded-full text-sm font-semibold mb-8">
              <span class="w-2 h-2 bg-orange-400 rounded-full animate-pulse"></span>
              <span>🤖 Próximamente</span>
            </div>
            
            <h2 class="text-4xl lg:text-6xl font-bold mb-6">
              Inteligencia Artificial
              <span class="bg-gradient-to-r from-orange-400 to-yellow-400 bg-clip-text text-transparent">
                Integrada
              </span>
            </h2>
            
            <p class="text-xl text-gray-300 mb-8 leading-relaxed">
              La próxima evolución en gestión de proyectos. IA que aprende de tus patrones, 
              predice obstáculos y optimiza automáticamente los flujos de trabajo.
            </p>
            
            <!-- AI Features -->
            <div class="space-y-6">
              <div 
                v-for="(ai, i) in aiFeatures" 
                :key="i"
                class="flex items-start space-x-4 p-4 bg-white/5 rounded-2xl backdrop-blur-sm border border-white/10 hover:bg-white/10 transition-all duration-300"
              >
                <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-500 to-yellow-500 flex items-center justify-center text-white font-bold flex-shrink-0">
                  {{ ai.icon }}
                </div>
                <div>
                  <div class="font-semibold text-white mb-1">{{ ai.title }}</div>
                  <div class="text-gray-400 text-sm leading-relaxed">{{ ai.description }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- AI Visualization -->
          <div class="relative">
            <div class="aspect-square bg-gradient-to-br from-gray-800 to-gray-900 rounded-3xl shadow-2xl p-8 relative overflow-hidden border border-gray-700">
              <!-- Neural Network -->
              <svg class="w-full h-full">
                <defs>
                  <linearGradient id="nodeGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#f97316;stop-opacity:0.8"/>
                    <stop offset="100%" style="stop-color:#eab308;stop-opacity:0.6"/>
                  </linearGradient>
                  <filter id="glow">
                    <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
                    <feMerge> 
                      <feMergeNode in="coloredBlur"/>
                      <feMergeNode in="SourceGraphic"/>
                    </feMerge>
                  </filter>
                </defs>
                
                <!-- Connections -->
                <g v-for="(connection, i) in aiConnections" :key="i">
                  <line 
                    :x1="connection.x1" 
                    :y1="connection.y1" 
                    :x2="connection.x2" 
                    :y2="connection.y2"
                    stroke="url(#nodeGradient)"
                    :stroke-width="connection.width"
                    :opacity="connection.opacity"
                    filter="url(#glow)"
                    class="animate-pulse"
                    :style="{ animationDelay: connection.delay + 's' }"
                  />
                </g>
                
                <!-- Nodes -->
                <g v-for="(node, i) in aiNodes" :key="i">
                  <circle 
                    :cx="node.x" 
                    :cy="node.y" 
                    :r="node.r"
                    fill="url(#nodeGradient)"
                    :opacity="node.opacity"
                    filter="url(#glow)"
                    class="animate-pulse"
                    :style="{ animationDelay: node.delay + 's' }"
                  />
                </g>
              </svg>
              
              <!-- Floating Data Points -->
              <div 
                v-for="i in 15" 
                :key="i"
                class="absolute text-xs font-mono text-orange-400 opacity-60 animate-pulse"
                :style="{
                  left: Math.random() * 80 + 10 + '%',
                  top: Math.random() * 80 + 10 + '%',
                  animationDelay: Math.random() * 2 + 's'
                }"
              >
                {{ Math.random() > 0.5 ? '1' : '0' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Final CTA -->
    <section class="relative z-10 px-6 py-20 bg-gradient-to-br from-orange-500 to-yellow-500">
      <div class="max-w-4xl mx-auto text-center">
        <h2 class="text-4xl lg:text-6xl font-bold text-white mb-6">
          Transforma tu Equipo
          <span class="block">Hoy Mismo</span>
        </h2>
        <p class="text-xl text-orange-100 mb-12 max-w-2xl mx-auto leading-relaxed">
          Únete a miles de equipos que ya revolucionaron su forma de trabajar. 
          Configuración en 2 minutos, resultados desde el primer día.
        </p>
        
        <div class="flex flex-col sm:flex-row gap-6 justify-center items-center">
          <router-link
            to="/register"
            @mouseenter="finalCtaHover = true"
            @mouseleave="finalCtaHover = false"
            class="bg-white text-orange-600 hover:text-orange-700 px-12 py-6 rounded-2xl text-xl font-bold transition-all duration-300 transform hover:scale-105 shadow-xl hover:shadow-2xl"
          >
            Empezar Gratis Ahora
          </router-link>
          
          <router-link
            to="/login"
            class="border-2 border-white text-white hover:bg-white hover:text-orange-600 px-12 py-6 rounded-2xl text-xl font-bold transition-all duration-300 transform hover:scale-105"
          >
            Ya Tengo Cuenta
          </router-link>
        </div>
        
        <div class="mt-8 flex items-center justify-center space-x-8 text-orange-100 text-sm">
          <div class="flex items-center space-x-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
            <span>Sin tarjeta de crédito</span>
          </div>
          <div class="flex items-center space-x-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
            <span>Configuración en 2 minutos</span>
          </div>
          <div class="flex items-center space-x-2">
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
            <span>Soporte 24/7</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="relative z-10 px-6 py-16 bg-gray-900 text-white">
      <div class="max-w-7xl mx-auto">
        <div class="grid lg:grid-cols-4 gap-12">
          <div class="lg:col-span-2">
            <div class="flex items-center space-x-3 mb-6">
              <div class="w-12 h-12 bg-gradient-to-br from-orange-500 to-yellow-500 rounded-xl flex items-center justify-center">
                <span class="text-white font-bold text-xl">P</span>
              </div>
              <div class="text-2xl font-bold">ProjecS-J</div>
            </div>
            <p class="text-gray-400 leading-relaxed max-w-md">
              La plataforma de gestión de proyectos que evoluciona con tu equipo. 
              Diseñada para empresas que buscan eficiencia sin sacrificar simplicidad.
            </p>
          </div>
          
          <div>
            <h4 class="font-semibold mb-6 text-lg">Producto</h4>
            <div class="space-y-3 text-gray-400">
              <a href="#features" class="block hover:text-white transition-colors">Características</a>
              <a href="#ai" class="block hover:text-white transition-colors">Inteligencia Artificial</a>
              <a href="#pricing" class="block hover:text-white transition-colors">Precios</a>
              <a href="#" class="block hover:text-white transition-colors">Integraciones</a>
            </div>
          </div>
          
          <div>
            <h4 class="font-semibold mb-6 text-lg">Empresa</h4>
            <div class="space-y-3 text-gray-400">
              <a href="#" class="block hover:text-white transition-colors">Acerca de</a>
              <a href="#" class="block hover:text-white transition-colors">Blog</a>
              <a href="#" class="block hover:text-white transition-colors">Carreras</a>
              <a href="#" class="block hover:text-white transition-colors">Contacto</a>
            </div>
          </div>
        </div>
        
        <div class="border-t border-gray-800 mt-12 pt-8 flex flex-col lg:flex-row justify-between items-center">
          <div class="text-gray-400 text-sm">
            © 2024 ProjecS-J. Todos los derechos reservados.
          </div>
          <div class="flex space-x-6 mt-4 lg:mt-0">
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/>
              </svg>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M22.46 6c-.77.35-1.6.58-2.46.69.88-.53 1.56-1.37 1.88-2.38-.83.5-1.75.85-2.72 1.05C18.37 4.5 17.26 4 16 4c-2.35 0-4.27 1.92-4.27 4.29 0 .34.04.67.11.98C8.28 9.09 5.11 7.38 3 4.79c-.37.63-.58 1.37-.58 2.15 0 1.49.75 2.81 1.91 3.56-.71 0-1.37-.2-1.95-.5v.03c0 2.08 1.48 3.82 3.44 4.21a4.22 4.22 0 0 1-1.93.07 4.28 4.28 0 0 0 4 2.98 8.521 8.521 0 0 1-5.33 1.84c-.34 0-.68-.02-1.02-.06C3.44 20.29 5.7 21 8.12 21 16 21 20.33 14.46 20.33 8.79c0-.19 0-.37-.01-.56.84-.6 1.56-1.36 2.14-2.23z"/>
              </svg>
            </a>
            <a href="#" class="text-gray-400 hover:text-white transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// Animation states
const badgeOffset = ref(30)
const badgeOpacity = ref(0)
const titleOffset = ref(40)
const titleOpacity = ref(0)
const accentOffset = ref(20)
const underlineOpacity = ref(0)
const pathLength = ref(300)
const pathOffset = ref(300)
const executeOffset = ref(30)
const executeOpacity = ref(0)
const subtitleOffset = ref(50)
const subtitleOpacity = ref(0)
const ctaOffset = ref(60)
const ctaOpacity = ref(0)
const trustOpacity = ref(0)
const dashboardOffset = ref(100)
const dashboardOpacity = ref(0)
const dashboardRotateY = ref(15)

// Hover states
const primaryHover = ref(false)
const secondaryHover = ref(false)
const finalCtaHover = ref(false)

// Background colors
const orbColors = [
  'rgba(249, 115, 22, 0.1)',
  'rgba(251, 146, 60, 0.1)',
  'rgba(234, 179, 8, 0.1)',
  'rgba(59, 130, 246, 0.05)',
  'rgba(168, 85, 247, 0.05)',
  'rgba(34, 197, 94, 0.05)'
]

// Dashboard mockup data
const progressBars = ref([
  { 
    label: 'Desarrollo Frontend', 
    percentage: 85, 
    animatedWidth: 0,
    gradient: 'linear-gradient(90deg, #f97316, #fb923c)'
  },
  { 
    label: 'Testing & QA', 
    percentage: 60, 
    animatedWidth: 0,
    gradient: 'linear-gradient(90deg, #3b82f6, #60a5fa)'
  },
  { 
    label: 'Documentación', 
    percentage: 40, 
    animatedWidth: 0,
    gradient: 'linear-gradient(90deg, #10b981, #34d399)'
  }
])

const chartPaths = ref([
  {
    d: 'M20,80 Q80,20 140,60 T260,40',
    color: '#f97316',
    length: 400,
    offset: 400
  },
  {
    d: 'M20,60 Q80,40 140,70 T260,50',
    color: '#3b82f6',
    length: 400,
    offset: 400
  }
])

const floatingCards = ref([
  {
    icon: '📊',
    title: 'Analytics',
    subtitle: '+23% este mes',
    gradient: 'linear-gradient(135deg, #f97316, #fb923c)',
    position: { top: '20px', right: '-50px' },
    x: 0, y: 0, scale: 0, opacity: 0
  },
  {
    icon: '⚡',
    title: 'Velocidad',
    subtitle: '2.3x más rápido',
    gradient: 'linear-gradient(135deg, #3b82f6, #60a5fa)',
    position: { bottom: '60px', left: '-60px' },
    x: 0, y: 0, scale: 0, opacity: 0
  },
  {
    icon: '🎯',
    title: 'Objetivos',
    subtitle: '94% completado',
    gradient: 'linear-gradient(135deg, #10b981, #34d399)',
    position: { top: '50%', right: '-80px' },
    x: 0, y: 0, scale: 0, opacity: 0
  }
])

// Features data
const features = ref([
  {
    icon: '🎯',
    title: 'Gestión de Tareas Inteligente',
    description: 'Organiza, prioriza y asigna tareas con IA que aprende de tus patrones de trabajo.',
    highlights: ['Asignación automática', 'Priorización inteligente', 'Seguimiento en tiempo real'],
    offset: 50, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  },
  {
    icon: '📊',
    title: 'Analytics Avanzados',
    description: 'Dashboards que revelan insights accionables sobre el rendimiento de tu equipo.',
    highlights: ['Métricas personalizables', 'Reportes automáticos', 'Predicciones de rendimiento'],
    offset: 60, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  },
  {
    icon: '⚡',
    title: 'Colaboración en Tiempo Real',
    description: 'Comunicación fluida con herramientas integradas que mantienen a todos sincronizados.',
    highlights: ['Chat integrado', 'Comentarios contextuales', 'Notificaciones inteligentes'],
    offset: 70, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  },
  {
    icon: '🔄',
    title: 'Automatización de Flujos',
    description: 'Workflows que se ejecutan automáticamente, eliminando tareas repetitivas.',
    highlights: ['Triggers personalizables', 'Integraciones nativas', 'Reglas de negocio'],
    offset: 55, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  },
  {
    icon: '🛡️',
    title: 'Seguridad Empresarial',
    description: 'Protección de datos con estándares de seguridad de nivel empresarial.',
    highlights: ['Encriptación end-to-end', 'Auditoría completa', 'Cumplimiento GDPR'],
    offset: 65, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  },
  {
    icon: '🎨',
    title: 'Interfaz Adaptable',
    description: 'Diseño que se adapta a tu forma de trabajar, no al revés.',
    highlights: ['Vistas personalizables', 'Temas adaptativos', 'Accesibilidad completa'],
    offset: 75, opacity: 0, scale: 1, isHovered: false, iconScale: 1, iconRotation: 0
  }
])

// AI Features
const aiFeatures = [
  {
    icon: '🧠',
    title: 'Predicción de Bloqueos',
    description: 'Identifica problemas potenciales antes de que afecten el cronograma del proyecto.'
  },
  {
    icon: '⚙️',
    title: 'Automatización Inteligente',
    description: 'Ejecuta tareas repetitivas automáticamente basándose en patrones aprendidos.'
  },
  {
    icon: '📈',
    title: 'Optimización Continua',
    description: 'Sugiere mejoras en procesos basándose en datos históricos y tendencias.'
  },
  {
    icon: '🎯',
    title: 'Asignación Óptima',
    description: 'Distribuye tareas considerando habilidades, carga de trabajo y disponibilidad.'
  }
]

// AI Visualization
const aiNodes = ref([])
const aiConnections = ref([])

// Animation intervals
let backgroundAnimation
let dashboardAnimation

// Methods
const hoverFeature = (index) => {
  features.value[index].isHovered = true
  features.value[index].scale = 1.02
  features.value[index].iconScale = 1.15
  features.value[index].iconRotation = 10
}

const unhoverFeature = (index) => {
  features.value[index].isHovered = false
  features.value[index].scale = 1
  features.value[index].iconScale = 1
  features.value[index].iconRotation = 0
}

const initializeAIVisualization = () => {
  // Create nodes
  for (let i = 0; i < 20; i++) {
    aiNodes.value.push({
      x: 50 + Math.random() * 300,
      y: 50 + Math.random() * 300,
      r: 2 + Math.random() * 6,
      opacity: 0.4 + Math.random() * 0.6,
      delay: Math.random() * 3
    })
  }
  
  // Create connections
  aiNodes.value.forEach((node, i) => {
    if (i < aiNodes.value.length - 2) {
      const nextNode = aiNodes.value[i + 1]
      const distance = Math.sqrt(Math.pow(node.x - nextNode.x, 2) + Math.pow(node.y - nextNode.y, 2))
      
      if (distance < 150) {
        aiConnections.value.push({
          x1: node.x,
          y1: node.y,
          x2: nextNode.x,
          y2: nextNode.y,
          width: 1 + Math.random() * 2,
          opacity: 0.2 + Math.random() * 0.4,
          delay: Math.random() * 2
        })
      }
    }
  })
}

const startHeroAnimation = () => {
  // Sequential animation
  const animations = [
    { target: () => { badgeOffset.value = 0; badgeOpacity.value = 1 }, delay: 200 },
    { target: () => { titleOffset.value = 0; titleOpacity.value = 1 }, delay: 400 },
    { target: () => { accentOffset.value = 0 }, delay: 800 },
    { target: () => { underlineOpacity.value = 1; pathOffset.value = 0 }, delay: 1200 },
    { target: () => { executeOffset.value = 0; executeOpacity.value = 1 }, delay: 1600 },
    { target: () => { subtitleOffset.value = 0; subtitleOpacity.value = 1 }, delay: 2000 },
    { target: () => { ctaOffset.value = 0; ctaOpacity.value = 1 }, delay: 2400 },
    { target: () => { trustOpacity.value = 1 }, delay: 2800 },
    { target: () => { 
      dashboardOffset.value = 0; 
      dashboardOpacity.value = 1; 
      dashboardRotateY.value = 0 
    }, delay: 3200 }
  ]
  
  animations.forEach(({ target, delay }) => {
    setTimeout(target, delay)
  })
  
  // Animate progress bars
  setTimeout(() => {
    progressBars.value.forEach((bar, i) => {
      setTimeout(() => {
        bar.animatedWidth = bar.percentage
      }, i * 300)
    })
  }, 3800)
  
  // Animate chart paths
  setTimeout(() => {
    chartPaths.value.forEach((path, i) => {
      setTimeout(() => {
        path.offset = 0
      }, i * 500)
    })
  }, 4200)
  
  // Animate floating cards
  setTimeout(() => {
    floatingCards.value.forEach((card, i) => {
      setTimeout(() => {
        card.scale = 1
        card.opacity = 1
      }, i * 200)
    })
  }, 4600)
}

const startFeatureAnimation = () => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        features.value.forEach((feature, i) => {
          setTimeout(() => {
            feature.offset = 0
            feature.opacity = 1
          }, i * 150)
        })
      }
    })
  }, { threshold: 0.1 })
  
  setTimeout(() => {
    const featuresSection = document.querySelector('#features')
    if (featuresSection) {
      observer.observe(featuresSection)
    }
  }, 1000)
}

const startBackgroundAnimation = () => {
  backgroundAnimation = setInterval(() => {
    // Background animations are handled by CSS transforms in template
  }, 50)
}

onMounted(() => {
  initializeAIVisualization()
  startHeroAnimation()
  startFeatureAnimation()
  startBackgroundAnimation()
})

onUnmounted(() => {
  if (backgroundAnimation) clearInterval(backgroundAnimation)
  if (dashboardAnimation) clearInterval(dashboardAnimation)
})
</script>

<style scoped>
.transform-gpu {
  transform: translateZ(0);
  backface-visibility: hidden;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse-soft {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-pulse-soft {
  animation: pulse-soft 3s ease-in-out infinite;
}

/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}

/* Custom gradient text */
.gradient-text {
  background: linear-gradient(135deg, #f97316, #eab308);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>