<script setup>

</script>

<template>
  <v-app>
      <router-view :key="$route.path"/>
  </v-app>
</template>

<style>
@font-face {
    font-family: 'Pretendard-Regular';
    src: url('https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff') format('woff');
    font-weight: 400;
    font-style: normal;
}

body {
  font-family: 'Pretendard-Regular';
}
</style>