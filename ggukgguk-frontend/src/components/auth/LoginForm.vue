<script setup>
import { ref, onMounted, computed } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

const store = useStore();
const router = useRouter();
// const route = useRoute();

const form = ref("");
const memberId = ref("");
const memberPw = ref("");

const memberInfo = computed(() => {
  return store.getters['auth/memberInfo'];
})

// const to = computed(() => {
//   return route.params.redirect;
// })

const idRules = [
  (value) => {
    if (value) return true;
    return "아이디를 입력해주세요";
  },
];

const pwRules = [
  (value) => {
    if (value) return true;
    return "비밀번호를 입력해주세요";
  },
];

async function login() {
  const { valid } = await form.value.validate();
  if (!valid) return;

  store
    .dispatch("auth/login", {
      memberId: memberId.value,
      memberPw: memberPw.value,
    })
    .then(() => {
      // alert('로그인 성공');
      // router.push({path: to.value})
      if (memberInfo.value?.memberAuthority === 'SYSTEM_ADMIN'
        || memberInfo.value?.memberAuthority === 'SERVICE_ADMIN') {
        router.push({ name: "adminMain" });
      } else {
        router.push({ name: "recordMain" });
      }
    })
    .catch((error) => {
      if (error.code === "ERR_BAD_REQUEST") {
        alert("로그인에 실패했습니다. 아이디와 비밀번호를 다시 확인해주세요.");
      }
      console.log("로그인 실패");
      console.error(error);
    });
}
async function loginWithKakao() {
  const params = {
    redirectUri: process.env.VUE_APP_KAKAO_REDIRECT_URL,
  };
  window.Kakao.Auth.authorize(params);
}

onMounted(() => {
  store.commit("auth/setMemberInfo", { memberInfo: {}, accessToken: "", refreshToken: "" });
});

async function findId() {
  router.push({ name: "findMemberId" });

}

async function findPassword() {
  router.push({ name: "findMemberPw" });

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
  <div class="img-wrap">
    <img src="/img/landing_main.png" />
  </div>
  <v-sheet width="300" class="mx-auto">
    <v-form @submit.prevent ref="form">
      <v-text-field v-model="memberId" :rules="idRules" label="아이디"></v-text-field>
      <v-text-field type="password" v-model="memberPw" :rules="pwRules" label="비밀번호"></v-text-field>
      <v-btn type="submit" @click="login" block class="mt-2">로그인</v-btn>
      <div class="center">
        <div class="button-wrap">
          <img class="button-icon" src="/img/google_login.png" @click="loginWithGoogle" />
          <img class="button-icon" src="/img/kakao_login.png" @click="loginWithKakao" />
        </div>
      </div>
      <hr />
      <v-container>
        <v-row>
          <v-col class="text-center">
            <v-btn @click="findId">아이디 찾기</v-btn>
          </v-col>
          <v-col class="text-center">
            <v-btn @click="findPassword">비밀번호 찾기</v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-form>
  </v-sheet>
</template>

<style scoped>
.img-wrap {
  margin-top: 10%;
  text-align: center;
}

.img-wrap img {
  width: 300px;
}

.center {
  display: flex;
  justify-content: center;
}

.button-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 15px;
}

.button-wrap img {
  margin-left: 11px;
  margin-right: 17px;
}
.button-icon{
  width: 140px;
}
.text-center {
  text-align: center;
}
</style>