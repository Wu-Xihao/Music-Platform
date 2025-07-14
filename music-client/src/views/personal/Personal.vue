<template>
  <div class="personal">
    <div class="personal-info">
      <div class="personal-img" @click="dialogTableVisible = true">
        <el-image fit="contain" :src="attachImageUrl(userPic)" class="profile-image"/>
      </div>
      <div class="personal-msg">
        <div class="username">{{ personalInfo.username }}</div>
        <div class="introduction">{{ personalInfo.introduction }}</div>
      </div>
      <el-button class="edit-info" round :icon="Edit" @click="goPage()">修改个人信息</el-button>
    </div>
    <div class="personal-body">
      <song-list :songList="collectSongList" :show="true" @changeData="changeData"></song-list>
    </div>
    <el-dialog v-model="dialogTableVisible" title="修改头像" class="avatar-dialog">
      <upload></upload>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, nextTick, ref, computed, watch, reactive } from "vue";
import { useStore } from "vuex";
import { Edit } from "@element-plus/icons-vue";
import SongList from "@/components/SongList.vue";
import Upload from "../setting/Upload.vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName } from "@/enums";

export default defineComponent({
  components: {
    SongList,
    Upload,
  },
  setup() {
    const store = useStore();

    const { routerManager } = mixin();

    const dialogTableVisible = ref(false);
    const collectSongList = ref([]); // 收藏的歌曲
    const personalInfo = reactive({
      username: "",
      userSex: "",
      birth: "",
      location: "",
      introduction: "",
    });
    const userId = computed(() => store.getters.userId);
    const userPic = computed(() => store.getters.userPic);
    watch(userPic, () => {
      dialogTableVisible.value = false;
    });

    function goPage() {
      routerManager(RouterName.Setting, { path: RouterName.Setting });
    }
    async function getUserInfo(id) {
      const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
      personalInfo.username = result.data[0].username;
      personalInfo.userSex = result.data[0].sex;
      personalInfo.birth = result.data[0].birth;
      personalInfo.introduction = result.data[0].introduction;
      personalInfo.location = result.data[0].location;
    }
    // 获取收藏的歌曲
    async function getCollection(userId) {
      collectSongList.value = []
      const result = (await HttpManager.getCollectionOfUser(userId)) as ResponseBody;
      const collectIDList = result.data || []; // 存放收藏的歌曲ID
      // 通过歌曲ID获取歌曲信息
      for (const item of collectIDList) {
        if (!item.songId) {
          console.error(`歌曲${item}异常`);
          continue;
        }

        const result = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        collectSongList.value.push(result.data[0]);
      }
    }

    function changeData() {
      getCollection(userId.value);
    }

    nextTick(() => {
      getUserInfo(userId.value);
      getCollection(userId.value);
    });

    return {
      Edit,
      userPic,
      dialogTableVisible,
      collectSongList,
      personalInfo,
      attachImageUrl: HttpManager.attachImageUrl,
      goPage,
      changeData,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.personal {
  padding-top: $header-height + 150px;
  position: relative;
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  min-height: 100vh;
  overflow: hidden;

  &::before {
    content: "";
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: absolute;
    top: 0;
    width: 100%;
    height: $header-height + 150px;
    z-index: -1;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
  }
}

.personal-info {
  position: relative;
  margin-bottom: 60px;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;

  .personal-img {
    height: 200px;
    width: 200px;
    border-radius: 50%;
    border: 5px solid rgba(255, 255, 255, 0.3);
    position: absolute;
    top: -180px;
    left: 50px;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow:
        0 15px 35px rgba(0, 0, 0, 0.25),
        0 5px 15px rgba(0, 0, 0, 0.1);
    z-index: 10;
    overflow: hidden;
    background: linear-gradient(45deg, #667eea, #764ba2);

    &:hover {
      transform: scale(1.08) translateY(-5px);
      box-shadow:
          0 20px 40px rgba(0, 0, 0, 0.3),
          0 10px 20px rgba(0, 0, 0, 0.15);
      border-color: rgba(255, 255, 255, 0.5);
    }

    &::after {
      content: "更换头像";
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: rgba(0, 0, 0, 0.6);
      color: white;
      padding: 8px;
      font-size: 14px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover::after {
      opacity: 1;
    }

    .profile-image {
      border-radius: 50%;
      object-fit: cover;
      width: 100%;
      height: 100%;
    }
  }

  .personal-msg {
    margin-left: 300px;
    position: absolute;
    top: -120px;
    color: $color-white;
    text-align: left;
    max-width: 60%;

    .username {
      font-size: 2.8rem;
      font-weight: 700;
      margin-bottom: 10px;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      letter-spacing: -0.5px;
      background: linear-gradient(45deg, #fff, #e0e0ff);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }

    .introduction {
      font-size: 1.2rem;
      font-weight: 400;
      opacity: 0.85;
      max-width: 600px;
      line-height: 1.6;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
      padding-right: 20px;
    }
  }

  .edit-info {
    position: absolute;
    right: 10vw;
    margin-top: -120px;
    background: linear-gradient(45deg, #6a11cb 0%, #2575fc 100%);
    color: white;
    font-weight: 600;
    padding: 12px 28px;
    transition: all 0.3s ease;
    border: none;
    box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(37, 117, 252, 0.6);
      background: linear-gradient(45deg, #5e0db4 0%, #1c68f0 100%);
    }

    &:active {
      transform: translateY(1px);
    }
  }
}

.personal-body {
  background-color: #f8f9fa;
  border-radius: 30px 30px 0 0;
  padding: 50px 40px;
  box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.08);
  min-height: 500px;
  position: relative;

  &::before {
    content: "";
    position: absolute;
    top: -20px;
    left: 5%;
    width: 90%;
    height: 40px;
    background: #f8f9fa;
    border-radius: 20px 20px 0 0;
    z-index: -1;
  }
}

.avatar-dialog {
  border-radius: 15px;
  overflow: hidden;
  max-width: 90vw;

  ::v-deep .el-dialog__header {
    background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
    color: $color-white;
    margin: 0;
    padding: 20px;
  }

  ::v-deep .el-dialog__title {
    color: $color-white;
    font-weight: 600;
    font-size: 1.4rem;
  }

  ::v-deep .el-dialog__body {
    padding: 30px;
  }
}

@media screen and (min-width: $sm) {
  .personal-body {
    padding: 50px 100px;
    margin: 0 20px;
  }
}

@media screen and (max-width: $sm) {
  .edit-info {
    position: relative !important;
    margin-top: 30px !important;
    right: auto !important;
    left: 50%;
    transform: translateX(-50%);
    margin-bottom: 20px;
  }

  .personal-info {
    padding-bottom: 40px;

    .personal-img {
      left: 50%;
      transform: translateX(-50%);
      height: 160px;
      width: 160px;
      top: -130px;
    }

    .personal-msg {
      margin-left: 0;
      width: 100%;
      text-align: center;
      top: 60px;
      position: relative;
      max-width: 90%;
      padding: 0 10px;

      .username {
        font-size: 1.8rem;
        margin-top: 20px;
      }

      .introduction {
        font-size: 1rem;
        padding: 0;
        margin: 0 auto;
        max-width: 90%;
      }
    }
  }

  .personal-body {
    padding: 30px 15px;
    margin-top: 100px;
    border-radius: 20px 20px 0 0;

    &::before {
      height: 30px;
      top: -15px;
    }
  }

  ::-webkit-scrollbar {
    width: 8px;
  }

  ::-webkit-scrollbar-track {
    background: #f8f9fa;
  }

  ::-webkit-scrollbar-thumb {
    background: linear-gradient(45deg, #6a11cb, #2575fc);
    border-radius: 4px;
  }

  ::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(45deg, #5e0db4, #1c68f0);
  }
}
</style>
