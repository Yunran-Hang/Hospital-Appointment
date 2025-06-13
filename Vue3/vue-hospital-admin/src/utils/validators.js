
/**
 * 验证身份证号码格式及校验码
 * @param {string} idCard 身份证号
 * @returns {boolean} 是否有效
 */
export function validateIdCard(idCard) {
    const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dxX]$/;
  
    if (!idCardRegex.test(idCard)) {
      return false;
    }
  
    // 加权因子
    const weightFactor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
    // 校验码对应值
    const checkCode = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
  
    let sum = 0;
    for (let i = 0; i < 17; i++) {
      sum += parseInt(idCard[i], 10) * weightFactor[i];
    }
  
    const lastChar = idCard[17].toUpperCase();
    const expectedCheckCode = checkCode[sum % 11];
  
    return lastChar === expectedCheckCode;
  }