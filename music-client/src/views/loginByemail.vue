<template>
  <div class="login-container">
    <yin-login-logo></yin-login-logo>
    <div class="sign">
      <div class="sign-head">
        <span>邮箱登录</span>
      </div>
      <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules">
        <el-form-item prop="email">
          <el-input placeholder="邮箱" v-model="registerForm.email" class="rotate-in"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn" class="rotate-in"></el-input>
        </el-form-item>
        <el-form-item class="sign-btn">
          <el-button type="primary" @click="handleLoginIn">登录</el-button>
          <el-button type="primary" @click="handleLoginCancel">取消</el-button>
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

    // 登录
    const registerForm = reactive({
      email: "",
      password: "",
    });

    async function handleLoginCancel() {
      routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    async function handleLoginIn() {
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      try {
        const email = registerForm.email;
        const password = registerForm.password;
        const result = (await HttpManager.signInByemail({ email, password })) as ResponseBody;
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

    return {
      registerForm,
      SignInRules,
      handleLoginIn,
      handleLoginCancel,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";

.el-form-item {
  margin-bottom: 100px;
}

.login-container {
  display: flow;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400vh;
  background-color: #f4f4f9; // 背景颜色

  .sign {
    background-color: #fff;
    border-radius: 80px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    padding: 150px;
    width: 350px;
    animation: fadeIn 0.5s ease-in-out; // 淡入动画

    // 设置文字颜色为白色
    color: white;

    .sign-head {
      text-align: center;
      margin-bottom: 20px;

      span {
        font-size: 24px;
        font-weight: bold;
        color: white; // 确保标题文字颜色为白色
      }
    }

    .sign-btn {
      display: flex;
      justify-content: space-between;
      margin-top: 20px;

      button {
        width: 48%;
        transition: background-color 0.3s ease; // 按钮背景颜色过渡效果
        color: white; // 确保按钮文字颜色为白色

        &:hover {
          background-color: #05ede4;
          border-color: #05ede4;
        }
      }
    }

    // 输入框旋转动画
    .rotate-in {
      animation: rotateIn 0.5s ease-in-out;
    }
  }
}

// 淡入动画
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

// 旋转动画
@keyframes rotateIn {
  from {
    opacity: 0;
    transform: rotate(-90deg);
  }
  to {
    opacity: 1;
    transform: rotate(0deg);
  }
}
</style>