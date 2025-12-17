<template>
  <div class="island-wrapper" @mouseenter="onEnter" @mouseleave="onLeave">
    <div class="island" :class="{ expanded }">
      <div style="padding-right: 15px;display: flex; align-items: center;">
      <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
      </div>
      <div class="island-main">
        <div class="island-title">REHome Admin</div>
        <div class="island-status">已登录</div>
      </div>
      <transition name="island-actions">
        <div v-if="expanded" class="island-actions">
          <el-button size="small" text @click.stop="goAdminHome">
            <el-icon><Menu /></el-icon>
            菜单
          </el-button>
          <el-button size="small" text @click.stop="goFrontend">
            <el-icon><Setting /></el-icon>
            设置
          </el-button>
          <el-button size="small" text type="danger" @click.stop="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '@/services/auth'

const expanded = ref(false)
const router = useRouter()
let hideTimer

const onEnter = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
    hideTimer = null
  }
  expanded.value = true
}

const onLeave = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
  }
  hideTimer = setTimeout(() => {
    expanded.value = false
    hideTimer = null
  }, 130)
}

const goAdminHome = () => {
  router.push('/admin/home')
}

const goFrontend = () => {
  router.push('/')
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (e) {
  }
}
</script>

<style scoped>
.island-wrapper {
  position: fixed;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  pointer-events: none;
}

.island {
  min-width: 180px;
  max-width: 520px;
  height: 40px;
  border-radius: 20px;
  background: radial-gradient(circle at 10% 10%, #4b8dff, #1f1f2b);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  color: #fff;
  cursor: pointer;
  transition: height 0.25s cubic-bezier(.4, 0, .2, 1),
              transform 0.25s cubic-bezier(.4, 0, .2, 1),
              box-shadow 0.25s cubic-bezier(.4, 0, .2, 1);
  pointer-events: auto;
  overflow: hidden;
}

.island.expanded {
  height: 56px;
  max-width: 520px;
  transform: scale(1.02);
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.3);
}

.island-main {
  display: flex;
  flex-direction: column;
  padding-right: 15px;
}

.island-title {
  font-size: 14px;
  font-weight: 600;
}

.island-status {
  font-size: 12px;
  opacity: 0.85;
}

.island-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.island-actions :deep(.el-button) {
  color: #fff;
}

.island-actions-enter-active,
.island-actions-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.island-actions-enter-from,
.island-actions-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 768px) {
  .island {
    min-width: 160px;
    max-width: 320px;
  }
}
</style>
