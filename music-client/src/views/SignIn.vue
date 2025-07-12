<template>
  <div class="signin-container">
    <div class="signin-background">
      <div class="signin-gradient"></div>
    </div>
    <yin-login-logo></yin-login-logo>
    <div class="sign">
      <div class="sign-head">
        <span>帐号登录</span>
      </div>
      <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules">
        <el-form-item prop="username">
          <el-input placeholder="用户名" v-model="registerForm.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn"></el-input>
        </el-form-item>
        <el-form-item class="sign-btn">
          <!-- 给每个按钮添加自定义类名 -->
          <el-button class="register-btn" @click="handleSignUp">注册</el-button>
          <el-button type="primary" class="login-btn" @click="handleLoginIn">登录</el-button>
          <el-button class="forgot-password-btn" @click="handleForgotPassword">忘记密码</el-button>
          <el-button class="email-login-btn" @click="handleEmail">邮箱登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance } from "vue";
import mixin from "@/mixins/mixin";
import YinLoginLogo from "@/components/layouts/YinLoginLogo.vue";
import { HttpManager } from "@/api";
import { NavName, RouterName, SignInRules } from "@/enums";

export default defineComponent({
  components: {
    YinLoginLogo,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    // 登录用户名密码
    const registerForm = reactive({
      username: "",
      password: "",
    });

    async function handleLoginIn() {
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      try {
        const username = registerForm.username;
        const password = registerForm.password;
        const result = (await HttpManager.signIn({ username, password })) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          proxy.$store.commit("setUserId", result.data[0].id);
          proxy.$store.commit("setUsername", result.data[0].username);
          proxy.$store.commit("setUserPic", result.data[0].avator);
          proxy.$store.commit("setToken", true);
          changeIndex(NavName.Home);
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
      }
    }

    function handleSignUp() {
      routerManager(RouterName.SignUp, { path: RouterName.SignUp });
    }

    function handleForgotPassword() {
      routerManager(RouterName.ForgotPassword, { path: RouterName.ForgotPassword });
    }
    function handleEmail() {
      routerManager(RouterName.loginByemail, { path: RouterName.loginByemail });
    }

    return {
      registerForm,
      SignInRules,
      handleLoginIn,
      handleForgotPassword,
      handleEmail,
      handleSignUp,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";

.signin-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400vh;
  position: relative;
  overflow: hidden;
}

.signin-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.signin-gradient {
  width: 100%;
  height: 100%;
  // 使用图片作为背景
  background-image: url('./music.png');
  background-size: cover;
  background-position: center;
  // 移除渐变动画
  animation: none;
}

.sign {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 99px;
  padding: 40px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
  width: 400px;
  animation: fadeIn 0.5s ease;
}

.sign-head {
  text-align: left;
  margin-bottom: 30px;

  span {
    font-size: 28px;
    font-weight: bold;
    color: #fff;
    text-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  }
}

.el-form-item {
  margin-bottom: 100px;
}

.el-input {
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;

  &::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }
}

.sign-btn {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;

  .el-button {
    border-radius: 5px;
    padding: 12px 20px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    }
  }

  // 设置每个按钮的背景颜色
  .register-btn {
    background: #ffffff;
    border: none;
    color: #000000;
  }

  .login-btn {
    background: #ffffff;
    border: none;
    color: #000000;
  }

  .forgot-password-btn {
    background: #ffffff;
    border: none;
    color: #000000;
  }

  .email-login-btn {
    background: #ffffff;
    border: none;
    color: #000000;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>