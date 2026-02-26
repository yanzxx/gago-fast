# 快速开发框架

## 更新日志

### 2025-05-16

---

#### Features

- 密码过期提醒

### 2025-05-12

---

#### Features

- 系统配置、个人中心做密码复杂度校验
- 系统已有职位名称、职位分类改为模糊值

#### Bug Fixes

- 系统配置返回明文密码
- 系统配置值允许为空
- 修复多数据源密码没有解密的问题
- 修复多数据源表读取SQL错误

#### Build

- 更新SQL初始化脚本

### 2025-05-06

---

#### Bug Fixes

- 允许同一账号并发登录
- 去除cookie
- dev_file表size_kb字段修正为varchar类型

## 框架介绍

快速开发平台，集成国密加解密插件，
软件层面完全符合等保测评要求，同时实现国产化机型、中间件、数据库适配，是您的不二之选！
技术框架与密码结合，让更多的人认识密码，使用密码；更是让前后分离“密”不可分。

### 后端支撑

| 插件 | 版本 | 用途 |
| --- | ----- |  ----- |
| jdk | 11 / 1.8 |java环境 |
| lombok | idea内 |代码简化插件 |
| maven | 最新版 |包管理工具 |
| redis | 最新版 | 缓存库 |
| mysql | 8.0 / 5.7 | 数据库 |

### 启动后端

开发工具内配置好maven并在代码中配置数据库即可启动

## 代码结构

框架对代码以插件化的模式进行分包，使得包层级结构更加清晰合理，同时降低了耦合度

```plain
snowy
  |-snowy-common == 基础通用模块
  |-snowy-plugin == 插件包
    |-snowy-plugin-auth == 登录鉴权插件
    |-snowy-plugin-biz == 业务功能插件
    |-snowy-plugin-client == C端功能插件
    |-snowy-plugin-dev == 开发工具插件
    |-snowy-plugin-gen == 代码生成插件
    |-snowy-plugin-mobile == 移动端管理插件
    |-snowy-plugin-sys == 系统功能插件
  |-snowy-plugin-api == 插件api包
    |-snowy-plugin-auth-api == 登录鉴权插件api接口
    |-snowy-plugin-biz-api == 业务功能插件api接口
    |-snowy-plugin-client-api == C端功能插件api接口
    |-snowy-plugin-dev-api == 开发工具插件api接口
    |-snowy-plugin-gen == 代码生成插件api接口
    |-snowy-plugin-mobile == 移动端管理插件api接口
    |-snowy-plugin-sys-api == 系统功能插件api接口
  |-snowy-web-app == 主启动模块
```

## 加密

| 功能                        | 算法类型          |
| ----------------------      | ------------- |
| 登录        | SM2前端加密，后端解密 |
| 登录登出日志        | SM2对登录登出日志做签名完整性保护存储    |
| 操作日志        | SM2对操作日志做签名完整性保护存储    |
| 用户密码        | SM3完整性保护存储，登录时做完整性校验    |
| 用户手机号        | SM4（cbc模式）加解密使用字段脱敏    |
