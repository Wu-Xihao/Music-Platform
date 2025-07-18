<template>
  <div class="sidebar transition-all duration-300 ease-in-out" :class="{ 'sidebar-collapsed': collapse }">
    <el-menu
        class="sidebar-el-menu"
        background-color="transparent"
        text-color="#334155"
        active-text-color="#1e40af"
        default-active="info"
        router
        :collapse="collapse"
        :collapse-transition="false"
    >
      <el-menu-item index="info">
        <el-icon class="menu-icon"><PieChart /></el-icon>
        <span class="menu-text">系统首页</span>
      </el-menu-item>
      <el-menu-item index="consumer">
        <el-icon class="menu-icon"><User /></el-icon>
        <span class="menu-text">用户管理</span>
      </el-menu-item>
      <el-menu-item index="singer">
        <el-icon class="menu-icon"><Mic /></el-icon>
        <span class="menu-text">歌手管理</span>
      </el-menu-item>
      <el-menu-item index="songList">
        <el-icon class="menu-icon"><Document /></el-icon>
        <span class="menu-text">歌单管理</span>
      </el-menu-item>
      <el-menu-item index="AllSongs">
        <el-icon class="menu-icon"><Headset /></el-icon>
        <span class="menu-text">歌曲管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { PieChart, Mic, Document, User, Headset } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";

const collapse = ref(false);

const handleCollapse = (msg: boolean) => {
  collapse.value = msg;
};

onMounted(() => {
  emitter.on("collapse", handleCollapse);
});

onBeforeUnmount(() => {
  emitter.off("collapse", handleCollapse);
});
</script>

<style scoped>
/* 整体侧边栏样式 */
.sidebar {
  display: block;
  position: absolute;
  left: 0;
  top: 60px;
  bottom: 0;
  width: 240px;
  background: linear-gradient(160deg, #f5f9ff, #edf2ff);
  z-index: 100;
  transition: all 0.4s cubic-bezier(0.22, 0.61, 0.36, 1);
  padding: 24px 16px;
  box-shadow:
      inset 0 0 40px rgba(255, 255, 255, 0.7),
      0 0 30px rgba(100, 130, 246, 0.1);
  border-right: 1px solid rgba(226, 232, 240, 0.6);
}

/* 折叠状态下居中处理 - 关键修改 */
.sidebar-collapsed {
  width: 80px;
  display: flex; /* 新增 */
  justify-content: center; /* 新增 */
}

/* 滚动条美化 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(241, 245, 249, 0.4);
  border-radius: 10px;
}

.sidebar::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #a3bffa, #7f9cf5);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

/* 菜单整体样式 */
.sidebar-el-menu {
  border-right: none;
  background-color: transparent;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 折叠状态下菜单容器居中 */
.sidebar-collapsed .sidebar-el-menu {
  align-items: center; /* 新增 */
}

.sidebar-el-menu .el-menu-item {
  height: 68px;
  margin-bottom: 0;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow:
      0 4px 12px rgba(100, 116, 139, 0.08),
      inset 0 1px 1px rgba(255, 255, 255, 0.9);
  transition:
      transform 0.3s ease,
      box-shadow 0.3s ease,
      background 0.3s ease;
  display: flex;
  align-items: center;
  backdrop-filter: blur(6px);
  position: relative;
  overflow: hidden;
  z-index: 1;
  font-weight: 500;
  border: 1px solid rgba(226, 232, 240, 0.4);
}

/* 菜单项悬停效果 */
.sidebar-el-menu .el-menu-item:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow:
      0 7px 20px rgba(100, 130, 246, 0.25),
      inset 0 2px 2px rgba(255, 255, 255, 0.95);
  border-color: rgba(165, 180, 252, 0.6);
}

/* 水波纹效果 */
.sidebar-el-menu .el-menu-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(199, 210, 254, 0.4), transparent);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: width 0.6s ease, height 0.6s ease, opacity 0.6s ease;
  z-index: -1;
}

.sidebar-el-menu .el-menu-item:hover::after {
  width: 160px;
  height: 160px;
  opacity: 1;
}

/* 激活状态效果 */
.sidebar-el-menu .el-menu-item.is-active {
  background: linear-gradient(120deg, rgba(219, 234, 254, 0.9), rgba(224, 231, 255, 0.9));
  color: #1e40af;
  box-shadow:
      0 6px 18px rgba(59, 130, 246, 0.25),
      inset 0 1px 1px rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(129, 140, 248, 0.3);
}

.sidebar-el-menu .el-menu-item.is-active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 28px;
  width: 4px;
  background: linear-gradient(to bottom, #4f46e5, #818cf8);
  border-radius: 0 4px 4px 0;
  box-shadow: 0 0 8px rgba(79, 70, 229, 0.3);
}

/* 图标样式 */
.menu-icon {
  font-size: 24px;
  width: 28px;
  text-align: center;
  margin-left: 10px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  color: #64748b;
  z-index: 2;
}

.sidebar-el-menu .el-menu-item.is-active .menu-icon,
.sidebar-el-menu .el-menu-item:hover .menu-icon {
  transform: scale(1.2);
  color: #4f46e5;
  filter: drop-shadow(0 1px 1px rgba(79, 70, 229, 0.1));
}

/* 文字样式 */
.menu-text {
  font-size: 16px;
  font-weight: 500;
  margin-left: 10px;
  transition: all 0.3s ease, opacity 0.3s ease;
  letter-spacing: 0.3px;
  z-index: 2;
  position: relative;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
}

.sidebar-el-menu .el-menu-item.is-active .menu-text {
  color: #312e81;
  font-weight: 550;
}

/* 折叠状态优化 - 关键修改 */
.sidebar-collapsed .sidebar-el-menu .el-menu-item {
  justify-content: center;
  padding: 0 !important;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  margin: 0 0 10px 0; /* 修改此处：移除左右auto，设置为0 */
}

.sidebar-collapsed .menu-text {
  display: none;
  position: absolute;
  left: 80px;
  background: white;
  padding: 10px 16px;
  border-radius: 10px;
  box-shadow:
      0 6px 20px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(226, 232, 240, 0.4);
  opacity: 0;
  transform: translateX(-15px);
  transition: opacity 0.3s ease, transform 0.3s ease;
  pointer-events: none;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 500;
  text-shadow: none;
}

.sidebar-collapsed .menu-text::before {
  content: '';
  position: absolute;
  left: -5px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid white;
}

.sidebar-collapsed .sidebar-el-menu .el-menu-item:hover .menu-text {
  display: block;
  opacity: 1;
  transform: translateX(0);
  z-index: 1000;
}

/* 折叠状态图标效果 */
.sidebar-collapsed .menu-icon {
  margin-left: 0;
  transform: scale(1);
  font-size: 22px;
}

.sidebar-collapsed .sidebar-el-menu .el-menu-item:hover .menu-icon {
  transform: scale(1.3) rotate(5deg);
}

/* 微光点缀 */
.sidebar::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(199, 210, 254, 0.1), transparent 70%);
  opacity: 0.5;
  pointer-events: none;
  z-index: -1;
  animation: rotateLight 40s linear infinite;
}

@keyframes rotateLight {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 菜单项顶部装饰 */
.sidebar-el-menu .el-menu-item::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s ease;
}

.sidebar-el-menu .el-menu-item:hover::after {
  transform: scaleX(1);
}

/* 响应式微调 */
@media (max-height: 700px) {
  .sidebar-el-menu .el-menu-item {
    height: 60px;
    margin-bottom: 10px;
  }
}
</style>