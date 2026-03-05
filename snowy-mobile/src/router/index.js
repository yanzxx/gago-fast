import { createRouter, createWebHashHistory } from 'vue-router';
import {showFailToast} from "vant";
import { getToken } from '@/utils/auth'
import config from '@/config';
const routes = [
    {
        path: '/',
        component: () => import('@/views/index.vue'),
        redirect: to => {
            return {
                path: '/home',
            }
        },
        children: [
            {
                path: '/home',
                name: 'home',
                meta: {
                    title: '首页'
                },
                component: () => import('@/views/home/index.vue'),
            },
            {
                path: '/work',
                name: 'work',
                meta: {
                    title: '工作台'
                },
                component: () => import('@/views/work/index.vue'),
            },
            {
                path: '/message',
                name: 'message',
                meta: {
                    title: '消息'
                },
                component: () => import('@/views/msg/index.vue'),
            },
            {
                path: '/mine',
                name: 'mine',
                meta: {
                    title: '我的'
                },
                component: () => import('@/views/mine/index.vue'),
            },
            {
                path: '/org',
                name: 'org',
                meta: {
                    title: '机构列表'
                },
                component: () => import('@/views/biz/org/index.vue'),
            },
            {
                path: '/orgForm',
                name: 'orgForm',
                meta: {
                    title: '新增/编辑机构'
                },
                component: () => import('@/views/biz/org/form.vue'),
            },
            {
                path: '/position',
                name: 'position',
                meta: {
                    title: '岗位列表'
                },
                component: () => import('@/views/biz/position/index.vue'),
            },
            {
                path: '/positionForm',
                name: 'positionForm',
                meta: {
                    title: '新增/编辑岗位'
                },
                component: () => import('@/views/biz/position/form.vue'),
            },
            {
                path: '/user',
                name: 'user',
                meta: {
                    title: '人员列表'
                },
                component: () => import('@/views/biz/user/index.vue'),
            },
            {
                path: '/userForm',
                name: 'userForm',
                meta: {
                    title: '新增/编辑人员'
                },
                component: () => import('@/views/biz/user/form.vue'),
            },
            {
                path: '/editMessage',
                name: 'editMessage',
                meta: {
                    title: '消息详情'
                },
                component: () => import('@/views/msg/detail.vue'),
            },
            {
                path: '/mineInfo',
                name: 'mineInfo',
                meta: {
                    title: '我的详情'
                },
                component: () => import('@/views/mine/info/index.vue'),
            },
            {
                path: '/mineEditInfo',
                name: 'mineEditInfo',
                meta: {
                    title: '编辑个人信息'
                },
                component: () => import('@/views/mine/info/edit.vue'),
            },
            {
                path: '/mineHomeConfig',
                name: 'mineHomeConfig',
                meta: {
                    title: '首页配置'
                },
                component: () => import('@/views/mine/home-config/index.vue'),
            },
            {
                path: '/editPwd',
                name: 'editPwd',
                meta: {
                    title: '更新密码'
                },
                component: () => import('@/views/mine/pwd/index.vue'),
            },
            {
                path: '/myFarm',
                name: 'myFarm',
                meta: {
                    title: '我的农场'
                },
                component: () => import('@/views/region/my-farm/index.vue'),
            },
            {
                path: '/editLand',
                component: () => import("../views/region/my-farm/editLand/index.vue"),
                name: "editLand",
                meta: { title: "编辑地块信息", icon: "" },
            },
            {
                path: '/landDetails',
                component: () => import("../views/region/my-farm/landDetails/index.vue"),
                name: "landDetails",
                meta: { title: "地块详情", icon: "" },
            },
            {
                path: '/addPointLand',
                component: () => import("../views/region/my-farm/addPointLand/index.vue"),
                name: "addPointLand",
                meta: { title: "圈画地块", icon: "" },
            },
            {
                path: '/searchPage',
                component: () => import("../views/region/my-farm/searchPage/index.vue"),
                name: "searchPage",
                meta: { title: "搜索地块", icon: "" },
            },
            {
                path: '/walkLand',
                component: () => import("../views/region/my-farm/walkLand/index.vue"),
                name: "walkLand",
                meta: { title: "跑马圈地", icon: "" },
            },
            {
                path: '/trackList',
                component: () => import("../views/region/my-farm/trackList/index.vue"),
                name: "trackList",
                meta: { title: "作业轨迹", icon: "" },
            },
            {
                path: '/homeTodoList',
                name: 'homeTodoList',
                meta: {
                    title: '待办详情'
                },
                component: () => import('@/views/home/todo-list.vue'),
            },
            {
                path: '/homeMetricList',
                name: 'homeMetricList',
                meta: {
                    title: '指标详情'
                },
                component: () => import('@/views/home/metric-list.vue'),
            },
        ]
    },
    {
        path: '/login',
        name: 'login',
        meta: {
            title: '登录'
        },
        component: () => import('@/views/login.vue'),
    }
];

const router = createRouter({
    //哈希值模式
    history: createWebHashHistory(),
    routes,
});

// 判断是否已加载过动态/静态路由
const isGetRouter = false

// 全局守卫：登录拦截 本地没有存token,请重新登录
router.beforeEach((to, from, next) => {
    // 判断有没有登录
    const {
        TOKEN_PREFIX
    } = config
    const appToken = TOKEN_PREFIX + getToken()
    if (!appToken) {
        if (to.name != "login") {
            if (config.NO_TOKEN_WHITE_LIST.includes(to.name)) {
                next()
            } else {
                showFailToast("页面【" + to.meta.title + "】需要进行登录，才能进行访问！");
                next({
                    path: '/login'
                })
            }
        } else {
            next()
        }
    } else {
        next()
    }
});

// 暴露出去
export default router;
