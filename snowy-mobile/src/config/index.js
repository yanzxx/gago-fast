const noTokenBackUrl = "login";
const hasTokenBackUrl = "home";
const apiBaseUrl =
    import.meta.env.VITE_API_BASEURL ||
    `${window.location.protocol}//${window.location.host}`;

const bottomNavigationBar = [{
    name: '首页',
    path: '/home',
}, {
    name: '生产管理',
    path: '/production',
}, {
    name: '我的',
    path: '/mine',
}]

// 应用全局配置
export default {
    // 服务平台类型（SNOWY或SNOWY_CLOUD）
    SERVER_TYPE: 'SNOWY',

    // 请求超时
    TIMEOUT: 10000,

    // TokenName // Authorization
    TOKEN_NAME: 'token',

    // 接口地址
    API_URL: apiBaseUrl,

    DOMAIN: import.meta.env.VITE_DOMAIN || import.meta.env.VITE_DOMAN || '',

    storageType: 'localStorage',

    // Token前缀，注意最后有个空格，如不需要需设置空字符串 // Bearer
    TOKEN_PREFIX: '',

    STORAGE_PREFIX: '',

    // 系统基础配置，这些是数据库中保存起来的
    SYS_BASE_CONFIG: {
        // 默认logo
        SNOWY_SYS_LOGO: '/static/logo.png',
        // 背景图
        SNOWY_SYS_BACK_IMAGE: '',
        // 系统名称
        SNOWY_SYS_NAME: 'Gago',
        // 版本
        SNOWY_SYS_VERSION: '2.0',
        // 版权
        SNOWY_SYS_COPYRIGHT: 'Gago',
        // 版权跳转URL
        SNOWY_SYS_COPYRIGHT_URL: '',
        // 默认文件存储
        SNOWY_SYS_DEFAULT_FILE_ENGINE: 'LOCAL',
        // 是否开启验证码
        SNOWY_SYS_DEFAULT_CAPTCHA_OPEN: 'false',
        // 默认重置密码
        SNOWY_SYS_DEFAULT_PASSWORD: '123456'
    },

    // 首页配置
    HOME_CONFIGS: [
        // 首页看板
        {
            name: "首页看板",
            code: "chart",
            isShow: true,
        },
    ],

    // 没有token访问退回页面
    NO_TOKEN_BACK_URL: noTokenBackUrl,
    // 不需要登录（没有token）页面白名单
    NO_TOKEN_WHITE_LIST: [
        noTokenBackUrl,
    ],
    // 有token访问退回页面
    HAS_TOKEN_BACK_URL: hasTokenBackUrl,
    // 登录（有token）可以访问的页面白名单
    HAS_TOKEN_WHITE_LIST: [
        hasTokenBackUrl,
    ],
    // 顶部导航栏数据
    BOTTOMNAVIGATIONBAR: bottomNavigationBar,
    // 显示顶部导航栏数据的路由
    SHOWNAVIGATIONBARROUTERLIST: ["/home", "/production", "/mine"],
    // 显示头部导航栏的路由
    SHOWTOPBARROUTERLIST: [
        "/message",
        "/messageManage",
        "/production",
        "/production/register",
        "/production/batch",
        "/production/records",
        "/production/detail",
        "/org",
        "/orgForm",
        "/position",
        "/positionForm",
        "/user",
        "/userForm",
        "/editMessage",
        "/mineInfo",
        "/mineEditInfo",
        "/mineHomeConfig",
        "/editPwd",
        "/myFarm",
        "/homeTodoList"
    ]
}
