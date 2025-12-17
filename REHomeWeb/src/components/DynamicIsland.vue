<template>
  <div ref="wrapperEl" class="island-wrapper" :class="{ dragging }" :style="wrapperStyle" @mouseenter="onEnter" @mouseleave="onLeave">
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
import { ref, computed, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '@/services/auth'

const expanded = ref(false)
const router = useRouter()
let hideTimer
let animTimer
const islandEl = ref(null)
const wrapperEl = ref(null)
const pressed = ref(false)
const animState = ref('')
const dragging = ref(false)
const dragPointerId = ref(null)
const dragStartClientX = ref(0)
const dragStartClientY = ref(0)
const dragStartDx = ref(0)
const dragStartDy = ref(0)
const dx = ref(0)
const dy = ref(0)

const STORAGE_KEY = 'rehome.dynamicIsland.position.v1'
const BASE_TOP = 12
const EDGE_SNAP_THRESHOLD = 50
const VIEWPORT_MARGIN = 8

const wrapperStyle = computed(() => ({
  transform: `translateX(-50%) translate3d(${dx.value.toFixed(2)}px, ${dy.value.toFixed(2)}px, 0)`
}))

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

let dragAnimRaf = null
let dragAnimStart = 0
let dragAnimFromX = 0
let dragAnimFromY = 0
let dragAnimOverX = 0
let dragAnimOverY = 0
let dragAnimToX = 0
let dragAnimToY = 0

const clamp = (v, min, max) => Math.max(min, Math.min(max, v))

const getBounds = () => {
  const el = wrapperEl.value
  const w = el ? el.offsetWidth : 0
  const h = el ? el.offsetHeight : 0
  const vw = window.innerWidth
  const vh = window.innerHeight
  const baseLeft = vw / 2 - w / 2
  const baseTop = BASE_TOP
  const minDx = VIEWPORT_MARGIN - baseLeft
  const maxDx = (vw - VIEWPORT_MARGIN - w) - baseLeft
  const minDy = VIEWPORT_MARGIN - baseTop
  const maxDy = (vh - VIEWPORT_MARGIN - h) - baseTop
  return { w, h, vw, vh, baseLeft, baseTop, minDx, maxDx, minDy, maxDy }
}

const clampToViewport = (x, y) => {
  const b = getBounds()
  return {
    x: clamp(x, b.minDx, b.maxDx),
    y: clamp(y, b.minDy, b.maxDy)
  }
}

const snapToEdges = (x, y) => {
  const b = getBounds()
  const left = b.baseLeft + x
  const top = b.baseTop + y
  const rightGap = b.vw - (left + b.w)
  const bottomGap = b.vh - (top + b.h)

  let targetLeft = left
  let targetTop = top

  if (left < EDGE_SNAP_THRESHOLD) targetLeft = VIEWPORT_MARGIN
  if (rightGap < EDGE_SNAP_THRESHOLD) targetLeft = b.vw - VIEWPORT_MARGIN - b.w
  if (top < EDGE_SNAP_THRESHOLD) targetTop = VIEWPORT_MARGIN
  if (bottomGap < EDGE_SNAP_THRESHOLD) targetTop = b.vh - VIEWPORT_MARGIN - b.h

  return {
    x: targetLeft - b.baseLeft,
    y: targetTop - b.baseTop
  }
}

const cubicBezier = (p1x, p1y, p2x, p2y) => {
  const cx = 3 * p1x
  const bx = 3 * (p2x - p1x) - cx
  const ax = 1 - cx - bx

  const cy = 3 * p1y
  const by = 3 * (p2y - p1y) - cy
  const ay = 1 - cy - by

  const sampleX = (t) => ((ax * t + bx) * t + cx) * t
  const sampleY = (t) => ((ay * t + by) * t + cy) * t
  const sampleDX = (t) => (3 * ax * t + 2 * bx) * t + cx

  const solveT = (x) => {
    let t = x
    for (let i = 0; i < 8; i++) {
      const x2 = sampleX(t) - x
      if (Math.abs(x2) < 1e-6) return t
      const d = sampleDX(t)
      if (Math.abs(d) < 1e-6) break
      t = t - x2 / d
    }
    let t0 = 0
    let t1 = 1
    t = x
    while (t0 < t1) {
      const x2 = sampleX(t)
      if (Math.abs(x2 - x) < 1e-6) return t
      if (x > x2) t0 = t
      else t1 = t
      t = (t1 - t0) * 0.5 + t0
    }
    return t
  }

  return (x) => sampleY(solveT(x))
}

const ease = cubicBezier(0.25, 0.1, 0.25, 1.0)

const cancelDragAnim = () => {
  if (dragAnimRaf) cancelAnimationFrame(dragAnimRaf)
  dragAnimRaf = null
}

const animateDragTo = (toX, toY) => {
  cancelDragAnim()
  const fromX = dx.value
  const fromY = dy.value

  const overX = toX + (toX - fromX) * 0.05
  const overY = toY + (toY - fromY) * 0.05

  dragAnimStart = performance.now()
  dragAnimFromX = fromX
  dragAnimFromY = fromY
  dragAnimOverX = overX
  dragAnimOverY = overY
  dragAnimToX = toX
  dragAnimToY = toY

  const duration = 300
  const split = 0.72

  const step = (now) => {
    const t = clamp((now - dragAnimStart) / duration, 0, 1)
    if (t < split) {
      const tt = ease(t / split)
      dx.value = dragAnimFromX + (dragAnimOverX - dragAnimFromX) * tt
      dy.value = dragAnimFromY + (dragAnimOverY - dragAnimFromY) * tt
    } else {
      const tt = ease((t - split) / (1 - split))
      dx.value = dragAnimOverX + (dragAnimToX - dragAnimOverX) * tt
      dy.value = dragAnimOverY + (dragAnimToY - dragAnimOverY) * tt
    }
    if (t < 1) {
      dragAnimRaf = requestAnimationFrame(step)
    } else {
      dragAnimRaf = null
      dx.value = dragAnimToX
      dy.value = dragAnimToY
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify({ dx: dx.value, dy: dy.value }))
      } catch {}
    }
  }
  dragAnimRaf = requestAnimationFrame(step)
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
  if (dragging.value) {
    const nextX = dragStartDx.value + (e.clientX - dragStartClientX.value)
    const nextY = dragStartDy.value + (e.clientY - dragStartClientY.value)
    const clampedPos = clampToViewport(nextX, nextY)
    dx.value = clampedPos.x
    dy.value = clampedPos.y
    return
  }
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
  if (dragging.value) return
  driveTo(0, 0, 0, 0)
}

const onPointerDown = (event) => {
  const el = islandEl.value
  if (!el) return
  if (event && event.target && event.target.closest && event.target.closest('.el-button')) return
  pressed.value = true
  if (event && typeof event.pointerId === 'number') {
    dragging.value = true
    dragPointerId.value = event.pointerId
    dragStartClientX.value = event.clientX
    dragStartClientY.value = event.clientY
    dragStartDx.value = dx.value
    dragStartDy.value = dy.value
    cancelDragAnim()
    try { el.setPointerCapture(event.pointerId) } catch {}
  }
}

const onPointerUp = (event) => {
  pressed.value = false
  if (!dragging.value) return
  dragging.value = false
  if (event && typeof event.pointerId === 'number') {
    try { islandEl.value && islandEl.value.releasePointerCapture(event.pointerId) } catch {}
  }
  const clampedPos = clampToViewport(dx.value, dy.value)
  const snapped = snapToEdges(clampedPos.x, clampedPos.y)
  const target = clampToViewport(snapped.x, snapped.y)
  animateDragTo(target.x, target.y)
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
  cancelDragAnim()
  window.removeEventListener('resize', handleResize)
})

const handleResize = () => {
  const clampedPos = clampToViewport(dx.value, dy.value)
  dx.value = clampedPos.x
  dy.value = clampedPos.y
}

onMounted(() => {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
    if (saved && typeof saved.dx === 'number' && typeof saved.dy === 'number') {
      dx.value = saved.dx
      dy.value = saved.dy
    }
  } catch {}
  requestAnimationFrame(() => {
    const clampedPos = clampToViewport(dx.value, dy.value)
    dx.value = clampedPos.x
    dy.value = clampedPos.y
  })
  window.addEventListener('resize', handleResize, { passive: true })
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
  top: 12px;
  left: 50%;
  z-index: 1000;
  pointer-events: none;
  opacity: 1;
  transition: opacity 120ms ease;
}

.island-wrapper.dragging {
  opacity: 0.8;
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
  touch-action: none;
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
