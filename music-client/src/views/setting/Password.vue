<template>
  <div class="password-form-container">
    <div class="form-header">
      <h2 class="form-title">修改密码</h2>
      <p class="form-subtitle">请填写以下信息更新您的账户密码</p>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <el-form
        ref="passwordForm"
        label-width="100px"
        :model="form"
        :rules="rules"
        class="password-form"
    >
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input
            type="password"
            v-model="form.oldPassword"
            placeholder="请输入当前密码"
            size="large"
            clearable
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="新密码" prop="newPassword">
        <el-input
            type="password"
            v-model="form.newPassword"
            placeholder="请输入新密码"
            size="large"
            clearable
            show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
            type="password"
            v-model="form.confirmPassword"
            placeholder="请再次输入新密码"
            size="large"
            clearable
            show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <div class="password-strength">
        <div class="strength-bar" :class=getPasswordStrength(form.newPassword)></div>
        <div class="strength-label">{{ getPasswordStrengthLabel(form.newPassword) }}</div>
      </div>

      <el-form-item class="form-actions">
        <div class="button-container">
        <el-button
            class="reset-btn"
            @click="clearData()"
        >
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button
            type="primary"
            class="confirm-btn"
            @click="confirm()"
        >
          <el-icon><Check /></el-icon>
          确认修改
        </el-button>
      </div>
      </el-form-item>
    </el-form>

    <div class="password-tips">
      <div class="tips-icon">
        <el-icon><Warning /></el-icon>
      </div>
      <h3>密码安全提示</h3>
      <ul>
        <li>推荐使用至少8个字符的密码</li>
        <li>混合使用字母、数字和符号</li>
        <li>请避免使用个人信息（如生日、姓名）</li>
        <li>不要与其他账户共用相同密码</li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, reactive } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { validatePassword } from "@/enums";
import { Refresh, Check, Lock, Warning } from "@element-plus/icons-vue";

export default defineComponent({
  components: {Check, Refresh, Lock, Warning},
  setup() {
    const store = useStore();
    const { proxy } = getCurrentInstance();
    const { goBack } = mixin();

    const form = reactive({
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
    const userId = computed(() => store.getters.userId);
    const userName = computed(() => store.getters.username);

    const validateCheck = (rule: any, value: any, callback: any) => {
      if (value === "") {
        callback(new Error("密码不能为空"));
      } else if (value !== form.newPassword) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const rules = reactive({
      oldPassword: [{ validator: validatePassword, trigger: "blur", min: 3 }],
      newPassword: [{ validator: validatePassword, trigger: "blur", min: 3 }],
      confirmPassword: [{ validator: validateCheck, trigger: "blur", min: 3 }],
    });

    // 获取密码强度
    function getPasswordStrength(password) {
      if (!password) return 'empty';
      if (password.length < 4) return 'weak';
      if (password.length < 8) return 'medium';

      const hasUpper = /[A-Z]/.test(password);
      const hasLower = /[a-z]/.test(password);
      const hasNumber = /[0-9]/.test(password);
      const hasSpecial = /[^A-Za-z0-9]/.test(password);

      const strength = [hasUpper, hasLower, hasNumber, hasSpecial].filter(Boolean).length;

      if (strength < 2) return 'weak';
      if (strength < 4) return 'medium';
      return 'strong';
    }

    // 获取密码强度标签
    function getPasswordStrengthLabel(password) {
      const strength = getPasswordStrength(password);
      switch(strength) {
        case 'empty': return '请设置密码';
        case 'weak': return '密码强度：弱';
        case 'medium': return '密码强度：中等';
        case 'strong': return '密码强度：强';
        default: return '';
      }
    }

    async function clearData() {
      form.oldPassword = "";
      form.newPassword = "";
      form.confirmPassword = "";
    }

    async function confirm() {
      let canRun = true;
      (proxy.$refs["passwordForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      const id = userId.value;
      const username = userName.value;
      const oldPassword = form.oldPassword;
      const password = form.newPassword;

      const result = (await HttpManager.updateUserPassword({id,username,oldPassword,password})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
        duration: 2000,
        offset: 80
      });
      if (result.success) {
        setTimeout(() => goBack(), 1000);
      }
    }

    return {
      form,
      clearData,
      confirm,
      rules,
      Refresh,
      Check,
      Lock,
      Warning,
      getPasswordStrength,
      getPasswordStrengthLabel,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.password-form-container {
  max-width: 650px;
  margin: 0 auto;
  padding: 30px;
  background: white;
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  position: relative;

  &:before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, $color-blue, $color-blue-dark);
    border-radius: 18px 18px 0 0;
  }
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;

  .form-title {
    font-size: 28px;
    font-weight: 700;
    color: $color-black;
    margin-bottom: 8px;
    background: linear-gradient(135deg, $color-blue 0%, $color-blue-dark 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .form-subtitle {
    font-size: 16px;
    color: $color-grey;
    margin: 0;
    max-width: 500px;
    margin: 0 auto;
  }

  .header-decoration {
    position: absolute;
    top: -10px;
    right: 0;
    display: flex;
    gap: 12px;
    z-index: 1;

    .decoration-circle {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: $color-blue;
      opacity: 0.6;

      &:nth-child(2) {
        background: $color-blue-active;
        transform: translateY(6px);
      }

      &:nth-child(3) {
        background: $color-blue-shallow;
      }
    }
  }
}

.password-form {
  :deep(.el-form-item) {
    margin-bottom: 32px;
  }

  :deep(.el-form-item__label) {
    font-size: 18px;
    font-weight: 600;
    color: $color-dark-grey;
    padding-bottom: 10px;
    display: block;
  }

  :deep(.el-input) {
    font-size: 17px;

    .el-input__wrapper {
      border-radius: 12px;
      padding: 0 20px;
      height: 52px;
      box-shadow: 0 0 0 1px $color-light-grey inset;
      transition: all 0.3s ease;

      &:hover, &:focus-within {
        box-shadow: 0 0 0 2px $color-blue inset;
      }
    }

    .el-input__prefix {
      display: flex;
      align-items: center;
      margin-right: 8px;

      .el-icon {
        color: $color-blue;
        font-size: 20px;
      }
    }
  }
}

.password-strength {
  margin: -15px 0 20px 100px;
  display: flex;
  align-items: center;
  gap: 15px;

  .strength-bar {
    height: 6px;
    flex: 1;
    border-radius: 3px;
    background: $color-light-grey;
    overflow: hidden;
    position: relative;

    &:before {
      content: "";
      position: absolute;
      left: 0;
      top: 0;
      height: 100%;
      width: 0;
      transition: width 0.5s ease;
    }

    &.empty:before { width: 0; }
    &.weak:before {
      width: 30%;
      background: $color-danger;
    }
    &.medium:before {
      width: 60%;
      background: $color-warning;
    }
    &.strong:before {
      width: 100%;
      background: $color-theme;
    }
  }

  .strength-label {
    font-size: 14px;
    min-width: 100px;

    .empty & { color: $color-grey; }
    .weak & { color: $color-danger; }
    .medium & { color: $color-warning; }
    .strong & { color: $color-theme; font-weight: 500; }
  }
}

.form-actions {

  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid $color-light-grey;

  .button-container {
    display: flex;
    justify-content: space-between; /* 两端对齐 */
    align-items: center;
    width: 100%;
    gap: 20px;
  }

  .el-button {
    padding: 14px 36px;
    font-size: 17px;
    font-weight: 500;
    border-radius: 12px;
    display: flex;
    align-items: center;
    gap: 20px;
    transition: all 0.3s ease;
    flex:1;

    .el-icon {
      font-size: 20px;
    }

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .reset-btn {
    background: white;
    border: 2px solid $color-grey;  /* 添加了2px边框 */
    color: $color-grey;

    &:hover {
      border-color: $color-blue;  /* 悬停时边框颜色变为蓝色 */
      color: $color-blue;
      background: rgba($color-blue, 0.05);
    }
  }

  .confirm-btn {
    background: linear-gradient(135deg, $color-blue 0%, $color-blue-dark 100%);
    border: none;
    box-shadow: 0 4px 15px rgba($color-blue, 0.3);
    position: relative;
    overflow: hidden;

    &:before {
      content: "";
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
      transition: all 0.6s ease;
    }

    &:hover {
      box-shadow: 0 6px 20px rgba($color-blue, 0.4);

      &:before {
        left: 100%;
      }
    }
  }
}

.password-tips {
  background: rgba($color-blue-shallow, 0.08);
  border-radius: 16px;
  padding: 25px;
  margin-top: 40px;
  border-left: 4px solid $color-blue;
  position: relative;

  .tips-icon {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: rgba($color-blue, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;

    .el-icon {
      color: $color-blue;
      font-size: 22px;
    }
  }

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $color-black;
    margin-top: 0;
    margin-bottom: 15px;
    padding-right: 50px;
  }

  ul {
    margin: 0;
    padding-left: 20px;

    li {
      font-size: 15px;
      margin-bottom: 12px;
      line-height: 1.6;
      color: $color-dark-grey;
      position: relative;

      &:before {
        content: "•";
        color: $color-blue;
        font-weight: bold;
        display: inline-block;
        width: 1em;
        margin-left: -1em;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .password-form-container {
    padding: 25px 20px;
  }

  .form-title {
    font-size: 24px;
  }

  .form-subtitle {
    font-size: 15px;
  }

  .password-form {
    :deep(.el-form-item) {
      margin-bottom: 28px;
    }

    :deep(.el-form-item__label) {
      font-size: 16px;
    }
  }

  .password-strength {
    margin-left: 0;
  }

  .form-actions {
    .button-container {
      flex-direction: column;
      gap: 15px;
    }

    .el-button {
      width: 100%;
      justify-content: center;
      padding: 13px 20px;
      font-size: 16px;
    }
  }

  .password-tips {
    padding: 20px;

    .tips-icon {
      top: 15px;
      right: 15px;
      width: 35px;
      height: 35px;
    }
  }
}
</style>
