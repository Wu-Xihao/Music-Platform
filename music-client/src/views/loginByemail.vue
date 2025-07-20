<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration"></div>

    <div class="login-box">
      <!-- 登录框头部 -->
      <div class="login-header">
        <div class="logo">
          <h1>邮箱登录</h1>
        </div>
      </div>

      <!-- 登录主体内容 -->
      <div class="login-main">
        <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules" class="login-form">
          <el-form-item prop="email" class="form-item">
            <el-input placeholder="邮箱" v-model="registerForm.email" class="login-input">
              <template #prefix>
                <el-icon class="input-icon"><i class="el-icon-message"></i></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" class="form-item">
            <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn" class="login-input">
              <template #prefix>
                <el-icon class="input-icon"><i class="el-icon-lock"></i></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <div class="button-group">
            <el-button type="primary" @click="handleLoginIn" class="login-btn" :loading="loginLoading">登录</el-button>
            <el-button @click="handleLoginCancel" class="cancel-btn">取消</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from "vue";
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';
import { HttpManager } from "@/api";
import { NavName, RouterName } from "@/enums";

// 定义API响应类型
interface ApiResponse {
  success: boolean;
  message: string;
  data?: any[];
}

// 定义用户数据类型
interface UserData {
  id: number;
  username: string;
  avatar?: string;
  avator?: string;
}

// 定义表单验证规则 - 密码最小长度改为3位
const SignInRules = {
  email: [
    { required: true, message: '邮箱未输入', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '密码未输入', trigger: 'blur' },
    { min: 3, message: '密码长度不能少于3位', trigger: 'blur' }
  ]
};

export default defineComponent({
  setup() {
    const router = useRouter();
    const store = useStore();
    const signInForm = ref();

    // 登录加载状态
    const loginLoading = ref(false);

    // 登录
    const registerForm = reactive({
      email: "",
      password: "",
    });

    async function handleLoginCancel() {
      // 修复路由跳转问题
      // 使用路径跳转而不是命名路由
      router.push('/sign-in');
    }

    async function handleLoginIn() {
      try {
        // 表单验证
        const isValid = await signInForm.value.validate();
        if (!isValid) return;

        // 显示加载状态
        loginLoading.value = true;

        const email = registerForm.email;
        const password = registerForm.password;

        // 调用登录接口
        const result = await HttpManager.signInByemail({ email, password }) as ApiResponse;

        // 打印响应结果以便调试
        console.log("登录响应结果:", result);

        // 处理登录结果
        if (result.success) {
          // 存储用户信息
          const userData: UserData = result.data?.[0] || {} as UserData;

          store.commit("setUserId", userData.id);
          store.commit("setUsername", userData.username);

          // 处理用户头像 - 优先使用 avatar，如果没有则使用 avator
          const userPic = userData.avatar || userData.avator;
          if (userPic) {
            store.commit("setUserPic", userPic);
          }

          store.commit("setToken", true);

          // 显示成功消息
          ElMessage({
            message: '登录成功',
            type: 'success',
          });

          // 导航到首页 - 使用路径跳转确保可靠性
          router.push('/');
        } else {
          // 显示错误消息
          ElMessage({
            message: result.message || '登录失败，请检查邮箱和密码',
            type: 'error',
          });
        }
      } catch (error) {
        console.error("登录请求失败:", error);
        ElMessage({
          message: '登录请求失败，请重试',
          type: 'error',
        });
      } finally {
        // 隐藏加载状态
        loginLoading.value = false;
      }
    }

    return {
      registerForm,
      SignInRules,
      signInForm,
      handleLoginIn,
      handleLoginCancel,
      loginLoading
    };
  },
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  position: relative;
  width: 100%;
}

/* 背景装饰 */
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

.login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
}

.login-header {
  padding: 30px 20px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  color: #fff;
  text-align: center;
}

.login-header .logo h1 {
  font-size: 24px;
  margin: 0;
  font-weight: 600;
}

.login-main {
  padding: 30px 40px;
}

.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px;
}

.login-input {
  height: 48px;
  border-radius: 8px;
  border-color: #e5e6eb;
  transition: all 0.3s ease;
}

.login-input:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.input-icon {
  display: flex;
  align-items: center;
  padding: 0 12px;
  color: #909399;
  font-size: 18px;
  height: 70%;
  line-height: 1;
}

.button-group {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-top: 10px;
}

.login-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border: none;
  transition: all 0.3s ease;
  margin-right: 10px;
  color: #fff;
}

.login-btn:hover {
  background: linear-gradient(90deg, #2563eb, #7c3aed);
}

.cancel-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #4a5568;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background-color: #edf2f7;
  border-color: #cbd5e0;
  color: #2d3748;
}

/* 响应式调整 */
@media (max-width: 576px) {
  .login-container {
    padding: 15px;
  }

  .login-box {
    width: 100%;
  }

  .login-main {
    padding: 20px;
  }
}
</style>
