<script setup></script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header style="background-color: white;">
        
      </el-header>

      <el-main style="background-color: rgb(224, 226, 228);display: flex; height: 100vh;">Main</el-main>
      
      <!-- 脱离当前布局，固定在屏幕正下方 -->
      <el-footer style="position: fixed; left: 50%; bottom: 10vh; transform: translateX(-50%);">
        <div style="text-align: center;">
          <el-text type="danger">Test<br>version:0.0.1<br>2025-11-30<br>by W7insvnter(冬七柒) & TraeCN</el-text>
        </div>
      </el-footer>
      
    </el-container>
  </div>
</template>

<style scoped></style>
