<template>
  <el-container class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration"></div>

    <div class="login-box">
      <!-- 登录框头部 -->
      <el-header class="login-header">
        <div class="logo">
<!--          <el-icon :size="40" class="logo-icon">-->
<!--            <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">-->
<!--              <circle cx="50" cy="50" r="45" fill="#409EFF" />-->
<!--              <path d="M30,70 L50,30 L70,70 Z" fill="#fff" />-->
<!--            </svg>-->
<!--          </el-icon>-->
          <h1>系统登录</h1>
        </div>
        <span class="login-title">账号登录</span>
      </el-header>

      <!-- 登录主体内容 -->
      <el-main class="login-main">
        <!-- 快捷登录方式 -->
        <div class="quick-login">
          <p class="login-method-title">QQ、邮箱快捷登录</p>
          <div class="button-group">
            <el-button type="primary" class="social-btn qq-btn" :ripple="false">
              <el-icon :size="24">
                <svg x="1752651456480" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4102" width="200" height="200"><path d="M511.09761 957.257c-80.159 0-153.737-25.019-201.11-62.386-24.057 6.702-54.831 17.489-74.252 30.864-16.617 11.439-14.546 23.106-11.55 27.816 13.15 20.689 225.583 13.211 286.912 6.767v-3.061z" fill="#FAAD08" p-id="4103"></path><path d="M496.65061 957.257c80.157 0 153.737-25.019 201.11-62.386 24.057 6.702 54.83 17.489 74.253 30.864 16.616 11.439 14.543 23.106 11.55 27.816-13.15 20.689-225.584 13.211-286.914 6.767v-3.061z" fill="#FAAD08" p-id="4104"></path><path d="M497.12861 474.524c131.934-0.876 237.669-25.783 273.497-35.34 8.541-2.28 13.11-6.364 13.11-6.364 0.03-1.172 0.542-20.952 0.542-31.155C784.27761 229.833 701.12561 57.173 496.64061 57.162 292.15661 57.173 209.00061 229.832 209.00061 401.665c0 10.203 0.516 29.983 0.547 31.155 0 0 3.717 3.821 10.529 5.67 33.078 8.98 140.803 35.139 276.08 36.034h0.972z" fill="#000000" p-id="4105"></path><path d="M860.28261 619.782c-8.12-26.086-19.204-56.506-30.427-85.72 0 0-6.456-0.795-9.718 0.148-100.71 29.205-222.773 47.818-315.792 46.695h-0.962C410.88561 582.017 289.65061 563.617 189.27961 534.698 185.44461 533.595 177.87261 534.063 177.87261 534.063 166.64961 563.276 155.56661 593.696 147.44761 619.782 108.72961 744.168 121.27261 795.644 130.82461 796.798c20.496 2.474 79.78-93.637 79.78-93.637 0 97.66 88.324 247.617 290.576 248.996a718.01 718.01 0 0 1 5.367 0C708.80161 950.778 797.12261 800.822 797.12261 703.162c0 0 59.284 96.111 79.783 93.637 9.55-1.154 22.093-52.63-16.623-177.017" fill="#000000" p-id="4106"></path><path d="M434.38261 316.917c-27.9 1.24-51.745-30.106-53.24-69.956-1.518-39.877 19.858-73.207 47.764-74.454 27.875-1.224 51.703 30.109 53.218 69.974 1.527 39.877-19.853 73.2-47.742 74.436m206.67-69.956c-1.494 39.85-25.34 71.194-53.24 69.956-27.888-1.238-49.269-34.559-47.742-74.435 1.513-39.868 25.341-71.201 53.216-69.974 27.909 1.247 49.285 34.576 47.767 74.453" fill="#FFFFFF" p-id="4107"></path><path d="M683.94261 368.627c-7.323-17.609-81.062-37.227-172.353-37.227h-0.98c-91.29 0-165.031 19.618-172.352 37.227a6.244 6.244 0 0 0-0.535 2.505c0 1.269 0.393 2.414 1.006 3.386 6.168 9.765 88.054 58.018 171.882 58.018h0.98c83.827 0 165.71-48.25 171.881-58.016a6.352 6.352 0 0 0 1.002-3.395c0-0.897-0.2-1.736-0.531-2.498" fill="#FAAD08" p-id="4108"></path><path d="M467.63161 256.377c1.26 15.886-7.377 30-19.266 31.542-11.907 1.544-22.569-10.083-23.836-25.978-1.243-15.895 7.381-30.008 19.25-31.538 11.927-1.549 22.607 10.088 23.852 25.974m73.097 7.935c2.533-4.118 19.827-25.77 55.62-17.886 9.401 2.07 11 2.07 13.75 5.116 14.668 6.316 1.355 1.77 1.726 4.29 0.352 7.684-2.722 6.725-8.338 6.542-11.454 5.226-2.01-0.85-26.94-15.889-49.905 6.553-1.579 1.545-4.405 2.074-7.085 0.242-2.678-1.834-3.786-5.553-2.196-8.135" fill="#000000" p-id="4109"></path><path d="M504.33261 584.495h-0.967c-63.568 0.752-140.646-7.504-215.286-21.92-6.391 36.262-10.25 81.838-6.936 136.196 8.37 137.384 91.62 223.736 220.118 224.996H506.48461c128.498-1.26 211.748-87.612 220.12-224.996 3.314-54.362-0.547-99.938-6.94-136.203-74.654 14.423-151.745 22.684-215.332 21.927" fill="#FFFFFF" p-id="4110"></path><path d="M323.27461 577.016v137.468s64.957 12.705 130.031 3.91V591.59c-41.225-2.262-85.688-7.304-130.031-14.574" fill="#EB1C26" p-id="4111"></path><path d="M788.09761 432.536s-121.98 40.387-283.743 41.539h-0.962c-161.497-1.147-283.328-41.401-283.744-41.539l-40.854 106.952c102.186 32.31 228.837 53.135 324.598 51.926l0.96-0.002c95.768 1.216 222.4-19.61 324.6-51.924l-40.855-106.952z" fill="#EB1C26" p-id="4112"></path></svg>
              </el-icon>

            </el-button>

            <el-button class="social-btn email-btn" :ripple="false" @click="handleEmail">
              <el-icon :size="24">
                <svg t="1752653064516" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12352" width="200" height="200"><path d="M265.225043 0.036501h12.629358l34.128466 0.073002 35.771013 0.073002 67.599915 0.109503 77.017181 0.146004c52.743994 0.036501 105.524488 0.146004 158.304983 0.292009v7.300206l0.365011 68.183931 0.182505 35.040993a6158.709917 6158.709917 0 0 0 0.255507 46.757824l0.109503 18.104513v5.329151c0.255507 17.374492 5.694161 32.996934 17.6665 45.772296 18.287018 16.498467 39.421116 19.272546 63.18329 19.455051l5.256149 0.036501 17.082483 0.182505a41566.537677 41566.537677 0 0 1 43.07122 0.438012l37.523063 0.365011 56.832109 0.547515v173.671919H129.696705c0-352.964996 0-352.964996 35.040992-391.510088 29.200827-27.850289 62.234262-30.587866 100.487346-30.36886z m443.341556 18.652028c22.192629 12.592857 40.005133 33.690454 57.963641 51.575961l3.978613 3.942112a22462.006131 22462.006131 0 0 1 41.866686 41.720681 7118.942611 7118.942611 0 0 1 24.273187 24.163684c29.127825 28.835817 29.127825 28.835817 39.603622 46.283311v4.015114a26990.50688 26990.50688 0 0 1-92.165111 0.292008 8154.002424 8154.002424 0 0 1-54.021529 0.146004h-4.489628c-7.446211 0-10.658302-0.219006-17.009481-4.453126a129.286661 129.286661 0 0 1-0.657019-15.841448v-4.964141c0-5.438654 0-10.840807 0.073002-16.279461v-11.242318l0.182505-29.675341c0.073002-10.074285 0.073002-20.185072 0.109503-30.295858l0.292009-59.387182zM133.310307 479.550581h17.374492l9.672774-0.036501a12869.534469 12869.534469 0 0 1 55.189563-0.073002H278.146409l39.092607-0.073002 108.298567-0.036501h35.113995l113.116703-0.109503a94769.458901 94769.458901 0 0 1 181.337136-0.109503 23462.572467 23462.572467 0 0 1 83.842874-0.036501 4393.337421 4393.337421 0 0 1 35.333001 0c26.061738-0.182505 49.020888 1.715549 69.826477 18.688529l3.86911 3.102588c11.753333 10.548799 17.155486 24.090682 22.083125 38.8371 0.219006 3.796108 0.292008 7.592215 0.32851 11.388322l0.036501 7.190704v7.920724l0.073002 8.322236 0.109503 27.375775 0.036501 9.417267 0.109503 39.275112c0 18.72503 0.073002 37.450061 0.219006 56.211592 0.073002 13.140372 0.109503 26.353746 0.109503 39.530619l0.109503 23.579668c0.036501 7.40971 0.073002 14.81942 0.036501 22.265631l0.109503 11.935838c-0.146004 22.630641-6.022671 41.246168-21.864119 57.744635-23.65267 22.59414-49.750909 23.762173-81.105297 23.65267l-9.636273 0.036501a9078.245099 9078.245099 0 0 1-51.466457 0h-3.394596a35508.899123 35508.899123 0 0 1-101.107864 0H312.639886a46788.850075 46788.850075 0 0 0-100.450845 0 12057.094461 12057.094461 0 0 0-53.912027 0H148.93275c-29.273829 0.073002-54.459542-2.080559-76.798175-22.740144a72.199045 72.199045 0 0 1-20.623084-49.057389l-0.073002-7.190704v-7.920724a15813.087332 15813.087332 0 0 0-0.182506-35.698011v-9.417267l-0.146004-39.275112c0-18.761531-0.073002-37.486562-0.219006-56.211592-0.073002-13.140372-0.109503-26.353746-0.109503-39.53062 0-7.884223 0-15.731946-0.109503-23.616168a1965.325159 1965.325159 0 0 1-0.036501-22.22913c0-4.015114-0.036501-7.957225-0.109503-11.935838 0.146004-22.265631 5.840165-41.209667 21.462608-57.489128 18.469523-17.118985 36.02652-23.65267 61.321736-23.725672z m689.030014 291.67976h35.953518v-179.658087h-35.953518v179.658087zM273.401275 623.510658v-29.930848H141.632543v177.650531h135.783845v-29.930847H177.586061v-47.889356h89.829044v-29.930848h-89.792543v-39.968632H273.401275z m515.029585 0v-29.930848h-33.945961v29.930848h33.945961z m-78.112212 25.477722c-12.592857-7.665217-25.258715-10.25679-39.89563-10.512298h-5.65766c-13.797391 0.109503-26.353746 0.073002-38.326085 7.77472-10.95031 9.599772-15.257432 16.571469-17.6665 31.171883l31.938404 4.015114c4.745134-9.745776 4.745134-9.745776 10.329793-12.629358 10.621801-2.555072 20.988094-2.007557 31.573394 0.620517l4.015113 17.958509c-16.790476 2.336066-33.763456 4.818136-49.969915 9.89178-14.235403 5.402153-23.981179 11.461325-30.441862 25.660227-3.577101 10.877308-2.81058 20.586583 1.934555 30.952876 5.694161 10.001283 12.519855 14.855921 23.178156 18.834534 14.1259 4.343623 28.324802 4.197619 41.538177-2.737578l6.387681-3.869109 6.460683-3.86911 4.891138-2.993085 2.007557 11.972339h35.917017c-4.015114-11.826335-6.679689-22.156127-6.825693-34.639481v-5.110144c-0.073002-7.482712-0.146004-14.965424-0.146004-22.484637 0-5.694161-0.073002-11.424824-0.146004-17.118985l-0.073002-5.438654c0.073002-14.49091-0.547516-26.390247-11.023313-37.45006z m-213.348542 6.460683c-8.577743-9.307764-15.987453-15.330434-28.872317-16.680973l-7.190704 0.109503c-10.95031 0-18.944037 0.949027-27.850289 7.628716l-3.577101 3.212091-3.650103 3.285093-2.701077 2.44557-2.007557-13.943395h-31.938404v129.724673h33.945961c0.109503-8.906252 0.219006-17.849005 0.401511-26.791758l0.219007-10.986811 0.255507-16.133457 0.073002-5.110145c0.109503-10.074285 0.766522-19.017039 3.832608-28.653312 2.737578-7.920724 5.475155-12.154844 13.176874-16.096955 6.935196-0.912526 13.505382-1.241035 19.966065 1.971055 4.59913 12.045341 4.59913 12.045341 4.745134 25.112712l0.073002 5.949668a4089.028303 4089.028303 0 0 1 0.547516 32.084409l0.511014 30.405361 0.109504 8.249233h35.917017a5454.349469 5454.349469 0 0 1 0.620517-42.158693l0.255508-17.99501 0.109503-5.730662c0-3.577101 0.073002-7.117702 0.146004-10.694803 0.584017-10.329793 3.321594-17.99501 11.096314-25.07621a35.113994 35.113994 0 0 1 23.725672-0.146005c4.23412 5.402153 4.23412 5.402153 4.526128 10.366294l0.073002 5.110145 0.146004 5.840165 0.073002 6.205176a4507.038141 4507.038141 0 0 1 0.511015 33.690454l0.511014 31.901903 0.146004 8.687246h35.917018a22361.117274 22361.117274 0 0 0 0.182505-70.30099V694.249661l0.036501-6.460683v-6.059171c0.036501-8.83325-0.073002-16.93648-4.964141-24.528695-7.300207-8.322236-13.870393-15.695444-25.185713-17.703001l-0.912526-0.073002c-15.403436-1.095031-27.55828-1.934555-40.005133 8.030227-4.270621 3.431097-8.030227 5.840165-12.994368 8.030228z m291.460754 115.781278v-129.724673h-33.945961v129.724673h33.945961zM371.224045 723.340985v-33.945961H303.332122v33.945961h67.891923z m314.675412 9.636273c1.75205-4.380124 1.460041-9.52677 0.73002-23.616169-4.891139 0-4.891139 0-15.220931 2.336066l-4.380124 1.022029c-11.497826 2.409068-18.871034 4.818136-26.317245 14.600414-1.095031 5.730662 0.073002 10.512298 1.971056 15.987453 3.139089 4.015114 3.139089 4.015114 8.030227 5.986169 12.70236 1.460041 19.637556-0.36501 29.930848-7.993726 2.591573-3.5406 4.270621-5.840165 5.256149-8.322236z m200.317673 209.917445H135.682874c7.154203 19.710558 12.446852 32.084409 26.609254 46.757824 22.630641 21.243602 48.03536 33.872959 79.535753 34.164968l6.533685-0.073002h83.879375c15.914451-0.073002 31.828901-0.073002 47.706851-0.073002 28.397804 0 56.759107 0 85.120411-0.073002l82.784344-0.109503h10.293292l94.756683-0.036502h33.398446l41.063663-0.073002h2.518571l18.433022-0.036501h19.41855l7.081201-0.036501c16.863478 0.036501 30.73387-0.985528 46.538818-7.373208 33.215941-16.206459 52.159977-38.034077 64.862337-73.002068z" fill="#FAAD08" p-id="4103"></path></svg>
              </el-icon>

            </el-button>
          </div>
        </div>

        <!-- 分割线 -->
        <div class="divider-wrapper">
          <div class="divider-line"></div>
          <span class="divider-text">或使用账号密码登录</span>
          <div class="divider-line"></div>
        </div>

        <!-- 账号密码登录表单 -->
        <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules" class="login-form">
          <el-form-item prop="username" class="form-item">
            <el-input placeholder="用户名" v-model="registerForm.username" class="login-input">
              <template #prefix>
                <el-icon class="input-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" class="form-item">
            <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn" class="login-input">
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-button type="primary" @click="handleLoginIn" class="login-btn" :loading="loginLoading">登录</el-button>
        </el-form>
      </el-main>

      <!-- 登录框底部 -->
      <el-footer class="login-footer">
        <div class="footer-links">
          <el-button type="text" @click="handleSignUp" class="footer-link">注册账号</el-button>
          <span class="link-separator">|</span>
          <el-button type="text" @click="handleForgotPassword" class="footer-link">忘记密码</el-button>
        </div>
      </el-footer>
    </div>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance, ref } from "vue";
import { User, Lock } from '@element-plus/icons-vue';
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { NavName, RouterName, SignInRules } from "@/enums";

export default defineComponent({
  components: {
    User,
    Lock
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    // 登录加载状态
    const loginLoading = ref(false);

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

      // 显示加载状态
      loginLoading.value = true;

      try {
        const username = registerForm.username;
        const password = registerForm.password;
        const result = (await HttpManager.signIn({username,password})) as ResponseBody;
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
      } finally {
        // 隐藏加载状态
        loginLoading.value = false;
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
      loginLoading
    };
  },
});
</script>

<style lang="scss" scoped>
// 引入Element Plus图标
@use "element-plus/theme-chalk/src/common/var.scss" as *;

.login-container {
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

  .login-box {
    width: 100%;
    max-width: 420px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    position: relative;
    z-index: 1;
    transition: transform 0.3s ease;
    top: -80px;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
    }
  }

  .login-header {
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
      top: -10px;

      .logo-icon {
        margin-right: 10px;
        color: #fff;
      }

      h1 {
        font-size: 24px;
        margin: 0;
        font-weight: 600;
      }
    }

    .login-title {
      font-size: 20px;
      font-weight: 500;
      letter-spacing: 1px;
    }
  }

  .login-main {
    padding: 30px 40px;

    .quick-login {
      margin-bottom: 25px;

      .login-method-title {
        text-align: center;
        color: #606266;
        margin-bottom: 15px;
        font-size: 14px;
        position: relative;

        &::after, &::before {
          content: "";
          position: absolute;
          top: 50%;
          width: 30%;
          height: 1px;
          background: #e5e6eb;
        }

        &::before {
          left: 0;
        }

        &::after {
          right: 0;
        }
      }

      .button-group {
        display: flex;
        justify-content: center;
        gap: 15px;
      }

      .social-btn {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        padding: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.1);
        }

        .icon {
          width: 24px;
          height: 24px;
        }
      }

      .qq-btn {
        background: #12B7F5;
        border-color: #12B7F5;

        &:hover {
          background: #0BA5E1;
          border-color: #0BA5E1;
        }
      }

      .email-btn {
        background: #f4ea2a;
        border-color: #f4ea2a;
        color: #333;

        &:hover {
          background: #E6DE1A;
          border-color: #E6DE1A;
        }
      }
    }

    .divider-wrapper {
      display: flex;
      align-items: center;
      margin: 20px 0;

      .divider-line {
        flex: 1;
        height: 1px;
        background: #e5e6eb;
      }

      .divider-text {
        padding: 0 15px;
        color: #909399;
        font-size: 12px;
      }
    }

    .login-form {
      width: 100%;
    }

    .form-item {
      margin-bottom: 20px;
    }

    .login-input {
      height: 48px;
      border-radius: 8px;
      border-color: #e5e6eb;
      transition: all 0.3s ease;

      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }

      .el-input__prefix {
        display: flex;
        align-items: center; // 垂直居中
        justify-content: center; // 水平居中
        padding: 0 12px; // 调整左右内边距

        .input-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          color: #909399; // 图标颜色
          font-size: 18px; // 图标大小
          // 确保图标垂直居中的关键设置
          height: 70%;
          line-height: 1; // 消除行高影响
        }
      }
    }

    .login-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
      border-radius: 8px;
      background: linear-gradient(90deg, #3b82f6, #8b5cf6);
      border-color: #409EFF;
      transition: all 0.3s ease;

      &:hover {
        background: linear-gradient(90deg, #8b5cf6, #3b82f6);
        border-color: #3688E7;
      }
    }
  }

  .login-footer {
    padding: 20px 40px;
    background: #f7f8fa;
    border-top: 1px solid #f0f0f0;

    .footer-links {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .footer-link {
      color: #606266;
      font-size: 14px;
      padding: 0 10px;
      transition: color 0.3s ease;

      &:hover {
        color: #409EFF;
      }
    }

    .link-separator {
      color: #e5e6eb;
    }
  }
}

// 响应式调整
@media (max-width: 576px) {
  .login-container {
    padding: 15px;
  }

  .login-box {
    width: 100%;
  }

  .login-main {
    padding: 25px 20px;
  }
}
</style>