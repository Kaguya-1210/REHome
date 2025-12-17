<template>
  <div class="island-wrapper" @mouseenter="expanded = true" @mouseleave="expanded = false">
    <div class="island" :class="{ expanded }" @click="toggle">
      <div class="island-main">
        <div class="island-title">REHome Admin</div>
        <div class="island-status">已登录</div>
      </div>
      <transition name="island-actions">
        <div v-if="expanded" class="island-actions">
          <el-button size="small" text @click.stop="goHome">
            <el-icon><HomeFilled /></el-icon>
            首页
          </el-button>
          <el-button size="small" text @click.stop="goSettings">
            <el-icon><Tools /></el-icon>
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

const toggle = () => {
  expanded.value = !expanded.value
}

const goHome = () => {
  router.push('/admin/home')
}

const goSettings = () => {
  router.push('/admin/home')
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
  transition: all 0.25s ease;
  pointer-events: auto;
}

.island.expanded {
  height: 56px;
  max-width: 520px;
}

.island-main {
  display: flex;
  flex-direction: column;
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

