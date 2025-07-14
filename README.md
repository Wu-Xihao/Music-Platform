# Music-Platform

## 数据库

### 1. 管理员表（`admin`）

| 字段名   | 类型             | 说明                 |
| -------- | ---------------- | -------------------- |
| id       | INT (主键, 自增) | 管理员唯一标识       |
| name     | VARCHAR(255)     | 管理员用户名（唯一） |
| password | VARCHAR(45)      | 登录密码             |



------

### 2. 轮播图表（`banner`）

| 字段名 | 类型             | 说明           |
| ------ | ---------------- | -------------- |
| id     | INT (主键, 自增) | 轮播图唯一标识 |
| pic    | VARCHAR(255)     | 图片 URL 路径  |



------

### 3. 收藏表（`collect`）

| 字段名       | 类型             | 说明                       |
| ------------ | ---------------- | -------------------------- |
| id           | INT (主键, 自增) | 收藏记录唯一标识           |
| user_id      | INT              | 用户 ID                    |
| type         | TINYINT          | 收藏类型（0=歌曲，1=歌单） |
| song_id      | INT (可空)       | 收藏的歌曲 ID              |
| song_list_id | INT (可空)       | 收藏的歌单 ID              |
| create_time  | DATETIME         | 收藏时间                   |



------

### 4. 评论表（`comment`）

| 字段名       | 类型             | 说明                   |
| ------------ | ---------------- | ---------------------- |
| id           | INT (主键, 自增) | 评论唯一标识           |
| user_id      | INT              | 用户 ID                |
| song_id      | INT (可空)       | 歌曲 ID                |
| song_list_id | INT (可空)       | 歌单 ID                |
| content      | VARCHAR(255)     | 评论内容               |
| create_time  | DATETIME         | 评论时间               |
| type         | TINYINT          | 类型（0=歌曲，1=歌单） |
| up           | INT (默认 0)     | 点赞数                 |



------

### 5. 用户表（`consumer`）

| 字段名       | 类型             | 说明               |
| ------------ | ---------------- | ------------------ |
| id           | INT (主键, 自增) | 用户唯一标识       |
| username     | VARCHAR(255)     | 用户名（唯一）     |
| password     | VARCHAR(100)     | 密码               |
| sex          | TINYINT          | 性别（0=女，1=男） |
| phone_num    | CHAR(15)         | 电话（唯一）       |
| email        | CHAR(30)         | 邮箱（唯一）       |
| birth        | DATETIME         | 出生日期           |
| introduction | VARCHAR(255)     | 个人简介           |
| location     | VARCHAR(45)      | 地区               |
| avator       | VARCHAR(255)     | 头像路径           |
| create_time  | DATETIME         | 注册时间           |
| update_time  | DATETIME         | 最后更新时间       |



------

### 6. 歌单-歌曲关联表（`list_song`）

| 字段名       | 类型             | 说明         |
| ------------ | ---------------- | ------------ |
| id           | INT (主键, 自增) | 关系唯一标识 |
| song_id      | INT              | 歌曲 ID      |
| song_list_id | INT              | 歌单 ID      |



------

### 7. 歌单评分表（`rank_list`）

| 字段名       | 类型                | 说明                        |
| ------------ | ------------------- | --------------------------- |
| id           | BIGINT (主键, 自增) | 评分记录标识                |
| song_list_id | BIGINT              | 歌单 ID                     |
| consumer_id  | BIGINT              | 用户 ID                     |
| score        | INT (0-10)          | 评分值                      |
| 唯一约束     |                     | (consumer_id, song_list_id) |



------

### 8. 歌手表（`singer`）

| 字段名       | 类型             | 说明               |
| ------------ | ---------------- | ------------------ |
| id           | INT (主键, 自增) | 歌手唯一标识       |
| name         | VARCHAR(45)      | 歌手姓名           |
| sex          | TINYINT          | 性别（0=女，1=男） |
| pic          | VARCHAR(255)     | 歌手图片路径       |
| birth        | DATETIME         | 出生日期           |
| location     | VARCHAR(45)      | 地区               |
| introduction | VARCHAR(255)     | 歌手简介           |



------

### 9. 歌曲表（`song`）

| 字段名       | 类型             | 说明         |
| ------------ | ---------------- | ------------ |
| id           | INT (主键, 自增) | 歌曲唯一标识 |
| singer_id    | INT              | 歌手 ID      |
| name         | VARCHAR(45)      | 歌曲名称     |
| introduction | VARCHAR(255)     | 歌曲简介     |
| create_time  | DATETIME         | 发行时间     |
| update_time  | DATETIME         | 更新时间     |
| pic          | VARCHAR(255)     | 歌曲封面路径 |
| lyric        | TEXT             | 歌词全文     |
| url          | VARCHAR(255)     | 歌曲文件路径 |



------

### 10. 歌单表（`song_list`）

| 字段名       | 类型             | 说明                  |
| ------------ | ---------------- | --------------------- |
| id           | INT (主键, 自增) | 歌单唯一标识          |
| title        | VARCHAR(255)     | 歌单标题              |
| pic          | VARCHAR(255)     | 歌单封面路径          |
| introduction | TEXT             | 歌单描述              |
| style        | VARCHAR(10)      | 风格标签（默认 "无"） |



------

### 11. 评论点赞表（`user_support`）

| 字段名     | 类型             | 说明         |
| ---------- | ---------------- | ------------ |
| id         | INT (主键, 自增) | 点赞记录标识 |
| comment_id | INT              | 评论 ID      |
| user_id    | VARCHAR(45)      | 用户 ID      |


## 运行配置

### 1.Redis运行命令
```shell
redis-server.exe
```

### 2. minio运行命令
```shell
E:
cd E:\ProgramFiles\minio\bin
set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin
.\minio.exe server E:\ProgramFiles\minio\data --address ":9005" --console-address ":9000"
```

## 任务分配
### 7月12日

> 主要工作：完成图片音频素材的查找完善，以及相关界面的美化

**音频素材**

+ 歌曲图片素材（杨周）
+ 歌单图片素材（杨周）
+ 歌曲mp3文件（杨可）
+ 歌词lrc文件（杨可）
+ 素材上传至minio
  + 图片上传
  + 音频上传

**界面优化**

+ 客户端
  1. 登录、注册、忘记密码、邮箱登录（石）
  2. 首页、歌单、歌手（何）
  3. 个人主页、设置（杨梓涵）
+ 管理端
  1. 登录界面（石）
  2. 图表首页（吴）
  3. 用户、歌手、歌单管理（吴）
  4. 轮播图管理

> 设计风格：简约沉浸式、卡片布局
