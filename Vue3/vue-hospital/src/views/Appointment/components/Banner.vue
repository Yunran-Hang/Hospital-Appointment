<script setup>
import { Search, Clock } from '@element-plus/icons-vue';
</script>

<template>
  <div class="banner">
    <!-- 主背景容器 -->
    <div class="banner-bg">
      <!-- 几何装饰 -->
      <div class="geometric-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <!-- 内容区域 -->
    <div class="banner-container">
      <!-- 左侧内容 -->
      <div class="content-left">
        <div class="hospital-badge">
          <el-icon class="hospital-icon"><School /></el-icon>
          <span>宁波市第二医院</span>
        </div>
        
        <h1 class="main-title">在线门诊预约挂号</h1>
        <p class="description">专业医疗服务，便捷预约体验</p>
        
        <!-- 服务特点 -->
        <div class="service-points">
          <div class="point">
            <el-icon><Clock /></el-icon>
            <span>24小时在线服务</span>
          </div>
          <div class="point">
            <el-icon><Grid /></el-icon>
            <span>专业医疗保障</span>
          </div>
        </div>
        
        <!-- 搜索按钮 -->
        <div class="search-section">
          <router-link to="/search" class="search-link">
            <el-button type="primary" size="large" class="search-btn">
              <el-icon><Search /></el-icon>
              <span>搜索医生</span>
            </el-button>
          </router-link>
          <p class="search-hint">快速找到适合的医生和科室</p>
        </div>
      </div>
      
      <!-- 右侧装饰 -->
      <div class="content-right">
        <div class="illustration">
          <div class="medical-card">
            <div class="card-header">
              <div class="card-icon">
                <el-icon><Hospital /></el-icon>
              </div>
              <div class="card-title">预约挂号</div>
            </div>
            <div class="card-content">
              <div class="info-line"></div>
              <div class="info-line short"></div>
              <div class="info-line"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use 'sass:color';
@use '@/styles/var.scss' as *;

.banner {
  position: relative;
  background: linear-gradient(120deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 32px;
  min-height: 320px;
  
  .banner-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    
    .geometric-shapes {
      position: relative;
      width: 100%;
      height: 100%;
      
      .shape {
        position: absolute;
        border-radius: 50%;
        opacity: 0.1;
        
        &.shape-1 {
          width: 200px;
          height: 200px;
          background: linear-gradient(135deg, $primary-color, $success-color);
          top: -100px;
          right: -100px;
          animation: float-slow 8s ease-in-out infinite;
        }
        
        &.shape-2 {
          width: 120px;
          height: 120px;
          background: linear-gradient(135deg, $success-color, $primary-light);
          bottom: -60px;
          left: 10%;
          animation: float-slow 6s ease-in-out infinite reverse;
        }
        
        &.shape-3 {
          width: 80px;
          height: 80px;
          background: linear-gradient(135deg, $primary-light, $info-color);
          top: 30%;
          right: 20%;
          animation: float-slow 10s ease-in-out infinite;
        }
      }
    }
  }
  
  .banner-container {
    position: relative;
    z-index: 2;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    padding: 60px 40px;
    align-items: center;
    
    .content-left {
      .hospital-badge {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: rgba($primary-color, 0.1);
        color: $primary-color;
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 600;
        margin-bottom: 24px;
        border: 1px solid rgba($primary-color, 0.2);
        
        .hospital-icon {
          font-size: 16px;
        }
      }
      
      .main-title {
        font-size: 42px;
        font-weight: 800;
        color: $text-primary;
        margin: 0 0 16px 0;
        line-height: 1.2;
        background: linear-gradient(135deg, $text-primary 0%, $primary-color 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .description {
        font-size: 18px;
        color: $text-secondary;
        margin: 0 0 32px 0;
        line-height: 1.6;
        font-weight: 500;
      }
      
      .service-points {
        display: flex;
        gap: 24px;
        margin-bottom: 40px;
        
        .point {
          display: flex;
          align-items: center;
          gap: 8px;
          color: $text-secondary;
          font-size: 14px;
          font-weight: 500;
          
          .el-icon {
            color: $success-color;
            font-size: 16px;
            background: rgba($success-color, 0.1);
            padding: 4px;
            border-radius: 6px;
          }
        }
      }
      
      .search-section {
        .search-link {
          text-decoration: none;
          display: inline-block;
          
          .search-btn {
            padding: 16px 32px !important;
            font-size: 16px !important;
            font-weight: 600 !important;
            border-radius: 16px !important;
            background: linear-gradient(135deg, $primary-color 0%, color.scale($primary-color, $lightness: 21.34%) 100%) !important;
            border: none !important;
            box-shadow: 0 8px 24px rgba($primary-color, 0.3) !important;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
            color: white !important;
            
            .el-icon {
              margin-right: 8px;
              font-size: 18px;
            }
            
            &:hover {
              transform: translateY(-2px) !important;
              box-shadow: 0 12px 32px rgba($primary-color, 0.4) !important;
              background: linear-gradient(135deg, color.scale($primary-color, $lightness: 10.67%) 0%, $primary-color 100%) !important;
            }
            
            &:active {
              transform: translateY(0) !important;
            }
          }
        }
        
        .search-hint {
          margin: 12px 0 0 0;
          font-size: 14px;
          color: $text-secondary;
          opacity: 0.8;
        }
      }
    }
    
    .content-right {
      display: flex;
      justify-content: center;
      align-items: center;
      
      .illustration {
        .medical-card {
          background: white;
          border-radius: 20px;
          padding: 24px;
          box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
          border: 1px solid rgba($primary-color, 0.1);
          width: 240px;
          transform: rotate(5deg);
          transition: transform 0.3s ease;
          
          &:hover {
            transform: rotate(0deg) scale(1.05);
          }
          
          .card-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 20px;
            
            .card-icon {
              width: 40px;
              height: 40px;
              background: linear-gradient(135deg, $primary-color, $primary-light);
              border-radius: 10px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: white;
              
              .el-icon {
                font-size: 20px;
              }
            }
            
            .card-title {
              font-size: 16px;
              font-weight: 600;
              color: $text-primary;
            }
          }
          
          .card-content {
            .info-line {
              height: 8px;
              background: linear-gradient(90deg, rgba($primary-color, 0.3), rgba($primary-color, 0.1));
              border-radius: 4px;
              margin-bottom: 8px;
              
              &.short {
                width: 60%;
              }
              
              &:last-child {
                margin-bottom: 0;
              }
            }
          }
        }
      }
    }
  }
}

// 动画
@keyframes float-slow {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .banner {
    .banner-container {
      gap: 40px;
      padding: 50px 30px;
      
      .content-left {
        .main-title {
          font-size: 36px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .banner {
    min-height: auto;
    
    .banner-container {
      grid-template-columns: 1fr;
      gap: 40px;
      padding: 40px 24px;
      text-align: center;
      
      .content-left {
        .main-title {
          font-size: 32px;
        }
        
        .description {
          font-size: 16px;
        }
        
        .service-points {
          justify-content: center;
          flex-wrap: wrap;
          gap: 16px;
        }
      }
      
      .content-right {
        order: -1;
        
        .illustration {
          .medical-card {
            transform: rotate(0deg);
            width: 200px;
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .banner {
    border-radius: 16px;
    
    .banner-container {
      padding: 32px 20px;
      
      .content-left {
        .hospital-badge {
          font-size: 12px;
          padding: 6px 12px;
        }
        
        .main-title {
          font-size: 28px;
        }
        
        .description {
          font-size: 14px;
        }
        
        .service-points {
          .point {
            font-size: 12px;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }
        
        .search-section {
          .search-btn {
            padding: 14px 28px !important;
            font-size: 14px !important;
          }
          
          .search-hint {
            font-size: 12px;
          }
        }
      }
      
      .content-right {
        .illustration {
          .medical-card {
            width: 180px;
            padding: 20px;
          }
        }
      }
    }
  }
}
</style>


  
  
  
  