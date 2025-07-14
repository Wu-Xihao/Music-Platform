<template>
  <div class="personal-form-container">
    <div class="form-header">
      <h2>个人资料设置</h2>
      <p>更新您的个人信息和联系方式</p>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <el-form
        ref="updateForm"
        label-width="100px"
        :model="registerForm"
        :rules="SignUpRules"
        class="profile-form"
    >
      <div class="form-columns">
        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">
              <el-icon><User /></el-icon>
            </div>
            <h3 class="section-title">基本信息</h3>
          </div>

          <el-form-item prop="username" label="用户名">
            <el-input
                v-model="registerForm.username"
                placeholder="输入用户名"
                prefix-icon="User"
                clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="性别">
            <el-radio-group v-model="registerForm.sex" class="gender-radio">
              <el-radio :label="0" border>
                <el-icon><Female /></el-icon>
                <span>女</span>
              </el-radio>
              <el-radio :label="1" border>
                <el-icon><Male /></el-icon>
                <span>男</span>
              </el-radio>
              <el-radio :label="2" border>
                <el-icon><Lock /></el-icon>
                <span>保密</span>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item prop="birth" label="生日">
            <el-date-picker
                type="date"
                placeholder="选择日期"
                v-model="registerForm.birth"
                style="width: 100%"
                value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>

          <el-form-item prop="location" label="地区">
            <el-select
                v-model="registerForm.location"
                placeholder="选择地区"
                style="width: 100%"
                clearable
            >
              <el-option
                  v-for="item in AREA"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>

        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">
              <el-icon><Iphone /></el-icon>
            </div>
            <h3 class="section-title">联系方式</h3>
          </div>

          <el-form-item prop="phoneNum" label="手机">
            <el-input
                placeholder="输入手机号码"
                v-model="registerForm.phoneNum"
                prefix-icon="Iphone"
                clearable
            >
              <template #prefix>
                <el-icon><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email" label="邮箱">
            <el-input
                v-model="registerForm.email"
                placeholder="输入邮箱地址"
                prefix-icon="Message"
                clearable
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="introduction" label="个人签名">
            <el-input
                type="textarea"
                placeholder="分享您的个性签名..."
                v-model="registerForm.introduction"
                :rows="4"
                maxlength="100"
                show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </div>

      <div class="form-actions">
        <el-button class="cancel-btn" @click="goBack(-1)" round>
          <el-icon><Close /></el-icon>
          <span>取消</span>
        </el-button>
        <el-button
            type="primary"
            class="save-btn"
            @click="saveMsg()"
            round
        >
          <el-icon><Check /></el-icon>
          <span>保存更改</span>
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, onMounted, getCurrentInstance, reactive } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { AREA, SignUpRules } from "@/enums";
import { HttpManager } from "@/api";
import {
  User, Iphone, Message, Close, Check,
  Female, Male, Lock
} from "@element-plus/icons-vue";

export default defineComponent({
  components: {
    User,
    Message,
    Iphone,
    Female, Male, Lock, Close, Check
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { goBack } = mixin();

    // 注册
    const registerForm = reactive({
      username: "",
      sex: "",
      phoneNum: "",
      email: "",
      birth: new Date(),
      introduction: "",
      location: "",
      userPic: "",
    });
    const userId = computed(() => store.getters.userId);

    async function getUserInfo(id) {
      const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
      registerForm.username = result.data[0].username;
      registerForm.sex = result.data[0].sex;
      registerForm.phoneNum = result.data[0].phoneNum;
      registerForm.email = result.data[0].email;
      registerForm.birth = result.data[0].birth;
      registerForm.introduction = result.data[0].introduction;
      registerForm.location = result.data[0].location;
      registerForm.userPic = result.data[0].avator;
    }

    async function saveMsg() {
      let canRun = true;
      (proxy.$refs["updateForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;

      const id = userId.value;
      const username = registerForm.username;
      const sex = registerForm.sex;
      const phoneNum = registerForm.phoneNum;
      const email = registerForm.email;
      const birth = registerForm.birth;
      const introduction = registerForm.introduction;
      const location = registerForm.location;
      const result = (await HttpManager.updateUserMsg({id,username,sex,phoneNum,email,birth,introduction,location})) as ResponseBody;

      (proxy as any).$message({
        message: result.message,
        type: result.type,
        duration: 2000,
        offset: 80
      });

      if (result.success) {
        proxy.$store.commit("setUsername", registerForm.username);
        setTimeout(() => goBack(-1), 1000);
      }
    }

    onMounted(() => {
      getUserInfo(userId.value);
    });

    return {
      AREA,
      registerForm,
      SignUpRules,
      saveMsg,
      goBack,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.personal-form-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px;
  background: $color-light-white;
  border-radius: 18px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;

  h2 {
    font-size: 28px;
    font-weight: 700;
    color: $color-black;
    margin-bottom: 8px;
    background: linear-gradient(135deg, $color-blue 0%, $color-blue-dark 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  p {
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
    z-index: 0;

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

.profile-form {
  :deep(.el-form-item) {
    margin-bottom: 25px;
  }

  :deep(.el-form-item__label) {
    font-weight: 600;
    color: $color-dark-grey;
    padding-right: 15px;
    font-size: 16px;
    margin-bottom: 8px;
    display: block;
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    background: $color-light-white;
    box-shadow: 0 0 0 1px $color-light-grey inset;
    transition: all 0.3s ease;
    padding: 0 18px;
    height: 46px;

    &:hover, &:focus {
      box-shadow: 0 0 0 2px $color-blue inset;
    }
  }

  :deep(.el-input__prefix) {
    display: flex;
    align-items: center;
    margin-right: 8px;

    .el-icon {
      color: $color-blue;
      font-size: 18px;
    }
  }

  :deep(.el-textarea__inner) {
    border-radius: 12px;
    background: $color-light-white;
    box-shadow: 0 0 0 1px $color-light-grey inset;
    transition: all 0.3s ease;
    padding: 12px 18px;
    min-height: 100px;

    &:hover, &:focus {
      box-shadow: 0 0 0 2px $color-blue inset;
    }
  }

  :deep(.el-select .el-input__wrapper) {
    box-shadow: 0 0 0 1px $color-light-grey inset;

    &:hover, &:focus {
      box-shadow: 0 0 0 2px $color-blue inset;
    }
  }
}

.form-columns {
  display: flex;
  gap: 35px;

  .form-section {
    flex: 1;
    background: #fff;
    padding: 30px;
    border-radius: 16px;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba($color-blue-shallow, 0.3);
    position: relative;
    overflow: hidden;

    &:before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, $color-blue, $color-blue-dark);
    }
  }
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 25px;

  .section-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: rgba($color-blue, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;

    .el-icon {
      color: $color-blue;
      font-size: 22px;
    }
  }
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: $color-black;
  margin: 0;
}

.gender-radio {
  :deep(.el-radio) {
    margin-right: 12px;
    margin-bottom: 10px;

    &.is-bordered {
      border-radius: 10px;
      padding: 10px 18px;
      border-color: $color-light-grey;
      transition: all 0.3s ease;

      .el-radio__label {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
        font-size: 15px;
      }

      .el-icon {
        font-size: 18px;
      }
    }

    &.is-checked {
      border-color: $color-blue;
      background: rgba($color-blue, 0.08);
      box-shadow: 0 2px 8px rgba($color-blue, 0.15);

      .el-radio__inner {
        background: $color-blue;
        border-color: $color-blue;
      }

      .el-radio__label {
        color: $color-blue;
      }
    }
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid $color-light-grey;

  .el-button {
    padding: 14px 40px;
    font-weight: 600;
    font-size: 17px;
    display: flex;
    align-items: center;
    gap: 10px;
    transition: all 0.3s ease;
    border-radius: 12px;
    letter-spacing: 0.5px;

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

  .cancel-btn {
    background: $color-light-white;
    border: 2px solid $color-grey;  /* 添加了按钮边界 */
    color: $color-grey;

    &:hover {
      background: #f9f9f9;
      border-color: $color-blue;
      color: $color-blue;
    }
  }

  .save-btn {
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

@media screen and (max-width: $sm) {
  .personal-form-container {
    padding: 20px 15px;
  }

  .form-header {
    h2 {
      font-size: 24px;
    }

    p {
      font-size: 14px;
    }
  }

  .form-columns {
    flex-direction: column;
    gap: 25px;
  }

  .form-section {
    padding: 25px 20px !important;
  }

  .section-header {
    margin-bottom: 20px;

    .section-icon {
      width: 36px;
      height: 36px;
    }
  }

  .section-title {
    font-size: 18px;
  }

  .gender-radio {
    :deep(.el-radio) {
      width: 100%;
      margin-right: 0;
      margin-bottom: 10px;
    }
  }

  .form-actions {
    flex-direction: column;
    gap: 15px;
    margin-top: 30px;
    padding-top: 25px;

    .el-button {
      width: 100%;
      justify-content: center;
      padding: 13px 20px;
      font-size: 16px;
    }
  }
}
</style>
