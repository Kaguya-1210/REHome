<template>
  <div class="island-wrapper" @mouseenter="onEnter" @mouseleave="onLeave">
    <div ref="islandEl" class="island" :class="islandClasses" @pointermove="onPointerMove" @pointerleave="onPointerLeave" @pointerdown="onPointerDown" @pointerup="onPointerUp" @pointercancel="onPointerUp">
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
import { ref, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '@/services/auth'

const expanded = ref(false)
const router = useRouter()
let hideTimer
let animTimer
const islandEl = ref(null)
const pressed = ref(false)
const animState = ref('')

const islandClasses = computed(() => ({
  expanded: expanded.value,
  expanding: animState.value === 'expanding',
  collapsing: animState.value === 'collapsing',
  pressed: pressed.value
}))

const motion = {
  raf: null,
  x: 0,
  y: 0,
  rx: 0,
  ry: 0,
  vX: 0,
  vY: 0,
  vRx: 0,
  vRy: 0,
  tX: 0,
  tY: 0,
  tRx: 0,
  tRy: 0
}

const setVars = () => {
  const el = islandEl.value
  if (!el) return
  el.style.setProperty('--ix', `${motion.x.toFixed(2)}px`)
  el.style.setProperty('--iy', `${motion.y.toFixed(2)}px`)
  el.style.setProperty('--rx', `${motion.rx.toFixed(3)}deg`)
  el.style.setProperty('--ry', `${motion.ry.toFixed(3)}deg`)
}

const tick = () => {
  const kPos = 0.16
  const dPos = 0.76
  const kRot = 0.14
  const dRot = 0.72

  motion.vX = (motion.vX + (motion.tX - motion.x) * kPos) * dPos
  motion.vY = (motion.vY + (motion.tY - motion.y) * kPos) * dPos
  motion.x += motion.vX
  motion.y += motion.vY

  motion.vRx = (motion.vRx + (motion.tRx - motion.rx) * kRot) * dRot
  motion.vRy = (motion.vRy + (motion.tRy - motion.ry) * kRot) * dRot
  motion.rx += motion.vRx
  motion.ry += motion.vRy

  setVars()

  const moving = Math.abs(motion.vX) + Math.abs(motion.vY) + Math.abs(motion.vRx) + Math.abs(motion.vRy) > 0.02
  const far = Math.abs(motion.tX - motion.x) + Math.abs(motion.tY - motion.y) + Math.abs(motion.tRx - motion.rx) + Math.abs(motion.tRy - motion.ry) > 0.2
  if (moving || far) {
    motion.raf = requestAnimationFrame(tick)
  } else {
    motion.raf = null
  }
}

const driveTo = (x, y, rx, ry) => {
  motion.tX = x
  motion.tY = y
  motion.tRx = rx
  motion.tRy = ry
  if (!motion.raf) {
    motion.raf = requestAnimationFrame(tick)
  }
}

const onEnter = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
    hideTimer = null
  }
  expanded.value = true
  animState.value = 'expanding'
  if (animTimer) clearTimeout(animTimer)
  animTimer = setTimeout(() => { animState.value = '' }, 420)
}

const onLeave = () => {
  if (hideTimer) {
    clearTimeout(hideTimer)
  }
  hideTimer = setTimeout(() => {
    expanded.value = false
    animState.value = 'collapsing'
    if (animTimer) clearTimeout(animTimer)
    animTimer = setTimeout(() => { animState.value = '' }, 520)
    hideTimer = null
  }, 130)
}

const onPointerMove = (e) => {
  const el = islandEl.value
  if (!el) return
  const rect = el.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2
  const nx = (e.clientX - cx) / (rect.width / 2)
  const ny = (e.clientY - cy) / (rect.height / 2)
  const clampedX = Math.max(-1, Math.min(1, nx))
  const clampedY = Math.max(-1, Math.min(1, ny))
  const x = clampedX * 10
  const y = clampedY * 6
  const ry = clampedX * 3.5
  const rx = -clampedY * 2.6
  driveTo(x, y, rx, ry)
}

const onPointerLeave = () => {
  driveTo(0, 0, 0, 0)
}

const onPointerDown = () => {
  pressed.value = true
}

const onPointerUp = () => {
  pressed.value = false
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

onBeforeUnmount(() => {
  if (hideTimer) clearTimeout(hideTimer)
  if (animTimer) clearTimeout(animTimer)
  if (motion.raf) cancelAnimationFrame(motion.raf)
})
</script>

<style scoped>
@property --islandScale {
  syntax: '<number>';
  inherits: false;
  initial-value: 1;
}

.island {
  --ix: 0px;
  --iy: 0px;
  --rx: 0deg;
  --ry: 0deg;
  --islandScale: 1;
}

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
  transition: height 340ms cubic-bezier(.2, .8, .2, 1),
              box-shadow 340ms cubic-bezier(.2, .8, .2, 1),
              --islandScale 340ms cubic-bezier(.2, .8, .2, 1);
  pointer-events: auto;
  overflow: hidden;
  position: relative;
  transform: translate3d(var(--ix), var(--iy), 0) perspective(900px) rotateX(var(--rx)) rotateY(var(--ry)) scale(var(--islandScale)) translateZ(0);
  will-change: transform, height;
  backface-visibility: hidden;
  contain: layout paint;
}

.island::before {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  background: radial-gradient(circle at 30% 10%, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0) 55%);
  opacity: 0;
  transform: translate3d(-10px, -6px, 0);
  transition: opacity 260ms ease, transform 320ms cubic-bezier(.2, .8, .2, 1);
  pointer-events: none;
}

.island::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(90deg, rgba(255,255,255,0), rgba(255,255,255,0.16), rgba(255,255,255,0));
  opacity: 0;
  transform: translate3d(-60%, 0, 0);
  transition: opacity 180ms ease;
  pointer-events: none;
}

.island.expanded {
  height: 56px;
  max-width: 520px;
  --islandScale: 1.03;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.3);
}

.island.expanded::before {
  opacity: 1;
  transform: translate3d(0, 0, 0);
}

.island.expanded::after {
  opacity: 1;
  transform: translate3d(60%, 0, 0);
  transition: transform 780ms cubic-bezier(.2, .8, .2, 1), opacity 180ms ease;
}

.island.pressed {
  --islandScale: 0.99;
}

.island.expanding {
  animation: island-pop-in 520ms cubic-bezier(.16, 1, .3, 1);
}

.island.collapsing {
  animation: island-pop-out 560ms cubic-bezier(.2, .8, .2, 1);
}

@keyframes island-pop-in {
  0% { --islandScale: 0.985; }
  55% { --islandScale: 1.05; }
  100% { --islandScale: 1.03; }
}

@keyframes island-pop-out {
  0% { --islandScale: 1.03; }
  60% { --islandScale: 0.985; }
  100% { --islandScale: 1; }
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
