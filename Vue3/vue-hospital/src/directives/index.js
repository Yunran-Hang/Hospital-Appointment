// 定义懒加载插件
// 用于检测用户是否进入视口区域
import { useIntersectionObserver } from '@vueuse/core'

export const lazyPlugin = {
    install(app) {
        // 懒加载指令逻辑

        // 图片懒加载
        app.directive('img-lazy', {
            mounted(el,binding) {
                // el: 指令绑定的那个元素 img
                // binding: binding.value 指令等于号后面绑定的表达式的值 图片url
                // console.log(el,binding.value)
                const { stop } = useIntersectionObserver(
                    el, // 要监听的对象
                    ([{ isIntersecting}]) => {
                        if (isIntersecting){
                            // 进入视口区域，给src赋值
                            el.src = binding.value
                            // 用于执行一次之后就停止
                            stop()
                        }
                    }
                )
            }
        })

    }
}
