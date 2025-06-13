<script setup>
import { verifyCaptchaApi } from '@/apis/captcha';
import 'element-plus/theme-chalk/el-message.css'
import { ElMessage } from 'element-plus';
import { ref, onMounted, onBeforeUnmount } from 'vue';

const emit = defineEmits(['handle-captcha-verify']);


// 阿里云滑块验证配置
const captcha = ref(null);
// 加载时初始化阿里云滑块
onMounted(() => {
    // 嵌入式，除region和prefix以外的参数
    window.initAliyunCaptcha({
        SceneId: 'u85r32wr', // 场景ID
        prefix: '1m4d6s',
        // 验证码模式。embed表示要集成的验证码模式为嵌入式。无需修改
        mode: 'embed',
        // 页面上预留的渲染验证码的元素，与原代码中预留的页面元素保持一致。
        element: '#captcha-element',
        // 业务请求(带验证码校验)回调函数
        captchaVerifyCallback: captchaVerifyCallback,
        // 业务请求结果回调函数
        onBizResultCallback: onBizResultCallback,
        // 绑定验证码实例函数，无需修改
        getInstance: getInstance,
        // 滑块验证码样式，支持自定义宽度和高度，单位为px。其中，width最小值为320 px
        slideStyle: {
            width: 360,
            height: 40,
        },
        // 验证码语言类型，支持简体中文（cn）、繁体中文（tw）、英文（en）
        language: 'cn',
        // 完成验证后，是否立即发送验证请求（调用captchaVerifyCallback函数）
        immediate: true,
        autoRefresh: false, //验证完成后是否自动刷新验证码 
    });
})
onBeforeUnmount(() => {
    // 必须删除相关元素，否则再次mount多次调用 initAliyunCaptcha 会导致多次回调 captchaVerifyCallback
    document.getElementById('aliyunCaptcha-mask')?.remove();
    document.getElementById('aliyunCaptcha-window-embed')?.remove();
})
// 绑定验证码实例函数。该函数为固定写法，无需修改
function getInstance(instance) {
    captcha.value = instance;
}
// 登录验证码验证回调函数
const captchaVerifyCallback = async (captchaVerifyParam) => {
    try{
        const res = await verifyCaptchaApi({captchaVerifyParam:captchaVerifyParam});
        console.log(res)
        console.log(res.code == 1)
        if (res.code == 1){
            emit('handle-captcha-verify',true);
            return {captchaResult:true};
        }
        return {captchaResult:false};
    }catch(e){
        emit('handle-captcha-verify',false);
        return {captchaResult:false};
    }
    
}
// 业务请求验证结果回调函数
const onBizResultCallback = (bizResult) => {
}

// 添加刷新验证码的方法
const refreshCaptcha = () => {
    if (captcha.value) {
        captcha.value.refresh();
    }
};

// 暴露刷新方法给父组件
defineExpose({
    refreshCaptcha
});

</script>

<template>
    <div id="captcha-element"></div>
</template>

<style scoped>
</style>