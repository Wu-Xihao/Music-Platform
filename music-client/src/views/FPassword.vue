<template>
  <el-container id="appSend">
    <el-header>
      <h1>修改密码</h1>
    </el-header>
    <el-main>
      <!-- 使用 transition 组件添加动画效果 -->
      <transition name="scale-in">
        <el-form v-if="showForm" @submit.prevent="handleSubmit">
          <el-form-item label="邮箱：" prop="email">
            <el-input id="email" v-model="email" type="email" required placeholder="请输入邮箱" />
            <el-button @click="sendVerificationCode" class="gradient-button">发送验证码</el-button>
          </el-form-item>
          <el-form-item label="验证码：" prop="code">
            <el-input id="code" v-model="code" type="text" required placeholder="请输入验证码" />
          </el-form-item>
          <el-form-item label="新密码：" prop="password">
            <el-input id="password" v-model="password" type="password" required placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码：" prop="confirmPassword">
            <el-input id="confirmPassword" v-model="confirmPassword" type="password" required placeholder="请再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button @click="handleSubmit" type="submit" class="gradient-button">提交</el-button>
          </el-form-item>
        </el-form>
      </transition>
    </el-main>
  </el-container>
</template>

<style scoped>
#appSend {
  max-width: 400px;
  margin: 0 auto;
  /* 修改背景为渐变色 */
  background: linear-gradient(to bottom, #ea9cb6, #06f6f6);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.el-header h1 {
  color: #ffffff;
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.el-form-item__label {
  /* 将表单标签颜色改为白色 */
  color: #ffffff;
}

.el-input {
  background-color: #282828;
  border: none;
  color: #fff;
  border-radius: 5px;
}

.el-input::placeholder {
  color: #b3b3b3;
}

.gradient-button {
  background: linear-gradient(to right, #10d6df, #1aea0b);
  border: none;
  color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.gradient-button:hover {
  background: linear-gradient(to right, #1ED760, #1DB954);
  transform: translateY(-2px);
}

.el-message {
  z-index: 9999;
}

/* 定义缩放动画 */
.scale-in-enter-active {
  animation: scaleIn 0.5s ease;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      email: "",
      code: "",
      password: "",
      confirmPassword: "",
      showForm: false // 新增一个变量来控制表单的显示与隐藏
    };
  },
  mounted() {
    // 移除延迟显示逻辑
    this.showForm = true;
  },
  methods: {
    async sendVerificationCode() {
      try {
        const email = document.getElementById('email').value;
        const response = await axios.get('http://localhost:8888/user/sendVerificationCode', {
          params: {
            email: email
          }
        });
        console.log(response.data);
        this.$message({
          message: response.data,
          type: 'success'
        });
      } catch (error) {
        console.error('Error submitting email:', error);
        this.$message({
          message: '发送验证码失败',
          type: 'error'
        });
      }
    },
    async handleSubmit() {
      try {
        const email = document.getElementById('email').value;
        const code = document.getElementById('code').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
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
        console.error('Error submitting form:', error);
        this.$message({
          message: '提交表单失败',
          type: 'error'
        });
      }
    }
  },
};
</script>