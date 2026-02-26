# 快速开发框架

## 更新日志

### 2025-05-16

---

#### Features

- 密码过期提醒

#### Bug Fixes

- 去除登录页自动填写账号密码

### 2025-05-12

---

#### Features

- 个人中心账号管理只保留密码修改功能
- 个人中心、系统配置增加密码复杂度校验

#### Bug Fixes

- 系统配置允许提交空值

### 2025-05-06

---

#### Build

- 在构建镜像时进行npm打包操作，无需再本地打包后提交

## 安装依赖

npm i

## 启动项目(开发模式)

npm run dev

启动完成后浏览器访问 <http://localhost:81>

```plain
现在的UI风格是原版的风格，如果想用苏农云版的风格，请按下面的规则将文件改名
1、src/layout/index.vue => src/layout/index_init.vue
 src/layout/index_new.vue => src/layout/index.vue

2、src/layout/components/userbar.vue => src/layout/components/userbar_init.vue
 src/layout/components/userbar_new.vue => src/layout/components/userbar.vue

3、src/layout/components/moduleMenu.vue => src/layout/components/moduleMenu_init.vue
 src/layout/components/moduleMenu_new.vue => src/layout/components/moduleMenu.vue
 
4、src/style/index.less => src/style/index_init.less
 src/style/index_new.less => src/style/index.less

```

相关功能使用说明：

 一、切换登录页面背景图：
  /public/img/login_background.png 这个是登录页面背景图片，如果需要换掉，替换相同名称的图片即可
 二、单页具有很多功能：
  1、可以设置是否显示在顶部导航栏，显示在顶部导航栏的位置：由新建单页时填写的排序字段决定，单页的排序会
  和模块的排序相比较。如果有相同排序数字，则单页优先排在模块前面
  2、可以设置是否全屏展示，如果选择全屏展示，当打开该单页时，默认是全屏的效果（和登录页面效果一致，没有顶部导航栏和左侧菜单栏）
  3、可以设置是否隐藏左侧菜单栏，如果选择隐藏，当打开该单页时，左侧的菜单栏会隐藏

## 代码结构

snowy
  |-snowy-admin-web == 前端
    |-public == 基础静态文件
    |-src == 前端源代码
      |-api == API接口转发
      |-assets == 静态文件
      |-components == VUE组件
      |-config == 基础配置
      |-layout == 基础布局
      |-locales == 多语言配置
      |-router == 基础路由配置
      |-store == Pinia缓存配置
      |-style == 样式风格配置
      |-utils == 工具类
      |-views == 所有视图界面