
/**
 * 将PEM格式的公钥转换为base64字符串
 * @param {string} pemPublicKey - PEM格式的公钥
 * @returns {string} base64编码的公钥
 */
export function pemToBase64(pemPublicKey) {
    // 移除PEM头尾和换行符，提取base64内容
    return pemPublicKey
      .replace(/-----BEGIN PUBLIC KEY-----/g, '')
      .replace(/-----END PUBLIC KEY-----/g, '')
      .replace(/\s/g, '') // 移除所有空白字符（包括换行符）
  }