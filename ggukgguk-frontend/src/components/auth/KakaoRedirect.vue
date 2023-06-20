<script setup>
import { ref,onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter} from "vue-router";
import axios from "axios";

const store = useStore();
const router = useRouter();
const isLoading = ref(true);

/**
 * 
 * @param {*} code 
 */
async function getKakaoToken(code) {
  console.log("loginWithKakao");
  const kakaoHeader = { // 엔티티 본문의 데이터를 해석할 수 있는 정보를 제공
    Authorization: process.env.VUE_APP_KAKAO_ADMIN_KEY, // 우리 웹서비스를 카카오 서비스에 등록해서 발급받은 ADMIN 키 
    "Content-type": "application/x-www-form-urlencoded;charset=utf-8", // 표현데이터 형식
  };
  try {
    const data = {
      grant_type: "authorization_code", // 권한 타입_ 따로 카카오 문서에서 지정 "authorization_code"
      client_id: process.env.VUE_APP_KAKAO_LOGIN_KEY, // 우리 웹서비스를 등록한 REST_API키 (예시 한국영화진흥사에서 해당 영화정보를 가져왔을 때 처럼 용도가 동일 )
      redirect_uri: process.env.VUE_APP_KAKAO_REDIRECT_URL, //리다이렉트 주소_ 우리 웹서비스에서 등록한 반환 받을 주소 지정
      code: code, // 리다이렉트 주소로 반환받은 인증 코드 
    };
    //위에서 명시한 데이터를 쿼리 스트링으로 반환하는 코드
    const queryString = Object.keys(data)
      .map(k => encodeURIComponent(k) + "=" + encodeURIComponent(data[k]))
      .join("&");
    const result = await axios.post( // 위에서 작성한 쿼리스트링과 헤더를 해당 카카오 서버에게 토큰을 달라고 post방식으로 요청
      "https://kauth.kakao.com/oauth/token",
      queryString,
      { headers: kakaoHeader }
    );
    return result; // accesstoken과 reflashtoken도 가져옴
  } catch (e) {
    return e;
  }
}

const AccessToken = ref(""); //위에서 받은 엑세스 토크 받을 참조 공간
// const refreshToken = ref("");
onMounted(async () => { // 미은트가 된 이후에 비동기적으로 실행 (즉, 위 다이렉트 주소가 실행되자마자 수행하는 형태)
  const queryParams = new URLSearchParams(window.location.search); 
  // 카카오 서버에서 수행되는 리다이렉트 주소 
  //https://localhost:9090/login/kakao-redirect?code=QISHA7wwGCdd-O2qMI27-AiesmYxVFOp7s9rmFNe1yv3s6oZ6dZu65v3V9bYNvMJD0iQlQorDR4AAAGHkv2Qug
  //
  const code = queryParams.get("code");

  if (code) {
    try {
      const tokenResult = await getKakaoToken(code);
      console.log("Kakao Token", tokenResult);
      AccessToken.value = tokenResult.data.access_token;
      // refreshToken.value = tokenResult.data.refresh_token;
      store.dispatch("auth/handleKakaoAuth",AccessToken.value)
      // store.dispatch("auth/handleKakaoAuth",AccessToken.value,refreshToken.value)
      .then((response) =>{
        isLoading.value = false;
        console.log("카카오 로그인 성공");
        console.log(response);
        router.push({ name: "recordMain" });
      });
    } catch (error) {
      console.error("Failed to get Kakao token", error);
    }
  }
});
</script>
<template>
    <v-overlay class="loading-overlay" v-model="isLoading" scroll-strategy="block" persistent>
          <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
    </v-overlay>
</template>
<style scoped>
.loading-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>