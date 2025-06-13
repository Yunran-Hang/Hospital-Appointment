import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getServerPublicKeyApi } from '@/apis/cryptoApi'
import { generateKeyPair } from '@/utils/crypto'

export const useCryptoStore = defineStore('crypto', () => {
    // 服务器公钥缓存
    const serverPublicKey = ref('')
    const keyTimestamp = ref(0)
    const keyValidDuration = 60 * 60 * 1000 // 60分钟

    // 客户端密钥对缓存(进内存，不持久化)
    const clientKeyPair = ref({ publicKey: '', privateKey: '' })
    const clientKeyTimestamp = ref(0)
    const clientKeyValidDuration = 60 * 60 * 1000 // 60分钟
    
    /**
     * 检查服务器公钥是否有效
     */
    const isServerKeyValid = () => {
        if (!serverPublicKey.value) return false
        const now = Date.now()
        return (now - keyTimestamp.value) < keyValidDuration
    }
    /**
     * 检查客户端密钥对是否有效
     */
    const isClientKeyValid = () => {
        if (!clientKeyPair.value.publicKey || !clientKeyPair.value.privateKey) return false
        const now = Date.now()
        return (now - clientKeyTimestamp.value) < clientKeyValidDuration
    }
    /**
     * 获取服务器公钥（带缓存）
     */
    const getServerPublicKey = async () => {
        // 1.如果缓存的公钥仍然有效，直接返回
        if (isServerKeyValid()) {
            return serverPublicKey.value
        }

        try {
            // 2.从服务器获取新的公钥
            const res = await getServerPublicKeyApi()
            const base64PublicKey = res.data
            
            // 用正则表达式将base64字符串按64字符分行（符合PEM标准）
            const formattedKey = base64PublicKey.match(/.{1,64}/g).join('\n')
            // 将base64编码的公钥转换为PEM格式
            const pemPublicKey = `-----BEGIN PUBLIC KEY-----\n${formattedKey}\n-----END PUBLIC KEY-----`
            // 更新缓存
            serverPublicKey.value = pemPublicKey
            keyTimestamp.value = Date.now()
            return pemPublicKey
        } catch (error) {
            console.error('获取服务器公钥失败:', error)
            // 如果获取失败且有缓存的公钥，返回缓存的公钥
            if (serverPublicKey.value) {
                console.warn('使用缓存的公钥')
                return serverPublicKey.value
            }
        }
    }
    /**
     * 获取客户端密钥对（仅内存缓存）
     */
    const getClientKeyPair = () => {
        if (isClientKeyValid()) {
            return clientKeyPair.value
        }
        // 生成新的密钥对
        const newKeyPair = generateKeyPair()
        
        clientKeyPair.value = newKeyPair
        clientKeyTimestamp.value = Date.now()
        
        return newKeyPair
    }
    /**
     * 清除客户端密钥（安全清理）
     */
    const clearClientKeys = () => {
        // 安全清理：覆盖内存中的私钥
        if (clientKeyPair.value.privateKey) {
            clientKeyPair.value.privateKey = '0'.repeat(clientKeyPair.value.privateKey.length)
        }
        clientKeyPair.value = { publicKey: '', privateKey: '' }
        clientKeyTimestamp.value = 0
    }
    
    /**
     * 清除所有缓存
     */
    const clearAllCache = () => {
        serverPublicKey.value = ''
        keyTimestamp.value = 0
        clearClientKeys()
    }

    /**
     * 手动刷新服务器公钥
     */
    const refreshServerPublicKey = async () => {
        clearKeyCache()
        return await getServerPublicKey()
    }

    // 页面卸载时清理私钥
    if (typeof window !== 'undefined') {
        window.addEventListener('beforeunload', clearClientKeys)
        window.addEventListener('unload', clearClientKeys)
    }

    return {
        serverPublicKey,
        isServerKeyValid,
        isClientKeyValid,
        getServerPublicKey,
        getClientKeyPair,
        clearClientKeys,
        clearAllCache,
        refreshServerPublicKey
    }
}, {
  persist: {
    key: 'crypto-store',
    storage: sessionStorage, // 使用sessionStorage，关闭浏览器后清除
    // 只持久化服务器公钥，不持久化客户端密钥对
    paths: ['serverPublicKey', 'keyTimestamp']
  }
})