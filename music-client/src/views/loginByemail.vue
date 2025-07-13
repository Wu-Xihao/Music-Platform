<template>
  <div class="login-container">
    <!-- 左侧图片部分 -->
    <div class="left-image">
      <img src="@/views/music7.jpg" alt="Music Image" class="full-image">
    </div>
    <!-- 右侧登录表单部分 -->
    <div class="right-form">
      <yin-login-logo></yin-login-logo>
      <div class="sign">
        <div class="sign-head">
          <span>邮箱登录</span>
        </div>
        <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules">
          <el-form-item prop="email">
            <el-input placeholder="邮箱" v-model="registerForm.email" class="rotate-in transparent-input"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn" class="rotate-in transparent-input"></el-input>
          </el-form-item>
          <el-form-item class="sign-btn">
            <el-button type="primary" @click="handleLoginIn">登录</el-button>
            <el-button type="primary" @click="handleLoginCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
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
.login-container {
  display: flex;
  min-height: 100vh;
  background-color: #f4f4f9;

  .left-image {
    flex: 1;
    overflow: hidden;

    .full-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .right-form {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    /* 添加背景图片 */
    background-image: url('music8.jpg');
    background-size: cover;
    background-position: center;
  }

  .sign {
    /* 重新定义 sign 的样式 */
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 100px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    padding: 150px;
    width: 300px;
    animation: fadeIn 0.5s ease-in-out;

    .sign-head {
      text-align: center;
      margin-bottom: 20px;

      span {
        font-size: 24px;
        font-weight: bold;
        color: white;
      }
    }

    .sign-btn {
      display: flex;
      justify-content: space-between;
      margin-top: 50px;

      button {
        width: 48%;
        transition: background-color 0.3s ease;
        color: white;

        &:hover {
          background-color: #05ede4;
          border-color: #05ede4;
        }
      }
    }

    .rotate-in {
      animation: rotateIn 0.5s ease-in-out;
    }

    .transparent-input {
      background-color: transparent;
      border: 1px solid white;
      color: white;
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