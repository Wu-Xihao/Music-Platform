<template>
  <el-container id="appSend" class="changePassword-container">
    <div class="ChangePassword_Box">
      <el-header class="change-header">
        <h1>修改密码</h1>
      </el-header>
      <el-main class="change-main">
        <el-form @submit.prevent="handleSubmit">
          <el-form-item label="邮箱：" prop="email">
            <el-input id="email" v-model="email" type="email" required />
            <el-button @click="sendVerificationCode">发送验证码</el-button>
          </el-form-item>
          <el-form-item label="验证码：" prop="code">
            <el-input id="code" v-model="code" type="text" required />
          </el-form-item>

          <div class="newPassword">
            <el-form-item label="新密码：" prop="password">
              <el-input id="password" v-model="password" type="password" required />
            </el-form-item>
            <el-form-item label="确认密码：" prop="confirmPassword">
              <el-input id="confirmPassword" v-model="confirmPassword" type="password" required />
            </el-form-item>
            <el-form-item>
              <el-button @click="handleSubmit" type="submit">提交</el-button>
              <el-button @click="goBackLogin()">取消</el-button>
            </el-form-item>
          </div>
        </el-form>
        <el-footer class="login-footer">

        </el-footer>
      </el-main>
    </div>
  </el-container>
</template>


<style>
.changePassword-container {
  box-sizing: border-box;
  width: 100%;
  height: 100vh;
  padding-top: 5%;
  background-position: center;
  background-repeat: no-repeat;
  justify-content: center;
  align-items: center;
}

/* 修改登录框样式 */
.ChangePassword_Box {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid #ebeef5;
  width: auto;
  padding: 10px;
  position: relative;
}

.change-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  font-weight: 600;
  color: #606266;
  user-select: none;
}

.change-main {
  display: flex;
  padding: 12px 25px;
}
.el-form {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.el-form-item {
  margin: 8px 0;
}

:deep(.el-form-item .el-input__validateIcon) {
  color: #cc0005;
}

:deep(.el-form-item.is-error .el-input__validateIcon) {
  color: #f56c6c;
}
@media (max-width: 800px) {
  .change-main{
    flex-direction: column;
    align-items: center;
  }
}
</style>

<script>
import axios from 'axios';
import {RouterName} from "@/enums";
import mixin from "@/mixins/mixin";
export default {
  data() {

    return {
      email: "",
      code: "",
      password: "",
      confirmPassword: ""
    };
  },
  methods: {
    async sendVerificationCode() {
      try {
        const email =document.getElementById('email').value;
        const response = await axios.get('http://localhost:8888/user/sendVerificationCode',({params: {
            email: email
          }}));
        console.log(response.data);
        this.$message({
          message: response.data,
          type: 'success'
        });
      } catch (error) {
        console.error('Error submitting email:');
        this.$message({
          message: 'response.data',
          type: 'error'
        });
      }
    },
    async goBackLogin() {
      this.routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    },
    async handleSubmit() {
      try {
        const email =document.getElementById('email').value;
        const code=document.getElementById('code').value
        const password=document.getElementById('password').value
        const confirmPassword=document.getElementById('confirmPassword').value
        const data = {
          email: email,
          code: code,
          password: password,
          confirmPassword: confirmPassword
        };
        const response = await axios.post('http://localhost:8888/user/resetPassword', data);
        console.log(response.data);
        this.$message({
          message: response.data,
          type: 'success'
        });
      } catch (error) {
        this.$message({
          message: 'response.data',
          type: 'error'
        });
      }
    }
  },
  created(){
    const { routerManager} = mixin();
    this.routerManager=routerManager;
  }
};
</script>
