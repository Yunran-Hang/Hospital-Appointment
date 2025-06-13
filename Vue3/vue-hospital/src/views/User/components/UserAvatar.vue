<script setup>
import {ref} from 'vue'
import avatar from '@/assets/images/avatar.jpg'
import 'element-plus/theme-chalk/el-message.css'
import { userUpdateInfoApi } from '@/apis/user'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const uploadRef = ref(null)

//用户头像地址
const imgUrl= ref(userStore.userInfo.avatar)

//图片上传成功的回调函数
const uploadSuccess = (result)=>{imgUrl.value = result.data;}
// 图片上传失败的回调函数
const uploadError = (result)=>{ ElMessage.error('上传失败');}

//头像修改
const updateAvatar = async ()=>{
    try {
        //调用接口
        const data = {
            avatar: imgUrl.value
        }
        const result = await userUpdateInfoApi(data);
        if (result.code == 1){
            ElMessage.success('操作成功')
            //修改pinia中的数据
            userStore.userInfo.avatar = imgUrl.value
        }
    }catch (error) {
        ElMessage.error('操作失败')
    }
}
//图片上传前的回调函数
const beforeUpload = (file)=>{
    const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png';
    //限制文件大小
    const isLt2M = file.size / 1024 / 1024 < 2;

    if (!isJPGorPNG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        ElMessage.error('上传图片大小不能超过 2MB!');
      }
      return isJPGorPNG && isLt2M;
}
</script>

<template>
    <el-card class="page-container" shadow="hover">
        <template #header>
            <div class="header">
                <span class="title">更换头像</span>
                <small class="subtitle">支持jpg、png格式的图片</small>
            </div>
        </template>
        <el-row justify="center">
            <el-col :span="16">
                <div class="upload-container">
                    <el-upload 
                        ref="uploadRef"
                        class="avatar-uploader" 
                        :show-file-list="false"
                        :auto-upload="true"
                        action="/api/common/avatar/upload"
                        name="file"
                        :headers="{'Authorization':userStore.userInfo.token}"
                        :on-success="uploadSuccess"
                        :on-error="uploadError"
                        accept="image/*"
                        :before-upload="beforeUpload"
                        >
                        <div class="avatar-wrapper">
                            <img v-if="imgUrl" :src="imgUrl" class="avatar" />
                            <img v-else :src="avatar" class="avatar" />
                            <div class="hover-mask">
                                <el-icon class="upload-icon"><Plus /></el-icon>
                                <span>点击更换头像</span>
                            </div>
                        </div>
                    </el-upload>
                    
                    <div class="button-group">
                        <el-button 
                            type="primary" 
                            :icon="Plus" 
                            size="large"  
                            @click="uploadRef.$el.querySelector('input').click()"
                            class="action-button"
                        >
                            选择图片
                        </el-button>
                        <el-button type="success" :icon="Upload" size="large" @click="updateAvatar"class="action-button">
                            上传头像
                        </el-button>
                    </div>
                </div>
            </el-col>
        </el-row>
    </el-card>
</template>

<style lang="scss" scoped>
.page-container {
    max-width: 800px;
    margin: 0 auto;
    transition: all 0.3s ease;

    .header {
        display: flex;
        align-items: center;
        gap: 12px;

        .title {
            font-size: 18px;
            font-weight: 500;
            color: #333;
        }

        .subtitle {
            color: #999;
            font-size: 14px;
        }
    }
}

.upload-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
}

.avatar-uploader {
    width: 300px;
    margin-bottom: 24px;

    .avatar-wrapper {
        position: relative;
        width: 300px;
        height: 300px;
        border-radius: 8px;
        overflow: hidden;
        border: 2px dashed #e4e7ed;
        transition: all 0.3s ease;

        &:hover {
            border-color: #3498db;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

            .hover-mask {
                opacity: 1;
            }
        }

        .avatar {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: all 0.3s ease;
        }

        .hover-mask {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            opacity: 0;
            transition: all 0.3s ease;
            color: white;

            .upload-icon {
                font-size: 32px;
                margin-bottom: 8px;
            }

            span {
                font-size: 16px;
            }
        }
    }
}

.button-group {
    display: flex;
    gap: 16px;
    margin-top: 20px;

    .action-button {
        min-width: 120px;
        transition: all 0.3s ease;

        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
    }
}
</style>