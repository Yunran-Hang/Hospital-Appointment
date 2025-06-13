<script setup>
import { ArrowRight, Clock, Location, Phone, Tickets } from '@element-plus/icons-vue';
</script>

<template>
  <div class="home-container">
    <!-- 主横幅 -->
    <div class="main-banner">
      <div class="banner-content">
        <h1>宁波市第二医院</h1>
        <p class="slogan">专业医疗服务，守护您的健康</p>
        <div class="banner-buttons">
          <router-link to="/appointment">
            <el-button type="primary" size="large" class="action-button">
              <span>在线预约</span>
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </router-link>
          <router-link to="/search">
            <el-button size="large" class="action-button secondary">
              <span>查找医生</span>
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 医院简介 -->
    <div class="hospital-intro">
        <div class="section-title">
            <h2>医院简介</h2>
            <div class="title-line"></div>
        </div>
        <p class="intro-text">
            宁波市第二医院肇始于1843年，作为国内最早成立的西医医院之一，现已发展成为集医疗、教学、科研、预防为一体的现代化三级甲等综合医院。
            医院拥有本部（海曙区西北街41号）和北院区（江北区永丰北路175号）两大院区，总占地面积约10万平方米，建筑面积16.5万平方米，
            核定床位2100张，2025年新建医疗综合大楼投用后床位将扩容至4000张。  
        </p>
        <p class="intro-text">
            医院人才济济，截至2022年拥有职工3189人，其中高级职称专家686人、硕士及以上学历933人（含博士87人），国家级及省市级人才称号获得者62
            人，形成了一支结构合理、技术精湛的医疗队伍。学科建设成果丰硕，拥有4个省区域专病中心、3个省市共建重点学科，烧伤科、
            神经外科等多个专科跻身中国医院科技量值百强；同时依托国科宁波生命与健康产业研究院、国家消化系统疾病临床医学研究中心分中心等高端平台，
            近五年承担科研项目395项，发表论文1222篇，获专利796项，科研实力显著。  
        </p>
        <p class="intro-text">
            医疗服务能力突出，年门急诊量超173万人次，出院病人10万余人次，手术量4.4万余例，配备PET-CT、3.0T磁共振、三代双源CT等先进设备，
            肩负着区域急危重症救治与突发公共卫生事件处置重任。作为国家首批住院医师规范化培训基地，医院还承担着医学人才培养的重要使命，
            持续为医疗卫生事业输送优质力量。  
        </p>
        <p class="intro-text">
            从百年前的西医萌芽到今天的区域医疗标杆，宁波市第二医院始终以“仁心仁术、济众博施”为宗旨，用精湛医术和人文关怀守护着百姓健康，
            在新时代医疗改革浪潮中不断续写着跨越三个世纪的医者传奇。
        </p>
    </div>

    <!-- 服务特色 -->
    <div class="services">
      <div class="section-title">
        <h2>服务特色</h2>
        <div class="title-line"></div>
      </div>
      <div class="service-cards">
        <div class="service-card">
          <el-icon class="service-icon"><LocationFilled /></el-icon>
          <h3>专家团队</h3>
          <p>汇聚各领域医学专家，提供专业诊疗意见</p>
        </div>
        <div class="service-card">
          <el-icon class="service-icon"><Clock /></el-icon>
          <h3>便捷预约</h3>
          <p>在线预约挂号，节省您的宝贵时间</p>
        </div>
        <div class="service-card">
          <el-icon class="service-icon"><Tickets /></el-icon>
          <h3>医保服务</h3>
          <p>支持各类医保，减轻患者就医负担</p>
        </div>
      </div>
    </div>

    <!-- 联系信息 -->
    <div class="contact-info">
      <div class="section-title">
        <h2>联系我们</h2>
        <div class="title-line"></div>
      </div>
      <div class="contact-cards">
        <div class="contact-card">
          <el-icon class="contact-icon"><Location /></el-icon>
          <h3>医院地址</h3>
          <p>浙江省宁波市海曙区西北街168号</p>
        </div>
        <div class="contact-card">
          <el-icon class="contact-icon"><Phone /></el-icon>
          <h3>联系电话</h3>
          <p>总机: 0574-87089999</p>
          <p>急诊: 0574-87089120</p>
        </div>
        <div class="contact-card">
          <el-icon class="contact-icon"><Clock /></el-icon>
          <h3>门诊时间</h3>
          <p>周一至周五: 8:00-17:00</p>
          <p>周六至周日: 8:00-12:00</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-banner {
  height: 400px;
  border-radius: 12px;
  margin-bottom: 40px;
  background: url('@/assets/images/background.jpg') no-repeat center center;
  background-size: cover;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.3) 50%, rgba(0, 0, 0, 0) 100%);
  }

  .banner-content {
    position: relative;
    z-index: 2;
    padding: 0 60px;
    color: white;
    max-width: 600px;

    h1 {
      font-size: 42px;
      margin-bottom: 16px;
      font-weight: 700;
    }

    .slogan {
      font-size: 20px;
      margin-bottom: 30px;
      opacity: 0.9;
    }

    .banner-buttons {
      display: flex;
      gap: 16px;

      .action-button {
        padding: 12px 24px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 8px;
        display: flex;
        align-items: center;
        gap: 8px;
        transition: all 0.3s ease;

        &.secondary {
          background-color: rgba(255, 255, 255, 0.2);
          color: white;
          border: 2px solid rgba(255, 255, 255, 0.3);

          &:hover {
            background-color: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.5);
          }
        }
      }
    }
  }
}

.section-title {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    font-size: 28px;
    color: $text-primary;
    margin-bottom: 12px;
    font-weight: 600;
  }

  .title-line {
    height: 3px;
    width: 60px;
    background: linear-gradient(90deg, $primary-color, $primary-light);
    margin: 0 auto;
  }
}

.hospital-intro {
  margin-bottom: 50px;
  padding: 40px;
  background: linear-gradient(135deg, $card-bg 0%, rgba($primary-color, 0.02) 100%);
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border-left: 4px solid $primary-color;
  position: relative;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  }
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, $primary-color, $primary-light, $success-color);
    border-radius: 16px 16px 0 0;
  }

  .intro-text {
    font-size: 17px;
    line-height: 2;
    color: $text-secondary;
    text-align: justify;
    margin-bottom: 20px;
    text-indent: 2em;
    position: relative;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &:first-child {
      font-size: 18px;
      color: $text-primary;
      font-weight: 500;
      
      &::first-letter {
        font-size: 24px;
        font-weight: 600;
        color: $primary-color;
        float: left;
        line-height: 1;
        margin-right: 4px;
        margin-top: 4px;
      }
    }
    
    // 为重要信息添加高亮效果
    &:nth-child(2) {
      background: rgba($success-color, 0.05);
      padding: 20px;
      border-radius: 8px;
      border-left: 3px solid $success-color;
      margin: 25px 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hospital-intro {
    padding: 25px 20px;
    margin-bottom: 30px;
    
    .intro-text {
      font-size: 16px;
      line-height: 1.8;
      text-indent: 1.5em;
      
      &:first-child {
        font-size: 17px;
        
        &::first-letter {
          font-size: 20px;
        }
      }
      
      &:nth-child(2) {
        padding: 15px;
        margin: 20px 0;
      }
    }
  }
}

.services {
  margin-bottom: 50px;

  .service-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;

    .service-card {
      background-color: $card-bg;
      padding: 30px 20px;
      border-radius: 12px;
      text-align: center;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
      }

      .service-icon {
        font-size: 40px;
        color: $primary-color;
        margin-bottom: 20px;
      }

      h3 {
        font-size: 20px;
        margin-bottom: 12px;
        color: $text-primary;
      }

      p {
        color: $text-secondary;
        line-height: 1.6;
      }
    }
  }
}

.contact-info {
  margin-bottom: 50px;

  .contact-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;

    .contact-card {
      background-color: $card-bg;
      padding: 30px 20px;
      border-radius: 12px;
      text-align: center;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

      .contact-icon {
        font-size: 32px;
        color: $primary-color;
        margin-bottom: 16px;
      }

      h3 {
        font-size: 18px;
        margin-bottom: 12px;
        color: $text-primary;
      }

      p {
        color: $text-secondary;
        margin-bottom: 8px;
        line-height: 1.5;
      }
    }
  }
}

@media (max-width: 768px) {
  .main-banner {
    height: 350px;

    .banner-content {
      padding: 0 30px;

      h1 {
        font-size: 32px;
      }

      .slogan {
        font-size: 18px;
      }
    }
  }

  .service-cards,
  .contact-cards {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .service-cards,
  .contact-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

