<template>
  <el-container class="register-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration"></div>

    <div class="register-box">
      <!-- 注册框头部 -->
      <el-header class="register-header">
        <div class="logo">
          <h1>用户注册</h1>
        </div>
      </el-header>

      <!-- 注册主体内容 -->
      <el-main class="register-main">
        <el-form ref="signUpForm" label-width="70px" status-icon :model="registerForm" :rules="SignUpRules">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="registerForm.username" placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input type="password" placeholder="密码" v-model="registerForm.password"></el-input>
          </el-form-item>
          <el-form-item prop="sex" label="性别">
            <el-radio-group v-model="registerForm.sex">
              <el-radio :label="0">女</el-radio>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="phoneNum" label="手机">
            <el-input placeholder="手机" v-model="registerForm.phoneNum"></el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="registerForm.email" placeholder="邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="birth" label="生日">
            <el-date-picker type="date" placeholder="选择日期" v-model="registerForm.birth" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item prop="introduction" label="签名">
            <el-input type="textarea" placeholder="签名" v-model="registerForm.introduction"></el-input>
          </el-form-item>
          <el-form-item prop="location" label="地区">
            <el-select v-model="registerForm.location" placeholder="地区" style="width: 100%">
              <el-option v-for="item in AREA" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>

          <el-footer class="button-footer">
            <el-form-item class="sign-btn">
              <div class="button-group">
                <el-button class="cancel-btn" @click="goBackRegist()">取消</el-button>
                <el-button type="primary" class="confirm-btn" @click="handleSignUp">确定</el-button>
              </div>
            </el-form-item>
          </el-footer>
        </el-form>
      </el-main>
    </div>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { getBirth } from "@/utils";
import { AREA, RouterName, NavName, SignUpRules } from "@/enums";

export default defineComponent({
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
        const result = (await HttpManager.SignUp({ username, password, sex, phoneNum, email, birth, introduction, location })) as ResponseBody;
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
.register-container {
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

  .register-box {
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

  .register-header {
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

  .register-main {
    padding: 30px 40px;

    // 标签样式调整
    .el-form-item__label {
      font-weight: 500;
      color: #4a5568;
    }

    // 输入框统一大小
    .el-input, .el-select, .el-date-editor, .el-textarea {
      margin-top: 5px;
    }

    .el-input__inner, .el-textarea__inner {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }
  }

  // 按钮区域样式
  .button-footer {
    padding-top: 20px;
    margin-top: 15px;
    border-top: 1px solid #edf2f7;

    .sign-btn {
      margin-bottom: 0;
    }

    .button-group {
      display: flex;
      justify-content: center; /* 修改为居中布局 */
      width: 100%;
      gap: 20px;
      max-width: 400px; /* 设置最大宽度 */
      margin: 0 auto; /* 水平居中 */
    }

    .cancel-btn,
    .confirm-btn {
      flex: 0 0 calc(50% - 10px); /* 固定宽度并考虑间隙 */
      max-width: 200px; /* 最大宽度限制 */
      height: 44px;
      font-size: 16px;
      border-radius: 8px;
      transition: all 0.3s ease;
      margin-right: 70px;

      /* 保持原有的取消按钮样式 */
      &.cancel-btn {
        background-color: #f8fafc;
        border: 1px solid #e2e8f0;
        color: #4a5568;

        &:hover {
          background-color: #edf2f7;
          border-color: #cbd5e0;
          color: #2d3748;
        }
      }

      /* 保持原有的确定按钮样式 */
      &.confirm-btn {
        background: linear-gradient(90deg, #3b82f6, #8b5cf6);
        border: none;
        box-shadow: 0 4px 12px rgba(123, 97, 255, 0.25);

        &:hover {
          background: linear-gradient(90deg, #2563eb, #7c3aed);
          box-shadow: 0 6px 14px rgba(123, 97, 255, 0.35);
          transform: translateY(-2px);
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 576px) {
  .register-container {
    padding: 15px;
  }

  .register-box {
    width: 100%;
    max-width: 100%;
  }

  .register-main {
    padding: 20px;

    // 标签宽度在小屏幕上调整
    :deep(.el-form-item__label) {
      width: auto !important;
      text-align: left;
      padding-right: 12px;
      margin-bottom: 6px;
    }

    // 表单元素在小屏幕上100%宽度
    .el-input, .el-select, .el-date-editor {
      width: 100%;
    }
  }

  .button-group {
    flex-direction: column;
    gap: 12px !important;

    .cancel-btn, .confirm-btn {
      width: 100%;
    }
  }
}
</style>