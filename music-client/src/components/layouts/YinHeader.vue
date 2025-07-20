<template>
  <div class="header">
    <!--图标-->
    <div class="logo" @click="goPage()">
      <el-icon class="logo-icon"><Headset /></el-icon>
      <span>{{ musicName }}</span>
    </div>

    <yin-header-nav class="yin-header-nav"
                    :styleList="headerNavList"
                    :activeName="activeNavName"
                    @click="goPage"></yin-header-nav>

    <!--搜索框-->
    <div class="header-search">
      <el-input placeholder="搜索"
                :prefix-icon="Search"
                v-model="keywords"
                @keyup.enter="goSearch()" />
    </div>

    <!--用户区域-->
    <div class="header-right">
      <yin-header-nav v-if="!token"
                      :styleList="signList"
                      :activeName="activeNavName"
                      @click="goPage"></yin-header-nav>

      <div class="header-user-con" v-if="token">
        <div class="user-avator">
          <el-image class="user" fit="cover" :src="attachImageUrl(userPic)" />
          <span class="online-indicator"></span>
        </div>
        <el-dropdown class="user-name" trigger="click" @command="goMenuList">
          <span class="el-dropdown-link">
            {{ username }}
            <i class="el-icon-caret-bottom"></i>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="(item, index) in menuList"
                                :key="index"
                                :command="item.path">
                {{ item.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, reactive } from "vue";
import { Search } from "@element-plus/icons-vue";
import { useStore } from "vuex";
// import YinIcon from "./YinIcon.vue";
import { Headset } from "@element-plus/icons-vue";
import YinHeaderNav from "./YinHeaderNav.vue";
import mixin from "@/mixins/mixin";
import { HEADERNAVLIST, SIGNLIST, MENULIST, Icon, MUSICNAME, RouterName, NavName } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    Headset,
    YinHeaderNav,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { changeIndex, routerManager } = mixin();

    const musicName = ref(MUSICNAME);
    const headerNavList = ref(HEADERNAVLIST);
    const signList = ref(SIGNLIST);
    const menuList = ref(MENULIST);
    const iconList = reactive({ ERJI: Icon.ERJI });
    const keywords = ref("");
    const activeNavName = computed(() => store.getters.activeNavName);
    const userPic = computed(() => store.getters.userPic);
    const token = computed(() => store.getters.token);
    const username = computed(() => store.getters.username || "用户"); // 获取用户名

    function goPage(path?, name?) {
      if (!path && !name) {
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        changeIndex(name);
        routerManager(path, { path });
      }
    }

    function goMenuList(path) {
      if (path == RouterName.SignOut) {
        proxy.$store.commit("setToken", false);
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        routerManager(path, { path });
      }
    }

    function goSearch() {
      if (keywords.value !== "") {
        proxy.$store.commit("setSearchWord", keywords.value);
        routerManager(RouterName.Search, {
          path: RouterName.Search,
          query: { keywords: keywords.value }
        });
      } else {
        (proxy as any).$message({
          message: "搜索内容不能为空",
          type: "error",
        });
      }
    }

    return {
      musicName,
      headerNavList,
      signList,
      menuList,
      iconList,
      keywords,
      activeNavName,
      userPic,
      token,
      username,
      Search,
      goPage,
      goMenuList,
      goSearch,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped lang="scss">
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.header {
  position: fixed;
  z-index: 100;
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  font-size: 16px;
  padding: 0 20px;
  color: #333;
  background: linear-gradient(90deg, #f8fafc 0%, #f1f5f9 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

/* LOGO */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  height: 40px;
  font-weight: 600;
  color: #3b82f6;
  letter-spacing: 0.5px;
  cursor: pointer;
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  margin-left: 40px;
}

.logo-icon {
  font-size: 25px;
  width: 25px;
  height: 25px;
  color: #3b82f6;
}

.logo span {
  font-size: 25px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.yin-header-nav {
  flex: 1;
  margin-left: 30px;
}

/* 搜索框 */
.header-search {
  margin: 0 40px;
  width: 100%;
  max-width: 300px;
}

.header-search::v-deep input {
  text-indent: 5px;
  border-radius: 20px;
  box-shadow: none;
  background-color: $color-light-grey;
  color: $color-black;
}

/* 右侧用户区域 */
.header-right {
  min-width: 180px;
  justify-content: flex-end;
  margin-right: 40px;
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

.user {
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

/* 响应式调整 */
@media (max-width: 768px) {
  .logo span {
    display: none;
  }

  .logo {
    border-right: none;
    padding: 0 8px;
  }

  .header-search {
    display: none;
  }
}
</style>