<template>
  <div class="container">
    <!-- 左侧图片区域 -->
    <div class="left-image">
      <img src="./music5.jpg" alt="Music Image 5" class="full-image">
    </div>
    <!-- 右侧表单区域 -->
    <div class="right-form">
      <yin-login-logo></yin-login-logo>
      <!-- 修改图片样式 -->
      <img src="@/views/music4.png" alt="Music Image" class="music-image full-right-image">
      <div class="sign fade-in">
        <div class="sign-head">
          <span>用户注册</span>
        </div>
        <el-form ref="signUpForm" label-width="70px" status-icon :model="registerForm" :rules="SignUpRules">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="registerForm.username" placeholder="用户名" class="scale-in custom-input"></el-input>
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input type="password" placeholder="密码" v-model="registerForm.password" class="scale-in custom-input"></el-input>
          </el-form-item>
          <el-form-item prop="sex" label="性别">
            <el-radio-group v-model="registerForm.sex">
              <el-radio :label="0">女</el-radio>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="phoneNum" label="手机">
            <el-input placeholder="手机" v-model="registerForm.phoneNum" class="scale-in custom-input"></el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="registerForm.email" placeholder="邮箱" class="scale-in custom-input"></el-input>
          </el-form-item>
          <el-form-item prop="birth" label="生日">
            <el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%" class="scale-in custom-input"></el-date-picker>
          </el-form-item>
          <el-form-item prop="introduction" label="签名">
            <el-input type="textarea" placeholder="签名" v-model="registerForm.introduction" class="scale-in custom-input"></el-input>
          </el-form-item>
          <el-form-item prop="location" label="地区">
            <el-select v-model="registerForm.location" placeholder="地区" style="width: 100%" class="scale-in custom-input">
              <el-option v-for="item in AREA" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="sign-btn">
            <el-button @click="goBackRegist()">取消</el-button>
            <el-button type="primary" @click="handleSignUp(formRef)">确定</el-button>
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
import { getBirth } from "@/utils";
import { AREA, RouterName, NavName } from "@/enums";

export default defineComponent({
  components: {
    YinLoginLogo,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    const registerForm = reactive({
      username: "",
      password: "",
      sex: "",
      phoneNum: "",
      email: "",
      birth: new Date(),
      introduction: "",
      location: "",
    });

    // 定义验证规则
    const SignUpRules = {
      username: [
        { required: true, message: '用户名未输入', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '密码未输入', trigger: 'blur' }
      ]
    };

    async function goBackRegist() {
      routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    async function handleSignUp() {
      let canRun = true;
      (proxy.$refs["signUpForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      try {
        const username = registerForm.username;
        const password = registerForm.password;
        const sex = registerForm.sex;
        const phoneNum = registerForm.phoneNum;
        const email = registerForm.email;
        const birth = registerForm.birth;
        const introduction = registerForm.introduction;
        const location = registerForm.location;
        const result = (await HttpManager.SignUp({username,password,sex,phoneNum,email,birth,introduction,location})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          changeIndex(NavName.SignIn);
          routerManager(RouterName.SignIn, { path: RouterName.SignIn });
        }
      } catch (error) {
        console.error(error);
      }
    }

    return {
      SignUpRules,
      AREA,
      registerForm,
      handleSignUp,
      goBackRegist,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";

/* 定义淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 定义缩放动画 */
@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 添加动画类 */
.fade-in {
  animation: fadeIn 1s ease-in-out;
}

/* 应用缩放动画到输入框 */
.scale-in {
  animation: scaleIn 0.5s ease-in-out;
}

/* 设置所有文字颜色为白色 */
.sign {
  color: white;
}

/* 设置表单标签颜色为白色 */
.el-form-item__label {
  color: white;
}

/* 设置输入框内占位符颜色为白色 */
.el-input::placeholder {
  color: white;
}

/* 设置单选框文字颜色为白色 */
.el-radio__label {
  color: white;
}

/* 设置选择器选项文字颜色为白色 */
.el-option {
  color: white;
}

/* 图片样式 */
.music-image {
  margin-bottom: 20px;
}

/* 填充满右侧区域的图片 */
.full-right-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 容器布局 */
.container {
  display: flex;
  height: 100vh;
}

/* 左侧图片区域 */
.left-image {
  flex: 1;
  overflow: hidden;
}

/* 填充满左侧区域的图片 */
.full-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 右侧表单区域 */
.right-form {
  flex: 1;
  padding: 20px;
}

/* 自定义输入框样式 */
.custom-input {
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  transition: border-color 0.3s ease, background-color 0.3s ease;

  &:hover {
    border-color: rgba(255, 255, 255, 0.8);
  }

  &:focus {
    border-color: white;
    background-color: rgba(255, 255, 255, 0.2);
    outline: none;
  }
}
</style>