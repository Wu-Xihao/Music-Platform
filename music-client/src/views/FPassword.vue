<template>
  <el-container class="changePassword-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration"></div>

    <div class="changePassword-box">
      <!-- 修改密码框头部 -->
      <el-header class="change-header">
        <div class="logo">
          <h1>修改密码</h1>
        </div>
      </el-header>

      <!-- 修改密码主体内容 -->
      <el-main class="change-main">
        <el-form @submit.prevent="handleSubmit" ref="passwordForm" label-width="70px" status-icon :model="formData" :rules="passwordRules">
          <!-- 邮箱输入框和发送验证码按钮 -->
          <el-form-item prop="email" label="邮箱">
            <el-row>
              <el-col :span="18">
                <el-input v-model="formData.email" type="email" placeholder="邮箱"></el-input>
              </el-col>
              <el-col :span="6">
                <el-button @click="sendVerificationCode" style="margin-left: 15px;">发送验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item prop="code" label="验证码">
            <el-input v-model="formData.code" type="text" placeholder="验证码"></el-input>
          </el-form-item>

          <div class="newPassword">
            <el-form-item prop="password" label="新密码">
              <el-input v-model="formData.password" type="password" placeholder="新密码"></el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword" label="确认">
              <el-input v-model="formData.confirmPassword" type="password" placeholder="确认密码"></el-input>
            </el-form-item>
            <el-form-item class="sign-btn">
              <div class="button-group">
                <el-button @click="goBackLogin()" class="cancel-btn">取消</el-button>
                <el-button @click="handleSubmit" type="primary" class="confirm-btn">提交</el-button>
              </div>
            </el-form-item>
          </div>
        </el-form>
      </el-main>
    </div>
  </el-container>
</template>

<style lang="scss" scoped>
.changePassword-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  position: relative;

  // 背景装饰
  .bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image:
        radial-gradient(circle at 20% 30%, rgba(64, 158, 255, 0.1) 0%, transparent 40%),
        radial-gradient(circle at 80% 70%, rgba(64, 158, 255, 0.1) 0%, transparent 40%);
    z-index: 0;
  }

  .changePassword-box {
    width: 100%;
    max-width: 500px; // 增加最大宽度以容纳更多内容
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    position: relative;
    z-index: 1;
    transition: transform 0.3s ease;
    margin: 20px;
    top: -80px;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
    }
  }

  .change-header {
    padding: 30px 20px;
    background: linear-gradient(90deg, #3b82f6, #8b5cf6);
    color: #fff;
    text-align: center;
    position: relative;

    .logo {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 15px;
      position: relative;
      top: -15px;

      h1 {
        font-size: 26px; // 增大标题字号
        margin: 0;
        font-weight: 600;
      }
    }
  }

  .change-main {
    padding: 30px 40px;

    // 标签样式调整
    .el-form-item__label {
      font-weight: 500;
      color: #4a5568;
    }

    // 输入框统一大小
    .el-input {
      margin-top: 5px;
    }

    .el-input__inner {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }

    // 统一 el-form-item 的底部间距
    .el-form-item {
      margin-bottom: 20px;
    }
  }

  // 按钮区域样式
  .newPassword {
    .el-form-item {
      margin-bottom: 0;
    }

    .el-button {
      height: 44px;
      font-size: 16px;
      border-radius: 8px;
      transition: all 0.3s ease;
      margin-right: 20px;

      &.el-button--primary {
        background: linear-gradient(90deg, #3b82f6, #8b5cf6);
        border: none;
        box-shadow: 0 4px 12px rgba(123, 97, 255, 0.25);

        &:hover {
          background: linear-gradient(90deg, #2563eb, #7c3aed);
          box-shadow: 0 6px 14px rgba(123, 97, 255, 0.35);
          transform: translateY(-2px);
        }
      }

      &:not(.el-button--primary) {
        background-color: #f8fafc;
        border: 1px solid #e2e8f0;
        color: #4a5568;

        &:hover {
          background-color: #edf2f7;
          border-color: #cbd5e0;
          color: #2d3748;
        }
      }
    }
  }
}

.button-group {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  width: 100%;
  margin-top: 15px;
  margin-right: 60px;

  .cancel-btn, .confirm-btn {
    flex: 1;
    height: 44px;
    font-size: 16px;
    border-radius: 8px;
    transition: all 0.3s ease;
    text-align: center;
  }

  .cancel-btn {
    background-color: #f8fafc;
    border: 1px solid #e2e8f0;
    color: #4a5568;

    &:hover {
      background-color: #edf2f7;
      border-color: #cbd5e0;
      color: #2d3748;
    }
  }

  .confirm-btn {
    background: linear-gradient(90deg, #3b82f6, #8b5cf6);
    border: none;
    box-shadow: 0 4px 12px rgba(123, 97, 255, 0.25);
    color: white;

    &:hover {
      background: linear-gradient(90deg, #2563eb, #7c3aed);
      box-shadow: 0 6px 14px rgba(123, 97, 255, 0.35);
      transform: translateY(-2px);
    }
  }
}

// 响应式调整
@media (max-width: 576px) {
  .changePassword-container {
    padding: 15px;
  }

  .changePassword-box {
    width: 100%;
    max-width: 100%;
  }

  .change-main {
    padding: 20px;

    // 标签宽度在小屏幕上调整
    :deep(.el-form-item__label) {
      width: auto !important;
      text-align: left;
      padding-right: 12px;
      margin-bottom: 6px;
    }

    // 表单元素在小屏幕上100%宽度
    .el-input {
      width: 100%;
    }
  }
}
</style>

<script>
import axios from 'axios';
import { RouterName } from "@/enums";
import mixin from "@/mixins/mixin";

export default {
  data() {
    return {
      formData: {
        email: "",
        code: "",
        password: "",
        confirmPassword: ""
      },
      passwordRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.formData.password) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ]
      }
    };
  },
  methods: {
    async sendVerificationCode() {
      try {
        const { email } = this.formData;
        const response = await axios.get('http://localhost:8888/user/sendVerificationCode', {
          params: {
            email
          }
        });
        console.log(response.data);
        this.$message({
          message: response.data,
          type: 'success'
        });
      } catch (error) {
        console.error('Error submitting email:');
        this.$message({
          message: '发送验证码失败',
          type: 'error'
        });
      }
    },
    async goBackLogin() {
      this.routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    },
    async handleSubmit() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          try {
            const { email, code, password, confirmPassword } = this.formData;
            const data = {
              email,
              code,
              password,
              confirmPassword
            };
            const response = await axios.post('http://localhost:8888/user/resetPassword', data);
            console.log(response.data);
            this.$message({
              message: response.data,
              type: 'success'
            });
          } catch (error) {
            this.$message({
              message: '修改密码失败',
              type: 'error'
            });
          }
        }
      });
    }
  },
  created() {
    const { routerManager } = mixin();
    this.routerManager = routerManager;
  }
};
</script>