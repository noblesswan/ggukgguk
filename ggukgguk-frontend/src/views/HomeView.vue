<script setup>
import { computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();

const loginInfo = computed(() => {
  return store.getters['auth/memberInfo'];
});

const isLogin = computed(() => {
  return store.getters['auth/isLogin'];
});

function onLogout() {
  store.dispatch('auth/logout');
}

function skipLogin() {
  if (!isLogin.value) {
    return;
  }

  if (loginInfo.value?.memberAuthority === 'NORMAL')
    router.push({ name: 'recordMain' });
  else if (loginInfo.value?.memberAuthority === 'SYSTEM_ADMIN'
    || loginInfo.value?.memberAuthority === 'SERVICE_ADMIN')
    router.push({ name: 'adminMain' });
}

onMounted(() => {
  skipLogin();
})

function regiserInfo() {
  router.push({ name: 'memberRegiseter' });
}

async function loginWithKakao() {
  const params = {
    redirectUri: process.env.VUE_APP_KAKAO_REDIRECT_URL,
  };
  window.Kakao.Auth.authorize(params);
}

async function loginWithGoogle() {
  const params = new URLSearchParams({
    client_id: process.env.VUE_APP_GOOGLE_LOGIN_KEY,
    redirect_uri: process.env.VUE_APP_GOOGLE_REDIRECT_URL,
    response_type: 'code',
    scope: 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email',
  });

  window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
}

</script>

<template>
  <div class="container">
    <div class="landing-wrap">
      <img src="/img/landing_main.png">
    </div>
    <div class="landing-wrap">
      <img src="/img/landing_text.png">
    </div>

    <div class="landing-wrap">
      <v-btn id="to-register-btn" @click="regiserInfo" rounded="xl" size="x-large" color="primary">회원가입</v-btn> <br><br>
      <router-link id="to-login-link" :to="{ name: 'login' }">이미 회원이신가요?</router-link>
    </div>
    <div class="center">
      <div class="button-wrap">
        <img src="/img/google_login.png" @click="loginWithGoogle" />
        <img src="/img/kakao_login.png" @click="loginWithKakao" />
      </div>
    </div>
    <div class="debug">
      {{ loginInfo }}
      <br>
      <router-link :to="{ name: 'login' }" v-if="!isLogin">로그인</router-link> <br>
      <button @click="onLogout" v-if="isLogin">로그아웃</button> <br>
    </div>

    <v-footer class="text-center d-flex flex-column mt-16 py-16" style="width: 100%; font-size: 90%; background-color: #D3D3D3;">
      <div>
        꾹꾹 - 기록자들, {{ new Date().getFullYear() }} <br>
        한국소프트웨어산업협회 솔트룩스 과정
      </div>
    </v-footer>
  </div>
</template>

<style scoped>
.container {
  width: 100;
}

.landing-wrap {
  text-align: center;
  margin-top: 80px;
  width: 100%;
}

img {
  width: 80%;
}

#to-register-btn {
  width: 350px;
}

#to-login-link {
  color: black;
  text-decoration-line: none;
}

.debug {
  display: none;
  font-size: 8px;
  margin-top: 60px;
}

@media (min-width: 768px) {
  img {
    width: 30%;
  }
}
.center {
  display: flex;
  justify-content: center;
}

.button-wrap {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  width: 50%;
}

.button-wrap img {
  margin-right: 16px;
}

.text-center {
  text-align: center;
}
</style>