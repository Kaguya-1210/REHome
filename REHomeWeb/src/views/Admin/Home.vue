<script setup>

const avatarSize = 50;

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
            <el-dropdown trigger="hover">
                    <el-avatar :size="avatarSize" />
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-card class="dropdown-card">
                            <template #header>
                                <div class="card-header">
                                    UserName
                                </div>
                            </template>
                            <template #footer>
                                logout
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
    height: 200px;
}
.el-dropdown-menu{
    padding: 0;
}
</style>
