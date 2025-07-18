<template>
  <el-container class="login-container">
    <!-- 登录卡片 -->
    <div class="login-box">
      <!-- 顶部标题区 -->
      <el-header class="login-header">
        <div class="header-logo">
          <el-icon class="logo-icon"><Headset /></el-icon>
          <span class="logo-text">音乐平台</span>
        </div>
        <h2 class="login-title">账号登录</h2>
      </el-header>

      <!-- 主要内容区 -->
      <el-main class="login-main">
        <!-- 快捷登录方式 -->
        <div class="quick-login">
          <p class="login-desc">快捷登录</p>
          <div class="button-group">
            <el-button class="social-btn qq-btn" type="default" size="large" icon="qq">
              QQ
            </el-button>
            <el-button class="social-btn wx-btn" type="default" size="large" icon="weixin">
              微信
            </el-button>
            <el-button class="social-btn email-btn" type="default" size="large" @click="handleEmail">
              <el-icon><Message /></el-icon>
              邮箱
            </el-button>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="separator">
          <span class="line"></span>
          <span class="text">或使用账号密码登录</span>
          <span class="line"></span>
        </div>

        <!-- 账号密码登录表单 -->
        <el-form
            ref="signInForm"
            status-icon
            :model="registerForm"
            :rules="SignInRules"
            class="login-form"
        >
          <el-form-item prop="username" class="form-item">
            <el-input
                placeholder="请输入用户名"
                v-model="registerForm.username"
                prefix-icon="user"
                class="form-input"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password" class="form-item">
            <el-input
                type="password"
                placeholder="请输入密码"
                v-model="registerForm.password"
                prefix-icon="lock"
                @keyup.enter="handleLoginIn"
                class="form-input"
            ></el-input>
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
    Message,

  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();
    const loginLoading = ref(false); // 登录加载状态

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
        loginLoading.value = true; // 显示加载状态
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
        loginLoading.value = false; // 关闭加载状态
      }
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
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e5e9f2 100%);
  padding: 20px;
}

// 登录卡片
.login-box {
  width: 100%;
  max-width: 420px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
  }
}

// 头部区域
.login-header {
  padding: 30px 20px 15px;
  text-align: center;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);

  .header-logo {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 15px;

    .logo-icon {
      font-size: 32px;
      color: #fff;
      margin-right: 10px;
    }

    .logo-text {
      font-size: 24px;
      font-weight: 600;
      color: #fff;
    }
  }

  .login-title {
    color: #fff;
    font-size: 20px;
    font-weight: 500;
    margin: 0;
  }
}

// 主要内容区
.login-main {
  padding: 30px 35px;
}

// 快捷登录
.quick-login {
  margin-bottom: 25px;

  .login-desc {
    text-align: center;
    color: #64748b;
    font-size: 14px;
    margin-bottom: 15px;
  }

  .button-group {
    display: flex;
    gap: 15px;
    justify-content: center;
  }

  .social-btn {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    border: none;
    transition: all 0.3s ease;

    &:hover {
      transform: scale(1.1);
    }
  }

  .qq-btn {
    background-color: #1da1f2;
    color: #fff;

    &:hover {
      background-color: #1a91da;
    }
  }

  .wx-btn {
    background-color: #25d366;
    color: #fff;

    &:hover {
      background-color: #20be5b;
    }
  }

  .email-btn {
    background-color: #f59e0b;
    color: #fff;

    &:hover {
      background-color: #d97706;
    }
  }
}

// 分隔线
.separator {
  display: flex;
  align-items: center;
  margin: 20px 0 30px;

  .line {
    flex: 1;
    height: 1px;
    background-color: #e2e8f0;
  }

  .text {
    padding: 0 15px;
    color: #94a3b8;
    font-size: 13px;
  }
}

// 登录表单
.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px;
}

.form-input {
  height: 50px;
  border-radius: 8px;
  border-color: #e2e8f0;
  transition: all 0.3s ease;

  &:focus-within {
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
}

.login-submit {
  width: 100%;
  height: 50px;
  font-size: 16px;
  border-radius: 8px;
  background-color: #3b82f6;
  border: none;
  transition: all 0.3s ease;

  &:hover {
    background-color: #2563eb;
  }

  &:active {
    background-color: #1d4ed8;
  }
}

// 底部区域
.login-footer {
  padding: 15px 35px 25px;
  background-color: #f8fafc;
  border-top: 1px solid #f1f5f9;

  .footer-links {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .link-btn {
    color: #3b82f6;
    font-size: 14px;
    padding: 5px 10px;
    transition: color 0.2s ease;

    &:hover {
      color: #2563eb;
      background-color: transparent;
    }
  }

  .split {
    color: #cbd5e1;
    margin: 0 5px;
  }
}

// 响应式适配
@media (max-width: 480px) {
  .login-box {
    width: 100%;
    border-radius: 12px;
  }

  .login-main {
    padding: 25px 20px;
  }

  .social-btn {
    width: 50px;
    height: 50px;
    font-size: 18px;
  }
}
</style>