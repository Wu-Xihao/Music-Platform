<template>
  <div class="login-container">
    <!-- 标题部分 -->
    <div class="title">{{ nusicName }}</div>
    <!-- 登录表单部分 -->
    <div class="login">
      <el-form :model="ruleForm" :rules="rules" class="form-style">
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="请输入用户名" class="input-style"></el-input>
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input type="password" placeholder="请输入密码" v-model="ruleForm.password" @keyup.enter="submitForm" class="input-style"></el-input>
        </el-form-item>
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button class="login-btn" type="primary" @click="submitForm">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api/index";
import { RouterName, MUSICNAME } from "@/enums";

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();

    const nusicName = ref(MUSICNAME);
    const ruleForm = reactive({
      username: "admin",
      password: "123",
    });
    const rules = reactive({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    });

    async function submitForm() {
      let username = ruleForm.username;
      let password = ruleForm.password;
      const result = (await HttpManager.getLoginStatus({ username, password })) as ResponseBody;
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
    };
  },
});
</script>

<style scoped>
/* 登录容器样式 */
.login-container {
  position: relative;
  background: url('@/assets/music1.jpg') no-repeat center center fixed;
  background-size: cover;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* 标题样式 */
.title {
  font-size: 48px;
  font-weight: 800;
  color: #ffffff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  margin-bottom: 40px;
  animation: fadeInDown 1s ease-out;
}

/* 登录表单容器样式 */
.login {
  width: 380px;
  padding: 40px;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
  animation: fadeInUp 1s ease-out;
}

/* 表单样式 */
.form-style {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 输入框样式 */
.input-style {
  border-radius: 8px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, transform 0.3s ease; /* 添加 transform 过渡 */
}

.input-style:focus {
  border-color: #667eea;
  outline: none;
  transform: scale(1.1); /* 放大输入框 */
}

/* 登录按钮样式 */
.login-btn {
  width: 100%;
  border-radius: 8px;
  background: linear-gradient(135deg, #ec1c1c 0%, #0b31dc 100%);
  border: none;
  transition: background 0.3s ease;
}

.login-btn:hover {
  background: linear-gradient(135deg, #bc09ed 0%, #06f813 100%);
}

/* 淡入向下动画 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 淡入向上动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>