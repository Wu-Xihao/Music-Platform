<template>
  <el-breadcrumb class="crumbs" separator="/">
    <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.name" :to="{ path: item.path, query: item.query }">
      {{ item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>

  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input placeholder="筛选收藏信息" v-model="searchWord"></el-input>
    </div>
    <el-table height="600px" border size="small" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column prop="collector" label="收藏者ID"></el-table-column>
      <el-table-column prop="collectionName" label="收藏内容"></el-table-column>
      <el-table-column prop="type" label="类型"></el-table-column>
      <el-table-column label="收藏时间">
        <template #default="{ row }">
          {{ formatDate(row.collectTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center">
        <template v-slot="scope">
          <el-button type="danger" @click="deleteRow(scope.row.userId, scope.row.songId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 删除提示框 -->
  <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, computed } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";

interface ResponseBody {
  code: string;
  success: boolean;
  message: string;
  type: string;
}

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const tableData = ref([]); // 记录收藏信息，用于显示
    const tempDate = ref([]); // 记录收藏信息，用于搜索时能临时记录一份收藏信息列表
    const breadcrumbList = computed(() => store.getters.breadcrumbList);

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = [];
        for (let item of tempDate.value) {
          // 将 item.collector 转换为字符串类型
          const collectorStr = String(item.collector);
          const collectionNameStr = String(item.collectionName);
          const typeStr = String(item.type);

          if (
              collectorStr.includes(searchWord.value) ||
              collectionNameStr.includes(searchWord.value) ||
              typeStr.includes(searchWord.value)
          ) {
            tableData.value.push(item);
          }
        }
      }
    });

    getData();

    // 获取所有用户的收藏信息
    async function getData() {
      tableData.value = [];
      tempDate.value = [];
      const allUsers = (await HttpManager.getAllUser()) as any;
      for (let user of allUsers.data) {
        const collections = (await HttpManager.getCollectionOfUser(user.id)) as any;
        for (let collection of collections.data) {
          const song = await HttpManager.getSongOfId(collection.songId) as any;
          const item = {
            userId: user.id,
            songId: collection.songId,
            collector: user.id,
            collectionName: song.data[0].name,
            type: "歌曲",
            collectTime: collection.createTime,
          };
          tableData.value.push(item);
          tempDate.value.push(item);
        }
      }
    }

    /**
     * 删除
     */
    const userIdToDelete = ref(""); // 记录当前要删除的用户 ID
    const songIdToDelete = ref(""); // 记录当前要删除的歌曲 ID
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    async function confirm() {
      const result = (await HttpManager.deleteCollection(userIdToDelete.value, songIdToDelete.value)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) getData();
      delVisible.value = false;
    }
    function deleteRow(userId, songId) {
      userIdToDelete.value = userId;
      songIdToDelete.value = songId;
      delVisible.value = true;
    }
    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }
    function deleteAll() {
      for (let item of multipleSelection.value) {
        deleteRow(item.userId, item.songId);
        confirm();
      }
      multipleSelection.value = [];
    }

    const formatDate = (timestamp: number) => {
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    };

    return {
      searchWord,
      tableData,
      delVisible,
      breadcrumbList,
      deleteAll,
      handleSelectionChange,
      deleteRow,
      confirm,
      formatDate,
    };
  },
});
</script>

<style scoped></style>