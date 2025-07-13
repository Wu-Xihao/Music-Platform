<template>
  <div class="login-logo">
    <yin-icon :icon="icon"></yin-icon>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import YinIcon from "./YinIcon.vue";
import { Icon } from "@/enums";

export default defineComponent({
  components: {
    YinIcon,
  },
  data() {
    return {
      icon: Icon.ERJI,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";


@media screen and (min-width: $sm) {
  .login-logo {
    width: 50vw;
  }
}

@media screen and (max-width: $sm) {
  .login-logo {
    width: 100vw;
  }
}
</style>
