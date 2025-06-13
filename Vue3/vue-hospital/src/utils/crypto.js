import CryptoJS from 'crypto-js'
import JSEncrypt from 'jsencrypt'
import { useCryptoStore } from '@/stores/cryptoStore'

/**
 * 生成随机AES密钥
 * @returns {string} 16位随机密钥
 */
export function generateAESKey() {
  // 生成16位随机密钥
  const key = CryptoJS.lib.WordArray.random(16).toString()
  return key
}

/**
 * AES对称加密
 * @param {string} data - 要加密的数据
 * @param {string} key - AES密钥
 * @returns {string} 加密后的数据
 */
export function aesEncrypt(data, key) {
  const keyWordArray = CryptoJS.enc.Utf8.parse(key)
  const encrypted = CryptoJS.AES.encrypt(JSON.stringify(data), keyWordArray, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.toString()
}

/**
 * RSA加密AES密钥
 * @param {string} aesKey - AES密钥
 * @returns {string} RSA加密后的AES密钥
 */
export function rsaEncryptAesKey(aesKey,publicKey) {
  const encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKey)
  return encrypt.encrypt(aesKey)
}

/**
 * 混合加密函数
 * @param {any} data - 要加密的数据
 * @returns {object} 包含加密数据和加密密钥的对象
 */
export const hybridEncrypt = async (data) => {
  // 1. 生成随机AES密钥
  const aesKey = generateAESKey()
  
  // 2. 用AES加密数据
  const encryptedData = aesEncrypt(data, aesKey)
  
  // 3. 用RSA加密AES密钥
  const cryptoStore = useCryptoStore()
  const serverPublicKey = await cryptoStore.getServerPublicKey()
  if (serverPublicKey == null || serverPublicKey === ''){
    serverPublicKey = await cryptoStore.refreshServerPublicKey()
  }
  
  const encryptedKey = rsaEncryptAesKey(aesKey,serverPublicKey)

  // 4. 生成客户端密钥对
  const { publicKey, privateKey } = cryptoStore.getClientKeyPair()
  // 5. 对加密数据和当前时间戳进行签名
  const timestamp = Date.now()
  const signature = signWithClientKey(encryptedData + timestamp,privateKey)

  // 6. 返回
  return {
      encryptedData: encryptedData,
      aesKey: encryptedKey,
      timestamp: timestamp,
      signature: signature,
      clientPublicKey: publicKey
  }
}

/**
 * 判断是否需要加密
 * @param {string} method - HTTP方法
 * @param {string} url - 请求URL
 * @returns {boolean} 是否需要加密
 */
export function shouldEncrypt(method, url) {
  // GET请求不加密
  if (method.toUpperCase() === 'GET') {
    return false
  }
  
  // 可以根据URL路径添加更多不需要加密的接口
  const noEncryptPaths = [
    '/api/common/avatar/upload',
    '/user/user/captcha/verify'
  ]
  
  return !noEncryptPaths.some(path => url.includes(path))
}

/**
 * 生成客户端密钥对
 */
export const generateKeyPair = () => {
  const encrypt = new JSEncrypt({ default_key_size: 2048 })
  return {
    publicKey: encrypt.getPublicKey(),
    privateKey: encrypt.getPrivateKey()
  }
}

/**
   * 使用客户端私钥签名（SHA256）
   */
const signWithClientKey = (data,privateKey) => {
  // 先进行SHA256哈希
  const hash = CryptoJS.SHA256(data).toString()
  
  const encrypt = new JSEncrypt()
  encrypt.setPrivateKey(privateKey)
  
  // 对哈希值进行RSA签名
  return encrypt.sign(hash, CryptoJS.SHA256, 'sha256')
}