<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

function goBack() {
  router.go(-1);
}

</script>

<template>
<v-app-bar class="mb-20" :elevation="0" color="#E0E3DA">
  <v-btn icon
  @click="goBack">
    <v-icon>mdi-arrow-left</v-icon>
  </v-btn>
</v-app-bar>

</template>

<style scoped>
.v-app-bar {
  border-bottom-style: double;
  border-bottom-width: 3px;
  border-bottom-color: #111111;
}
</style>