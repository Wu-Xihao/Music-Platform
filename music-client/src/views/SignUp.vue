<template>
  <el-container class="login-container">
    <div class="login-box">
      <!-- 头部区域 -->
      <el-header class="login-header">
        <div class="header-logo">
          <el-icon class="logo-icon"><Headset /></el-icon>
          <span class="logo-text">音乐平台</span>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="login-main">
        <h2 class="login-title">账号登录</h2>

        <!-- 快捷登录 -->
        <div class="quick-login">
          <p class="login-desc">快捷登录</p>
          <div class="button-group">
            <el-button class="social-btn qq-btn" icon="qq" @click="handleThirdLogin('qq')"></el-button>
            <el-button class="social-btn wx-btn" icon="weixin" @click="handleThirdLogin('weixin')"></el-button>
            <el-button class="social-btn email-btn" icon="message" @click="handleEmail"></el-button>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="separator">
          <span class="line"></span>
          <span class="text">或使用账号密码</span>
          <span class="line"></span>
        </div>

        <!-- 登录表单 -->
        <el-form
            ref="signInForm"
            :model="registerForm"
            :rules="SignInRules"
            class="login-form"
        >
          <el-form-item prop="username" class="form-item">
            <el-input
                placeholder="用户名/手机号/邮箱"
                v-model="registerForm.username"
                class="form-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password" class="form-item">
            <el-input
                type="password"
                placeholder="请输入密码"
                v-model="registerForm.password"
                class="form-input"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-button
              type="primary"
              @click="handleLoginIn"
              class="login-submit"
              :loading="loginLoading"
          >
            登录
          </el-button>
        </el-form>
      </el-main>

      <!-- 底部操作区 -->
      <el-footer class="login-footer">
        <div class="footer-links">
          <el-button type="text" class="link-btn" @click="handleSignUp">注册账号</el-button>
          <span class="split">|</span>
          <el-button type="text" class="link-btn" @click="handleForgotPassword">忘记密码</el-button>
        </div>
      </el-footer>
    </div>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance, ref } from "vue";
import { Headset, Message, User, Lock } from "@element-plus/icons-vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { NavName, RouterName, SignInRules } from "@/enums";

export default defineComponent({
  components: {
    Headset,
    // Message,
    User,
    Lock
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();
    const loginLoading = ref(false);

    // 登录表单数据
    const registerForm = reactive({
      username: "",
      password: "",
    });

    // 登录处理
    async function handleLoginIn() {
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) canRun = false;
      });
      if (!canRun) return;

      try {
        loginLoading.value = true;
        const result = (await HttpManager.signIn({
          username: registerForm.username,
          password: registerForm.password
        })) as ResponseBody;

        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          // 存储用户信息
          proxy.$store.commit("setUserId", result.data[0].id);
          proxy.$store.commit("setUsername", result.data[0].username);
          proxy.$store.commit("setUserPic", result.data[0].avator);
          proxy.$store.commit("setToken", true);
          // 跳转首页
          changeIndex(NavName.Home);
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
        (proxy as any).$message.error("登录失败，请检查账号密码");
      } finally {
        loginLoading.value = false;
      }
    }

    // 第三方登录
    function handleThirdLogin(type: string) {
      (proxy as any).$message.info(`${type}登录功能开发中`);
    }

    // 注册跳转
    function handleSignUp() {
      routerManager(RouterName.SignUp, { path: RouterName.SignUp });
    }

    // 忘记密码跳转
    function handleForgotPassword() {
      routerManager(RouterName.ForgotPassword, { path: RouterName.ForgotPassword });
    }

    // 邮箱登录跳转
    function handleEmail() {
      routerManager(RouterName.loginByemail, { path: RouterName.loginByemail });
    }

    return {
      registerForm,
      SignInRules,
      loginLoading,
      handleLoginIn,
      handleThirdLogin,
      handleForgotPassword,
      handleEmail,
      handleSignUp,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

// 整体容器
.login-container {
  min-height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e5e9f2 100%);
  padding: 16px;
}

// 登录卡片
.login-box {
  width: 100%;
  max-width: 360px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

// 头部区域
.login-header {
  padding: 20px 0;
  text-align: center;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);

  .header-logo {
    display: flex;
    align-items: center;
    justify-content: center;

    .logo-icon {
      font-size: 24px;
      color: #fff;
      margin-right: 8px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: 600;
      color: #fff;
    }
  }
}

// 主内容区
.login-main {
  padding: 24px 24px 16px;
  flex: 1;

  .login-title {
    text-align: center;
    font-size: 18px;
    font-weight: 500;
    color: #334155;
    margin: 0 0 16px;
  }

  // 快捷登录
  .quick-login {
    margin-bottom: 16px;

    .login-desc {
      text-align: center;
      font-size: 12px;
      color: #64748b;
      margin-bottom: 12px;
    }

    .button-group {
      display: flex;
      justify-content: center;
      gap: 16px;
    }

    .social-btn {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      padding: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      border: none;
      transition: all 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }
    }

    .qq-btn {
      background-color: #1da1f2;
      color: #fff;
    }

    .wx-btn {
      background-color: #25d366;
      color: #fff;
    }

    .email-btn {
      background-color: #f59e0b;
      color: #fff;
    }
  }

  // 分隔线
  .separator {
    display: flex;
    align-items: center;
    margin: 16px 0;

    .line {
      flex: 1;
      height: 1px;
      background-color: #e2e8f0;
    }

    .text {
      padding: 0 12px;
      font-size: 11px;
      color: #94a3b8;
    }
  }

  // 登录表单
  .login-form {
    width: 100%;

    .form-item {
      margin-bottom: 12px;
    }

    .form-input {
      height: 40px;
      border-radius: 6px;
      border-color: #e2e8f0;
      font-size: 13px;

      &:focus-within {
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
      }
    }

    .login-submit {
      width: 100%;
      height: 40px;
      font-size: 14px;
      border-radius: 6px;
      background-color: #3b82f6;
      border: none;
      transition: all 0.2s ease;

      &:hover {
        background-color: #2563eb;
      }

      &:active {
        background-color: #1d4ed8;
      }
    }
  }
}

// 底部区域
.login-footer {
  padding: 12px 0;
  background-color: #f8fafc;
  border-top: 1px solid #f1f5f9;

  .footer-links {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .link-btn {
    color: #3b82f6;
    font-size: 12px;
    padding: 4px 8px;
    transition: color 0.2s ease;

    &:hover {
      color: #2563eb;
      background-color: transparent;
    }
  }

  .split {
    color: #cbd5e1;
    margin: 0 4px;
  }
}

// 响应式适配
@media (max-width: 480px) {
  .login-box {
    max-width: 320px;
  }

  .login-main {
    padding: 20px 16px 12px;
  }

  .social-btn {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
}
</style>