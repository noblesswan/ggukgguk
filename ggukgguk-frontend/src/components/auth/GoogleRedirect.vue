<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

const store = useStore();
const router = useRouter();
const isLoading = ref(true);

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  if (!code) {
    console.error('리다이렉트 주소를 찾을 수 없습니다.');
    return;
  }

  const AccessToken = ref("");

  try {
    const response = await fetch('https://oauth2.googleapis.com/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams({
        client_id: process.env.VUE_APP_GOOGLE_LOGIN_KEY,
        client_secret: process.env.VUE_APP_GOOGLE_SECRET_KEY,
        code,
        grant_type: 'authorization_code',
        redirect_uri: process.env.VUE_APP_GOOGLE_REDIRECT_URL
      }),
    });
    const data = await response.json();
    if (data.error) {
      throw new Error(data.error);
    }
    console.log(data)
    
      AccessToken.value = data.access_token;
      store.dispatch("auth/handleGoogleAuth",AccessToken.value)
      .then((response) =>{
        isLoading.value = false;
        console.log("구글 로그인 성공");
        console.log(response);
        router.push({ name: "recordMain" });
      });


  } catch (error) {
    console.error('토큰을 접근하는데 실패', error);
  }
});
</script>

<template>
  <v-overlay
      v-model="isLoading"
      scroll-strategy="block"
      persistent
      class="loading-overlay"
    >
      <v-progress-circular
        color="primary"
        indeterminate
        size="64"
      ></v-progress-circular>
    </v-overlay>
</template>

<style scoped>
.loading-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
