import request from '@/utils/request';

export const getServerPublicKeyApi = () => {
    return request({
        url: '/api/crypto/publicKey',
        method: 'get'
    })
}