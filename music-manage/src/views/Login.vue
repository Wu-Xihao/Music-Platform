<template>
  <div class="login-container" ref="loginBg">
    <div class="title">{{ nusicName }}</div>
    <div class="login">
      <el-form :model="ruleForm" :rules="rules">
        <el-form-item prop="username">
          <el-input
              v-model="ruleForm.username"
              placeholder="用户名"
              :prefix-icon="User"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              type="password"
              placeholder="密码"
              v-model="ruleForm.password"
              @keyup.enter="submitForm"
              :prefix-icon="Lock"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="login-btn" @click="submitForm">
            <span>登录</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive, onMounted, onUnmounted } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName, MUSICNAME } from "@/enums";
import * as THREE from "three";
import WAVES from "vanta/dist/vanta.waves.min";
import { User, Lock } from '@element-plus/icons-vue';

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();
    const loginBg = ref(null);
    let vantaEffect = null;

    const nusicName = ref(MUSICNAME);
    const ruleForm = reactive({
      username: "admin",
      password: "123",
    });
    const rules = reactive({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    });

    onMounted(() => {
      vantaEffect = WAVES({
        el: loginBg.value,
        THREE: THREE,
        mouseControls: true,
        touchControls: true,
        gyroControls: false,
        minHeight: 200.00,
        minWidth: 200.00,
        scale: 1.00,
        scaleMobile: 1.00,
        color: 0x41416f,
        shininess: 73.00,
        waveHeight: 31.50,
        waveSpeed: 0.95,
        zoom: 0.98
      });
    });

    onUnmounted(() => {
      if (vantaEffect) vantaEffect.destroy();
    });

    async function submitForm() {
      let username = ruleForm.username;
      let password = ruleForm.password;
      const result = (await HttpManager.getLoginStatus({username,password})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) routerManager(RouterName.Info, { path: RouterName.Info });
    }

    return {
      nusicName,
      ruleForm,
      rules,
      submitForm,
      loginBg,
      User,
      Lock
    };
  },
});
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.title {
  position: absolute;
  top: 50%;
  width: 100%;
  margin-top: -230px;
  text-align: center;
  font-size: 36px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  letter-spacing: 2px;
  z-index: 2;
}

.login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 380px;
  margin: -150px 0 0 -190px;
  padding: 40px;
  border-radius: 12px;
  /* 初始背景色 */
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  backdrop-filter: blur(5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  transition: all 0.4s ease;
  z-index: 2;
  /* 添加背景颜色动画 */
  animation: colorShift 15s ease infinite;
  background-size: 200% 200%;
}

/* 颜色切换动画 */
@keyframes colorShift {
  0% {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  }
  20% {
    background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  }
  40% {
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  }
  60% {
    background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  }
  80% {
    background: linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%);
  }
  100% {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  }
}

.login:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
  /* 悬停时加快动画速度 */
  animation-duration: 8s;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  letter-spacing: 2px;
  transition: all 0.4s;
  border-radius: 25px;
  border: none;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  color: white;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.login-btn span {
  position: relative;
  z-index: 1;
  transition: 0.4s;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(47, 82, 204, 0.91) 0%, rgba(255, 255, 255, 0.71) 100%);
  transition: 0.4s;
}

.login-btn:hover span {
  letter-spacing: 4px;
}

.login-btn:hover::before {
  left: 0;
}

.el-input {
  margin-bottom: 20px;
}

.el-input__inner {
  height: 45px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s;
}

.el-input__inner:focus {
  border-color: #4d705f;
  box-shadow: 0 0 0 2px rgba(77, 112, 95, 0.2);
}

/* 为输入框添加发光效果 */
.el-input:hover .el-input__inner {
  box-shadow: 0 0 10px rgba(77, 112, 95, 0.3);
}

/* 为整个登录框添加呼吸效果 */
@keyframes breathe {
  0% {
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  }
  50% {
    box-shadow: 0 15px 45px rgba(77, 112, 95, 0.3);
  }
  100% {
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  }
}

.login {
  animation: breathe 6s ease-in-out infinite, colorShift 15s ease infinite;
}
</style>
