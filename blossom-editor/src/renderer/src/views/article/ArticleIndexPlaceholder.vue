<template>
  <div class="article-index-placeholder-root">
    <bl-row just="center" align="flex-end">
      <div class="iconbl bl-a-leftdirection-line">
        <div class="desc-line">
          <div class="desc-text">显示排序</div>
        </div>
      </div>
      <div class="iconbl bl-cloud-line"></div>
      <div class="iconbl bl-a-lowerrightpage-line"></div>
      <div class="iconbl bl-star-line"></div>
      <div class="iconbl bl-a-cloudrefresh-line"></div>
      <div class="iconbl bl-a-fileadd-line">
        <div class="desc-line">
          <div class="desc-text">新增文档</div>
        </div>
      </div>
      <div class="iconbl bl-correlation-line">
        <div class="desc-line">
          <div class="desc-text">引用知识网络</div>
        </div>
      </div>
    </bl-row>
    <!-- <bl-row just="center"> -->
    <div class="keymaps-container">
      <bl-row>
        <div class="label">隐藏菜单</div>
        <div class="key">{{ keymaps.hideDocs }}</div>
      </bl-row>
      <bl-row>
        <div class="label">隐藏目录</div>
        <div class="key">{{ keymaps.hideToc }}</div>
      </bl-row>
      <bl-row>
        <div class="label">格式化</div>
        <div class="key">{{ keymaps.formatAll }}</div>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { keymaps } from './scripts/editor-tools'
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';

.article-index-placeholder-root {
  @include box(100%, 100%);
  @include flex(column, center, center);

  .iconbl {
    @include themeColor(#dbdbdb, #323232);
    font-size: 25px;
    padding: 3px 2px;
    transition: 0.3s;
    position: relative;
  }

  .desc-line {
    @include themeBorder(1px, #dcdcdc, #323232);
    position: absolute;

    .desc-text {
      @include themeColor(#dbdbdb, #323232);
      font-size: 13px;
      position: absolute;
      width: 100px;
    }
  }

  .bl-a-leftdirection-line {
    .desc-line {
      border-top: none;
      border-left: none;
      height: 20px;
      width: 30px;
      left: -15px;
      top: 30px;

      .desc-text {
        left: -55px;
        top: 10px;
      }
    }
  }

  .bl-a-fileadd-line {
    .desc-line {
      border-top: none;
      border-right: none;
      border-bottom: none;
      height: 25px;
      top: 30px;
      left: 15px;

      .desc-text {
        top: 30px;
        left: -27px;
      }
    }
  }

  .bl-correlation-line {
    font-size: 40px;
    padding-bottom: 0px;

    .desc-line {
      border-top: none;
      border-right: none;
      height: 20px;
      width: 30px;
      left: 20px;

      .desc-text {
        left: 35px;
        top: 10px;
      }
    }
  }

  .keymaps-container {
    margin-top: 70px;
    margin-left: 20px;
    width: 210px;
    font-size: 13px;
    .label {
      @include themeColor(#dbdbdb, #323232);
      width: 60px;
      text-align: right;
      margin-right: 10px;
    }
    .key {
      @include themeBg(#f1f1f1, #323232);
      @include themeColor(#c6c6c6, #151515);
      border-radius: 4px;
      padding: 2px 5px;
      margin-top: 5px;
    }
  }
}
</style>
