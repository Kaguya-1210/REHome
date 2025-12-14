<script setup>
import { ref } from 'vue'
import { logout as apiLogout } from '@/services/auth'
const avatarSize = 50;

const logout = async () => {
  try {
    await apiLogout()
  } catch (err) {
    console.log('退出登录失败', err)
  }
}


</script>

<template>
  <div class="common-layout">
    <el-container>
      <!-- 页面头部，包含Logo -->
      <el-header class="login-header">
        <div class="logo" >
          <el-text type="primary" class="logo-text">REHomeAdmin</el-text>
        </div>
        <div style="position: absolute; right: 2%;"> 
            <el-dropdown trigger="click">
                    <el-avatar :size="avatarSize" />
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-card class="dropdown-card" body-style="padding: 0;" header-class="card-header" footer-class="card-footer">
                            <template #header>
                                <div class="name">
                                    <el-text size="large">W7insvnter</el-text>
                                </div>
                            </template>
                            <div class="content">
                              <el-text>占位</el-text>
                            </div>
                            <template #footer >
                                <div style="display: flex; justify-content: center; align-items: center;">
                                  <el-button type="info" round size="lagre" @click="logout">退出登录</el-button>
                                </div>
                            </template>
                        </el-card>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
      </el-header>
      <el-main>

      </el-main>
    </el-container>
  </div>
</template>


<style scoped>
    .login-header {
        position: relative;
  background-color: white;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加轻微阴影增加层次感 */
  display: flex;
  align-items: center;
  height:10vh;
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
.dropdown-card {
    width: 200px;
    height: 100%;
}
.el-dropdown-menu{
    padding: 0;
}
.name{
  display: flex;
  justify-content: center;
  align-items: center;
}
:deep(.card-header) {
  border-bottom: none;
}
:deep(.card-footer) {
  border-top: none;
}
.content{
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
