
<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选关键词"></el-input>
      <el-button type="primary" @click="centerDialogVisible = true">添加歌单</el-button>
      <el-button type="primary" @click="exportPlaylist">导出歌单</el-button>
    </div>
    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="歌单图片" width="110" align="center">
        <template v-slot="scope">
          <img :src="attachImageUrl(scope.row.pic)" style="width: 80px"/>
          <el-upload :action="uploadUrl(scope.row.id)" :show-file-list="false" :on-success="handleImgSuccess"
                     :before-upload="beforeImgUpload">
            <el-button>更新图片</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200"></el-table-column>
      <el-table-column label="简介">
        <template v-slot="scope">
          <p style="height: 100px; overflow: scroll">
            {{ scope.row.introduction }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="风格" prop="style" width="100"></el-table-column>
      <el-table-column label="内容" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goContentPage(scope.row.id)">内容</el-button>
        </template>
      </el-table-column>
      <el-table-column label="评论" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goCommentPage(scope.row.id)">评论</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template v-slot="scope">
          <el-button @click="editRow(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="tableData.length"
        @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>

  <!--添加歌单-->
  <el-dialog title="添加歌单" v-model="centerDialogVisible">
    <el-form label-width="70px" :model="registerForm">
      <el-form-item label="歌单名" prop="title">
        <el-input v-model="registerForm.title"></el-input>
      </el-form-item>
      <el-form-item label="歌单介绍" prop="introduction">
        <el-input v-model="registerForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="风格" prop="style">
        <el-input v-model="registerForm.style"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addsongList">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 编辑弹出框 -->
  <el-dialog title="编辑" v-model="editVisible">
    <el-form :model="editForm">
      <el-form-item label="标题">
        <el-input v-model="editForm.title"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input type="textarea" v-model="editForm.introduction"></el-input>
      </el-form-item>
      <el-form-item label="风格">
        <el-input v-model="editForm.style"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveEdit">确 定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除提示框 -->
  <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, reactive, ref, watch} from "vue";
import mixin from "@/mixins/mixin";
import {HttpManager} from "@/api/index";
import {RouterName} from "@/enums";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import axios from 'axios';
export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const {proxy} = getCurrentInstance();
    const {routerManager, beforeImgUpload} = mixin();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const pageSize = ref(5); // 页数
    const currentPage = ref(1); // 当前页

    // 计算当前表格中的数据
    const data = computed(() => {
      return tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = [];
        for (let item of tempDate.value) {
          if (item.title.includes(searchWord.value)) {
            tableData.value.push(item);
          }
        }
      }
    });

    getData();




    // 获取歌单信息
    async function getData() {
      tableData.value = [];
      tempDate.value = [];
      const result = (await HttpManager.getSongList()) as ResponseBody;
      tableData.value = result.data;
      tempDate.value = result.data;
      currentPage.value = 1;
    }

    function exportPlaylist() {
      axios({
        method: 'get',
        url: 'http://localhost:8888/excle',
        responseType: 'blob', // 设置响应类型为blob
      })
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'SongList.xlsx'); // 设置下载的文件名
          document.body.appendChild(link);
          link.click();
          link.remove();
        })
        .catch((error) => {
          console.error('导出歌单失败：', error);
        });
  }

    // 获取当前页
    function handleCurrentChange(val) {
      currentPage.value = val;
    }

    function uploadUrl(id) {
      return HttpManager.attachImageUrl(`/songList/img/update?id=${id}`);
    }

    // 更新图片
    function handleImgSuccess(response, file) {
      (proxy as any).$message({
        message: response.message,
        type: response.type,
      });
      if (response.success) getData();
    }

    /**
     * 路由
     */
    function goContentPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.SongList,
          name: "歌单管理",
        },
        {
          path: "",
          name: "歌单内容",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.ListSong, {path: RouterName.ListSong, query: {id}});
    }

    function goCommentPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.SongList,
          name: "歌单管理",
        },
        {
          path: "",
          name: "评论详情",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.Comment, {path: RouterName.Comment, query: {id, type: 1}});
    }

    /**
     * 添加
     */
    const centerDialogVisible = ref(false);
    const registerForm = reactive({
      title: "",
      introduction: "",
      style: "",
    });

    async function addsongList() {
      let title = registerForm.title;
      let introduction = registerForm.introduction;
      let style = registerForm.style;
      const result = (await HttpManager.setSongList({title, introduction, style})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) {
        getData();
        registerForm.title = "";
        registerForm.introduction = "";
        registerForm.style = "";
      }
      centerDialogVisible.value = false;
    }

    /**
     * 编辑
     */
    const editVisible = ref(false);
    const editForm = reactive({
      id: "",
      title: "",
      pic: "",
      introduction: "",
      style: "",
    });

    function editRow(row) {
      idx.value = row.id;
      editForm.id = row.id;
      editForm.title = row.title;
      editForm.pic = row.pic;
      editForm.introduction = row.introduction;
      editForm.style = row.style;
      editVisible.value = true;
    }

    async function saveEdit() {

      let id = editForm.id;
      let title = editForm.title;
      let introduction = editForm.introduction;
      let style = editForm.style;

      const result = (await HttpManager.updateSongListMsg({id, title, introduction, style})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.success) getData();
      editVisible.value = false;
    }

    /**
     * 删除
     */
    const idx = ref(-1); // 记录当前要删除的行
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    async function confirm() {
      const result = await HttpManager.deleteSongList(idx.value) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.success) getData();
      delVisible.value = false;
    }

    function deleteRow(id) {
      idx.value = id;
      delVisible.value = true;
    }

    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }

    function deleteAll() {
      for (let item of multipleSelection.value) {
        deleteRow(item.id);
        confirm();
      }
      multipleSelection.value = [];
    }

    return {
      searchWord,
      data,
      tableData,
      centerDialogVisible,
      editVisible,
      delVisible,
      pageSize,
      currentPage,
      registerForm,
      editForm,
      addsongList,
      deleteAll,
      handleSelectionChange,
      handleImgSuccess,
      beforeImgUpload,
      saveEdit,
      attachImageUrl: HttpManager.attachImageUrl,
      uploadUrl,
      editRow,
      handleCurrentChange,
      exportPlaylist,
      confirm,
      deleteRow,
      goContentPage,
      goCommentPage,
    };
  },
});
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-table img {
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 所有按钮统一圆角、渐变背景 */
.el-button {
  background: linear-gradient(135deg, #fbc2eb, #a6c1ee);
  border: none;
  color: #fff;
  border-radius: 10px;
  padding: 10px 18px;
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
  transition: all 0.3s ease;
}

/* 悬停状态 */
.el-button:hover {
  background: linear-gradient(135deg, #a6c1ee, #fbc2eb);
  box-shadow: 0 6px 20px rgba(255, 182, 193, 0.4);
}

/* 主按钮（type="primary"）加一点光感 */
.el-button--primary {
  background: linear-gradient(to right, #a18cd1, #fbc2eb);
  font-weight: bold;
}

/* danger 类型按钮 */
.el-button--danger {
  background: linear-gradient(to right, #ff758c, #ff7eb3);
}

/* 点击状态反馈 */
.el-button:active {
  transform: scale(0.98);
}
✅ 二、滚动条（全局美化，适配所有滚动区域）
针对 .el-table__body-wrapper, .el-dialog__body, .container 等滚动区域

💅 推荐 CSS：
css
复制
编辑
  /* 滚动条 - 统一样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}


::-webkit-scrollbar {
  width: 0 !important;
}
::-webkit-scrollbar {
  width: 0 !important;height: 0;
}

</style>

