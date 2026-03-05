<template>
    <div class="login-container">
        <div class="logo-content" @tap="logoTap">
            <img
                style="width: 50px; height: 10px"
                :alt="sysBaseConfig.SNOWY_SYS_NAME"
                :src="sysBaseConfig.SNOWY_SYS_LOGO"
                mode="widthFix"
            />
            <label class="title"
                >{{ sysBaseConfig.SNOWY_SYS_NAME }}
                {{ sysBaseConfig.SNOWY_SYS_VERSION }}</label
            >
        </div>
        <div class="login-form-content">
            <div class="input-item">
                <van-icon name="user-o" size="20" color="#999" />
                <input
                    v-model="loginForm.account"
                    class="input"
                    type="text"
                    placeholder="请输入账号"
                    maxlength="30"
                />
            </div>
            <div class="input-item">
                <el-icon size="20" color="#999"><Lock /></el-icon>
                <input
                    v-model="loginForm.password"
                    type="password"
                    class="input"
                    placeholder="请输入密码"
                    maxlength="20"
                />
            </div>
            <div
                class="input-item"
                v-if="sysBaseConfig.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true'"
            >
                <van-icon name="other-pay" size="20" color="#999" />
                <input
                    v-model="loginForm.validCode"
                    type="text"
                    class="input"
                    placeholder="请输入验证码"
                    maxlength="4"
                />
                <img
                    :src="validCodeBase64"
                    @click="loginCaptcha"
                    class="login-code-img"
                />
            </div>
            <div>
                <van-button type="primary" @click="handleLogin"
                    >登录</van-button
                >
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { getPicCaptcha } from '@/api/login';
import store from '@/store';
import { showToast } from 'vant';
import router from '../router/index.js';
const logoTap = () => {
    router.push({
        path: '/home',
    });
};
const sysBaseConfig = computed(() => {
    return store.getters.sysBaseConfig;
});
const validCodeBase64 = ref('');
const loginForm = reactive({
    account: '',
    password: '',
    validCode: '',
    validCodeReqNo: '',
});
// 获取图形验证码
const loginCaptcha = () => {
    getPicCaptcha().then((res) => {
        validCodeBase64.value = res.data.validCodeBase64;
        loginForm.validCodeReqNo = res.data.validCodeReqNo;
    });
};
if (sysBaseConfig.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true') {
    loginCaptcha();
}
// 登录方法
const handleLogin = async () => {
    if (loginForm.account === '') {
        showToast({
            message: '请输入账号',
            icon: 'warning-o',
        });
    } else if (loginForm.password === '') {
        showToast({
            message: '请输入密码',
            icon: 'warning-o',
        });
    } else if (
        loginForm.validCode === '' &&
        sysBaseConfig.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true'
    ) {
        showToast({
            message: '请输入验证码',
            icon: 'warning-o',
        });
    } else {
        pwdLogin();
    }
};
// 密码登录
const pwdLogin = async () => {
    store
        .dispatch('Login', loginForm)
        .then(() => {
            // 所有异步请求结束之后才能够进行下一步操作
            Promise.all([
                store.dispatch('GetUserLoginMenu'),
                store.dispatch('GetUserInfo'),
                store.dispatch('GetDictTypeTreeData'),
            ])
                .then(() => {
                    router.push({
                        path: '/home',
                    });
                })
                .catch((error) => {
                    console.log('promise.all catch', error);
                    if (
                        sysBaseConfig.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN ===
                        'true'
                    ) {
                        loginCaptcha();
                        loginForm.validCode = '';
                    }
                });
        })
        .catch(() => {
            if (sysBaseConfig.value.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN === 'true') {
                loginCaptcha();
                loginForm.validCode = '';
            }
        });
};
</script>

<style lang="scss">
page {
    background-color: #ffffff;
}

.login-container {
    width: 100%;

    .logo-content {
        width: 100%;
        font-size: 21px;
        text-align: center;
        padding-top: 15%;
        align-items: center;
        justify-content: center;
        display: flex;

        image {
            border-radius: 4px;
            width: 100px;
            height: 100px;
        }

        .title {
            margin-left: 15px;
            color: #000;
            font-size: 21px;
            font-family:
                Helvetica Neue,
                Helvetica,
                system-ui;
        }
    }

    .login-form-content {
        text-align: center;
        margin: 20px auto;
        margin-top: 15%;
        width: 80%;

        .input-item {
            margin: 20px auto;
            background-color: #f5f6f7;
            height: 45px;
            border-radius: 20px;
            display: flex;
            align-items: center;
            padding-left: 14px;

            .icon {
                font-size: 38px;
                margin-left: 10px;
                color: #999;
            }

            .input {
                width: 100%;
                font-size: 14px;
                line-height: 20px;
                text-align: left;
                padding-left: 15px;
                border: 0;
                background: transparent;
                color: #000;
            }

            .login-code-img {
                border: 1px solid var(--border-color-split);
                cursor: pointer;
                width: 70%;
                height: 45px;
            }
        }

        .van-button {
            margin-top: 30px;
            height: 45px;
            border-radius: 1000px;
            color: #ffffff;
            width: 100%;
            font-size: 18px;
            background: #2979ff;
        }
    }
}

.login-code-img {
    border: 1px solid var(--border-color-split);
    cursor: pointer;
    width: 70%;
    height: 45px;
}
</style>
