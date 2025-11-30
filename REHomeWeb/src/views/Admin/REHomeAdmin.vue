<script setup></script>

<template>
  <div class="common-layout"> 
 
    <el-container >
      <el-header style="background-color: white;  height: 10vh;">
        <div class="logo" >
        <el-text type="Default" style="font-size: 200%;" >REHomeAdmin</el-text>
        </div>
      </el-header>
      <el-main style="background-color: rgb(224, 226, 228); height: 90vh;position: relative;">
        <div class="loginCard" >
        <el-card style="width: 80%; height: 90%;border-radius: 10px;text-align: center;">
            <template #header style="display: flex;">
              <el-text type="Default" style="font-size: 150%;">管理员登录</el-text>
            </template>
            <el-form ref="formRef" :model="form" label-width="auto"> 
                <el-form-item>
                  <el-input v-model="input" type="account" placeholder="请输入" />
                </el-form-item>
                <el-form-item>
                  <el-input v-model="input" type="password" :show-password="true" placeholder="请输入" />
                </el-form-item>
                <el-form-item style="width: 65%;">
                  <el-input v-model="input" placeholder="请输入" />
                </el-form-item>
                <el-form-item >
                  <el-button>login</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        </div>
      </el-main>
    </el-container>
     </div>
</template>

<style scoped>
.logo {
  color: white;
  /* background-color: black; */
  height: 100%;
  width: 15%;
  display: flex;
  justify-content: center;
  padding-left: 2%;
}
.loginCard {
   position: absolute;
   display: flex;
   justify-content: center; 
   /* background-color: black; */
   width: 30%;
   height: 60%;
   right: 5%;
   bottom: 20%;
}
</style>
