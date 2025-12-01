<script setup></script>

<template>
  <div class="app-container">
    <router-view/>
    
    <!-- 全局页脚 - 脱离当前布局，固定在屏幕正下方 -->
    <el-footer style="position: fixed; left: 50%; bottom: 10vh; transform: translateX(-50%);">
      <div style="text-align: center;">
        <el-text type="danger">Test<br>version:0.0.1<br>2025-11-30<br>by W7insvnter(冬七柒) & TraeCN</el-text>
      </div>
    </el-footer>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  padding-bottom: 0px; /* 为固定页脚留出空间 */
}
</style>
