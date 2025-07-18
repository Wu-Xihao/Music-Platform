<template>
  <div class="header">
    <div class="collapse-btn" @click="collapseChage">
      <el-icon v-if="!collapse"><expand /></el-icon>
      <el-icon v-else><fold /></el-icon>
    </div>
    <div class="logo">
      <el-icon class="logo-icon"><Headset /></el-icon>
      <span>{{ musicName }}</span>
    </div>
    <div class="header-right">
      <div class="header-user-con">
        <div class="user-avator">
          <img :src="attachImageUrl(userPic)" />
          <span class="online-indicator"></span>
        </div>
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ username }}
            <i class="el-icon-caret-bottom"></i>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="loginout">
                <el-icon class="menu-icon"><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, computed, ref, onMounted } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { Expand, Fold, Headset, SwitchButton } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";
import { HttpManager } from "@/api";
import { RouterName, MUSICNAME } from "@/enums";

export default defineComponent({
  components: {
    Expand,
    Fold,
    Headset,
    SwitchButton
  },
  setup() {
    const { routerManager } = mixin();
    const store = useStore();

    const collapse = ref(true);
    const username = ref("admin");
    const userPic = computed(() => store.getters.userPic);
    const musicName = ref(MUSICNAME);

    onMounted(() => {
      if (document.body.clientWidth < 1500) {
        collapseChage();
      }
    });

    // 侧边栏折叠
    function collapseChage() {
      collapse.value = !collapse.value;
      emitter.emit("collapse", collapse.value);
    }
    // 用户名下拉菜单选择事件
    function handleCommand(command) {
      if (command === "loginout") {
        routerManager(RouterName.SignIn, { path: RouterName.SignIn });
      }
    }
    return {
      musicName,
      username,
      userPic,
      collapse,
      collapseChage,
      handleCommand,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>
<style scoped>
.header {
  position: absolute;
  z-index: 100;
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
  background: linear-gradient(90deg, #f8fafc 0%, #f1f5f9 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

/* 折叠按钮样式 */
.collapse-btn {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #64748b;
}

.collapse-btn:hover {
  background-color: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  transform: scale(1.1);
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  height: 40px;
  font-weight: 600;
  color: #3b82f6;
  letter-spacing: 0.5px;
}

.logo-icon {
  font-size: 22px;
  color: #3b82f6;
}

.logo span {
  font-size: 20px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 右侧用户区域 */
.header-right {
  position: absolute;
  right: 0;
  padding: 0 24px;
}

.header-user-con {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 用户头像 */
.user-avator {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid transparent;
  background: linear-gradient(white, white) padding-box,
  linear-gradient(135deg, #3b82f6, #8b5cf6) border-box;
  transition: transform 0.2s ease;
}

.user-avator:hover {
  transform: scale(1.05);
}

.user-avator img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 在线状态指示器 */
.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background-color: #10b981;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 0 0 1px rgba(16, 185, 129, 0.2);
}

/* 用户名和下拉菜单 */
.user-name {
  position: relative;
  color: #334155;
  font-weight: 500;
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.el-dropdown-link:hover {
  background-color: rgba(59, 130, 246, 0.08);
}

.el-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  overflow: hidden;
}

.el-dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  transition: all 0.2s ease;
}

.el-dropdown-item:hover {
  background-color: #f1f5f9;
  color: #3b82f6;
}

.menu-icon {
  width: 16px;
  height: 16px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .logo span {
    display: none;
  }

  .logo {
    border-right: none;
    padding: 0 8px;
  }
}
</style>