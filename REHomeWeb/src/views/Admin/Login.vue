<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock, ChatSquare } from '@element-plus/icons-vue'
import axios from '@/services/http'
import { login } from '@/services/auth'
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()
// 表单引用，用于表单验证和控制
const formRef = ref()

// 登录表单数据模型
const form = ref({
  account: '',
  password: '',
  captcha: ''
})
const captchaSrc = ref(`${axios.defaults.baseURL}/captcha`)

// 表单验证规则
const rules = {
  account: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为 4 个字符', trigger: 'blur' }
  ]
}

/**
 * 处理登录表单提交
 * 1. 验证表单
 * 2. 执行登录逻辑
 * 3. 处理登录结果
 */
const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 模拟登录请求
      console.log('登录表单验证通过，提交数据:', form.value)
      // 这里可以添加实际的登录API调用
      login({ account: form.value.account, password: form.value.password, code: form.value.captcha })
        .then(res => {
          const ok = res?.data?.code === 2000
          if (ok) {
            const user = res?.data?.data?.user || null
            if (user) auth.setUser(user)
            window.location.href = '/admin/home'
          } else {
            ElMessage.error('登录失败，请检查用户名和密码')
          }
        })
    } else {
      console.log('登录表单验证失败')
      ElMessage.error('请检查表单输入')
    }
  })
}

/**
 * 刷新验证码
 * 实际项目中这里应该调用后端API获取新的验证码
 */
const refreshCaptcha = () => {
  captchaSrc.value = `${BASE_URL}/captcha?ts=${Date.now()}`
}
</script>

<template>
  <div class="admin-login-layout"> <!-- 登录页面整体布局容器 -->
    <!-- Element Plus 容器组件 -->
    <el-container class="login-container">
      <!-- 页面头部，包含Logo -->
      <el-header class="login-header">
        <div class="logo" >
          <el-text type="primary" class="logo-text">REHomeAdmin</el-text>
        </div>
      </el-header>
      
      <!-- 页面主体，包含登录表单 -->
      <el-main class="login-main">
        <!-- 登录卡片容器，使用绝对定位放置在右侧 -->
        <div class="login-card-wrapper" >
          <!-- 登录卡片 -->
          <el-card class="login-card">
            <!-- 卡片头部标题 -->
            <template #header>
              <div class="card-header">
                <el-text type="primary" class="login-title">管理员登录</el-text>
              </div>
            </template>
            
            <!-- 登录表单 -->
            <el-form 
              ref="formRef" 
              :model="form" 
              :rules="rules"
              class="login-form"
            >
              <!-- 用户名输入框 -->
              <el-form-item prop="account">
                <el-input 
                  v-model="form.account" 
                  type="text" 
                  placeholder="请输入用户名"
                >
                  <template #prepend>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <!-- 密码输入框 -->
              <el-form-item prop="password">
                <el-input 
                  v-model="form.password" 
                  type="password" 
                  :show-password="true" 
                  placeholder="请输入密码"
                >
                  <template #prepend>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <!-- 验证码输入框 -->
              <el-form-item prop="captcha" class="captcha-item">
                <el-input 
                  v-model="form.captcha" 
                  placeholder="请输入验证码"
                  class="captcha-input"
                >
                  <template #prepend>
                    <el-icon><ChatSquare /></el-icon>
                  </template>
                </el-input>
                <!-- 验证码图片区域 -->
                <div class="captcha-image" @click="refreshCaptcha">
                  <img :src="captchaSrc" alt="验证码" style="width: 150px; height: 50px;" />
                </div>
              </el-form-item>
              
              <!-- 登录按钮 -->
              <div class="login-button-wrapper">
                <el-button type="primary" round @click="handleLogin" class="login-button">
                  登录
                </el-button>
              </div>
            </el-form>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
/* 登录页面整体布局样式 */
.admin-login-layout {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

/* Element Plus 容器样式 */
.login-container {
  height: 100vh;
}

/* 页面头部样式 */
.login-header {
  background-color: white;
  height: 60px; /* 使用固定高度而不是百分比，更稳定 */
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加轻微阴影增加层次感 */
  display: flex;
  align-items: center;
}

/* Logo 样式 */
.logo {
  height: 100%;
  display: flex;
  align-items: center;
}

/* Logo 文字样式 */
.logo-text {
  font-size: 24px; /* 使用具体像素而不是百分比，更精确 */
  font-weight: bold;
}

/* 页面主体样式 */
.login-main {
  background-color: #f5f7fa; /* 使用更柔和的背景色 */
  height: calc(100vh - 60px); /* 计算高度，减去头部高度 */
  position: relative;
  padding: 0;
  margin: 0;
}

/* 登录卡片容器样式 */
.login-card-wrapper {
  position: absolute;
  display: flex;
  justify-content: center; 
  align-items: center;
  width: 30%;
  height: 55%;
  right: 10%; /* 从5%调整到10%，增加右侧边距 */
  bottom: 25%;
}

/* 登录卡片样式 */
.login-card {
  width: 100%;
  max-width: 400px; /* 添加最大宽度限制，响应式更好 */
  height: 100%;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 增加阴影，更美观 */
  background-color: white;
  overflow: hidden;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: center;
  padding: 0;
}

/* 登录标题样式 */
.login-title {
  font-size: 20px; /* 使用具体像素而不是百分比 */
  font-weight: bold;
}

/* 登录表单样式 */
.login-form {
  width: 90%;
  margin: 0 auto;
  padding: 20px 0;
}

/* 验证码输入框容器样式 */
.captcha-item {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 验证码输入框样式 */
.captcha-input {
  flex: 1;
}

/* 验证码图片样式 */
.captcha-image {
  width: 150px;
  height: 50px;
  background-color: #f0f2f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

/* 验证码图片悬停效果 */
.captcha-image:hover {
  background-color: #e6e8eb;
}

/* 登录按钮容器样式 */
.login-button-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 登录按钮样式 */
.login-button {
  width: 60%;
  font-size: 16px;
  height: 40px;
}

/* 响应式设计 - 适配不同屏幕尺寸 */
@media (max-width: 1200px) {
  .login-card-wrapper {
    width: 40%;
    right: 5%;
  }
}

@media (max-width: 768px) {
  .login-card-wrapper {
    position: static;
    width: 90%;
    height: auto;
    margin: 0 auto;
    padding: 20px 0;
  }
  
  .login-main {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .login-card {
    height: auto;
    max-width: none;
  }
}
</style>
