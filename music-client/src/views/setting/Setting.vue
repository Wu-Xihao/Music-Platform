<template>
  <div class="setting-container">
    <!-- 顶部标题区域 -->
    <div class="setting-header">
      <div class="header-content">
        <h1 class="setting-title">账户设置</h1>
        <p class="setting-subtitle">管理您的个人资料和安全设置</p>
        <div class="decoration">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
        </div>
      </div>
    </div>

    <!-- 标签页区域 -->
    <div class="tabs-container">
      <el-tabs tab-position="left" class="setting-tabs">
        <!-- 个人资料标签页 -->
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <div class="icon-wrapper">
                <el-icon :size="32" class="tab-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                    <path fill="currentColor" d="M288 320a224 224 0 1 0 448 0 224 224 0 1 0-448 0m544 608H160a32 32 0 0 1-32-32v-96a160 160 0 0 1 160-160h448a160 160 0 0 1 160 160v96a32 32 0 0 1-32 32z"></path>
                  </svg>
                </el-icon>
                <div class="icon-bg"></div>
              </div>
              <span>个人资料</span>
            </div>
          </template>
          <div class="tab-content">
            <div class="content-card">
              <Personal-data></Personal-data>
            </div>
          </div>
        </el-tab-pane>

        <!-- 更改密码标签页 -->
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <div class="icon-wrapper">
                <el-icon :size="32" class="tab-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                    <path fill="currentColor" d="M224 448a32 32 0 0 0-32 32v384a32 32 0 0 0 32 32h576a32 32 0 0 0 32-32V480a32 32 0 0 0-32-32zm0-64h576a96 96 0 0 1 96 96v384a96 96 0 0 1-96 96H224a96 96 0 0 1-96-96V480a96 96 0 0 1 96-96"></path>
                    <path fill="currentColor" d="M512 544a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V576a32 32 0 0 1 32-32m192-160v-64a192 192 0 1 0-384 0v64zM512 64a256 256 0 0 1 256 256v128H256V320A256 256 0 0 1 512 64"></path>
                  </svg>
                </el-icon>
                <div class="icon-bg"></div>
              </div>
              <span>更改密码</span>
            </div>
          </template>
          <div class="tab-content">
            <div class="content-card">
              <Password></Password>
            </div>
          </div>
        </el-tab-pane>

        <!-- 账号和安全标签页 -->
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <div class="icon-wrapper">
                <el-icon :size="32" class="tab-icon">
                  <svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                    <path d="M503.222 55.28a32 32 0 0 1 17.931 0.003l383.816 112.075A32 32 0 0 1 928 198.075v229.84C928 679.107 768.847 887.2 522.167 969.36a32 32 0 0 1-20.22 0.001C255.202 887.204 96 679.062 96 427.808V198.075a32 32 0 0 1 23.038-30.72z m8.956 64.055L160 222.073v205.735c0 218.482 135.324 399.774 348.986 476.278l3.068 1.079 3.065-1.078c211.424-75.723 346.112-254.06 348.839-469.498l0.042-6.675V222.067L512.178 119.335zM512 245.923c76.58 0 138.667 62.06 138.667 138.623 0 38.891-16.02 74.04-41.822 99.215 63.043 34.34 105.822 101.178 105.822 178.007 0 17.673-14.327 32-32 32-17.497 0-31.713-14.042-31.996-31.471l-0.004-0.53c0-76.542-62.08-138.597-138.667-138.597-75.821 0-137.424 60.82-138.648 136.306l-0.019 2.292c0 17.673-14.327 32-32 32-17.673 0-32-14.327-32-32 0-76.83 42.779-143.667 105.821-178.014-25.8-25.167-41.82-60.317-41.82-99.208 0-76.562 62.085-138.623 138.666-138.623z m0 64c-41.24 0-74.667 33.413-74.667 74.623 0 41.21 33.427 74.624 74.667 74.624s74.667-33.413 74.667-74.624c0-41.21-33.427-74.623-74.667-74.623z"></path>
                  </svg>
                </el-icon>
                <div class="icon-bg"></div>
              </div>
              <span>账号和安全</span>
            </div>
          </template>
          <div class="tab-content">
            <div class="security-section">
              <div class="security-card">
                <div class="security-header">
                  <div class="warning-icon-bg">
                    <el-icon :size="40" color="#fff">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                        <path fill="currentColor" d="M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896m0 192a58.432 58.432 0 0 0-58.24 63.744l23.36 256.384a35.072 35.072 0 0 0 69.76 0l23.296-256.384A58.432 58.432 0 0 0 512 256m0 512a51.2 51.2 0 1 0 0-102.4 51.2 51.2 0 0 0 0 102.4"></path>
                      </svg>
                    </el-icon>
                  </div>
                  <h3 class="security-title">账号安全警告</h3>
                </div>
                <p class="security-description">
                  此操作将永久删除您的账户和所有相关数据，且无法恢复。请谨慎操作。
                </p>
                <el-button
                    type="danger"
                    :icon="Delete"
                    @click="cancelAccount"
                    class="cancel-btn"
                    round
                >
                  注销账号
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed } from "vue";
import { Delete } from "@element-plus/icons-vue";
import PersonalData from "./PersonalData.vue";
import Password from "./Password.vue";
import { HttpManager } from "@/api";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { RouterName } from "@/enums";
import {ElMessageBox} from "element-plus";

export default defineComponent({
  components: {
    PersonalData,
    Password,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { routerManager } = mixin();

    const userId = computed(() => store.getters.userId);

    async function cancelAccount() {
      ElMessageBox.confirm('此操作将永久删除该账号，是否继续?','提示',{
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const result = (await HttpManager.deleteUser(userId.value)) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });
        routerManager(RouterName.SignIn, {path: RouterName.SignIn});
        proxy.$store.commit("setToken", false);
      }).catch(()=> {
        (proxy as any).$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    }

    return {
      Delete,
      cancelAccount,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

/* 添加全局隐藏滚动条 */
html, body {
  overflow-x: hidden;
  -ms-overflow-style: none;  /* IE和Edge */
  scrollbar-width: none;  /* Firefox */
}

body::-webkit-scrollbar {
  display: flex; /* Chrome, Safari, Opera */
}

.setting-container {
  background-color: $color-light-white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden; /* 修改为hidden */
  transition: all 0.3s ease;
  position: relative;
  max-width: calc(100% - 40px); /* 添加宽度限制 */
  margin: 30px auto;
  box-sizing: border-box; /* 添加盒模型 */

  &:before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 180px;
    background: linear-gradient(135deg, $color-blue 0%, $color-blue-dark 100%);
    z-index: 0;
    border-radius: 16px 16px 0 0;
  }
}


.setting-header {
  position: relative;
  padding: 40px 40px 30px;
  z-index: 1;

  .header-content {
    position: relative;
    z-index: 2;
  }

  .setting-title {
    font-size: 32px;
    font-weight: 700;
    color: white;
    margin: 0 0 8px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .setting-subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.85);
    margin: 0;
    font-weight: 500;
  }

  .decoration {
    position: absolute;
    top: 0;
    right: 0;
    height: 100%;
    width: 40%;
    overflow: hidden;

    .circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
    }

    .circle-1 {
      width: 120px;
      height: 120px;
      top: -40px;
      right: -20px;
    }

    .circle-2 {
      width: 80px;
      height: 80px;
      top: 20px;
      right: 100px;
    }

    .circle-3 {
      width: 50px;
      height: 50px;
      top: 70px;
      right: 40px;
    }
  }
}

.tabs-container {
  position: relative;
  z-index: 2;
}

.setting-tabs {
  min-height: 500px;
  background: white;
  border-radius: 16px;
  margin: 0 20px 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;

  :deep(.el-tabs__header) {
    width: 240px;
    background: linear-gradient(to bottom, rgba($color-blue-shallow, 0.1), rgba($color-blue-shallow, 0.05));
    padding: 20px 0;
    margin-right: 0;
  }
  :deep(.el-tabs__content) {
    overflow: visible; /* 允许内容区域可见 */
  }
  :deep(.el-tabs__item) {
    padding: 16px 30px;
    font-size: 15px;
    color: $color-dark-grey;
    transition: all 0.3s ease;
    height: auto;
    line-height: 1.5;
    font-weight: 500;

    &:hover {
      color: $color-blue;
      background-color: rgba($color-blue, 0.08);

      .icon-bg {
        background: rgba($color-blue, 0.1);
      }

      .tab-icon {
        color: $color-blue;
      }
    }

    &.is-active {
      color: $color-blue;
      font-weight: 600;
      background-color: rgba($color-blue, 0.1);

      .icon-bg {
        background: rgba($color-blue, 0.15);
      }

      .tab-icon {
        color: $color-blue;
      }
    }
  }

  :deep(.el-tabs__active-bar) {
    background-color: $color-blue;
    width: 4px;
    left: 0;
  }

  :deep(.el-tabs__nav-wrap)::after {
    display: none;
  }
}

.tab-label {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.icon-wrapper {
  position: relative;
  margin-right: 15px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;

  .icon-bg {
    position: absolute;
    width: 100%;
    height: 100%;
    border-radius: 12px;
    background: rgba($color-grey, 0.1);
    z-index: 0;
    transition: all 0.3s ease;
  }

  .tab-icon {
    position: relative;
    z-index: 1;
    transition: all 0.3s ease;
    color: $color-grey;
  }
}

.tab-content {
  padding: 30px 40px;
  background: white;
  min-height: 500px;
}

.content-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba($color-light-grey, 0.5);
}

.security-section {
  max-width: 600px;
  margin: 0 auto;

  .security-card {
    background: white;
    border-radius: 16px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
    padding: 35px;
    border: 1px solid rgba($color-danger, 0.1);
    position: relative;
    overflow: hidden;

    &:before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, $color-danger, $color-warning);
    }
  }

  .security-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }

  .warning-icon-bg {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, $color-danger 0%, $color-warning 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    box-shadow: 0 4px 12px rgba($color-danger, 0.2);
  }

  .security-title {
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    color: $color-danger;
  }

  .security-description {
    font-size: 15px;
    color: $color-grey;
    margin-bottom: 30px;
    line-height: 1.7;
    padding: 0 10px;
    background: rgba($color-light-grey, 0.1);
    border-radius: 10px;
    padding: 15px;
    position: relative;

    &:before {
      content: "''";
      position: absolute;
      top: -15px;
      left: 20px;
      font-size: 60px;
      color: rgba($color-danger, 0.1);
      font-family: serif;
      line-height: 1;
    }
  }

  .cancel-btn {
    padding: 14px 36px;
    font-weight: 600;
    font-size: 16px;
    letter-spacing: 0.5px;
    transition: all 0.3s ease;
    background: linear-gradient(135deg, $color-danger 0%, $color-warning 100%);
    border: none;
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
      transform: translateY(-3px);
      box-shadow: 0 6px 18px rgba($color-danger, 0.3);

      &:before {
        left: 100%;
      }
    }

    &:active {
      transform: translateY(0);
    }
  }
}

@media screen and (min-width: $sm) {
  .setting-container {
    margin: 40px auto;
  }
}

@media screen and (max-width: $sm) {
  .setting-container {
    margin: 0;
    border-radius: 0;
    max-width: 100%; /* 在小屏幕上占满宽度 */

    &:before {
      border-radius: 0;
    }
  }

  .setting-header {
    padding: 30px 15px 15px; /* 调整内边距 */
  }

  .setting-tabs {
    margin: 0 10px 10px;

    :deep(.el-tabs__header) {
      width: 100%; /* 小屏幕上占满宽度 */
      padding: 10px 0;
    }

    :deep(.el-tabs__nav) {
      width: 100%;
    }

    :deep(.el-tabs__item) {
      width: 100%;
      box-sizing: border-box;
    }
  }

  .tab-content {
    padding: 15px;
  }

  .icon-wrapper {
    width: 40px;
    height: 40px;
    margin-right: 10px;
  }

  .security-section {
    .security-card {
      padding: 25px 20px;
    }

    .security-description {
      padding: 12px;
    }
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.content-card, .security-card {
  animation: fadeIn 0.5s ease-out;
}

.tab-label {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
</style>
