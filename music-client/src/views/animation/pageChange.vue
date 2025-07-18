<template>
  <div class="auth-container">
    <!-- 路由出口承载动画 -->
    <transition :name="transitionName" mode="out-in">
      <router-view class="auth-view"/>
    </transition>
  </div>
</template>

<script>
export default {
  data() {
    return {
      transitionName: 'slide-left'
    }
  },
  watch: {
    '$route'(to, from) {
      // 通过meta.index判断切换方向
      const toIndex = to.meta.index || 0
      const fromIndex = from.meta.index || 0
      this.transitionName = toIndex > fromIndex ? 'slide-left' : 'slide-right'
    }
  }
}
</script>

<style scoped>
.auth-container {
  box-sizing: border-box;
  width: 100%;
  height: 100vh;
  background-position: center;
  background-repeat: no-repeat;
  justify-content: center;
  align-items: center;
  background-size: cover;
  position: fixed;
  overflow: hidden;
  display: flex;
}

.auth-view {
  width: 100%;
  height: 100%;
  display: flex; /* 新增：启用flex布局 */
  justify-content: center; /* 新增：水平居中 */
  align-items: center; /* 新增：垂直居中 */
  transition: all 0.5s ease; /* 优化：缩短过渡时间 */
}
/* 优化动画效果 */
.slide-left-enter-active,
.slide-right-enter-active,
.slide-left-leave-active,
.slide-right-leave-active {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1; /* 确保活动页面在顶层 */
}

.slide-left-enter-from {
  transform: translateX(100%);
  opacity: 0;
}
.slide-left-leave-to {
  transform: translateX(-30%);
  opacity: 0.5;
}

.slide-right-enter-from {
  transform: translateX(-30%);
  opacity: 0.5;
}
.slide-right-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
