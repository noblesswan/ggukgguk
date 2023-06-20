<script setup>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { auth } from '../../api';

const store = useStore();
const memberPw = ref("");
const memberPwCheck = ref("");
const router = useRouter();


// 비빌번호 변경
function editPw() {
    console.log(memberEmail.value);
    if(memberPw.value != memberPwCheck.value){
        alert('비밀번호가 일치하지 않습니다. 확인해주세요.');
        memberPw.value = '';
        memberPwCheck.value = '';
    }else {
        return auth.modifyPassword({
            memberEmail : memberEmail.value,
            memberPw : memberPw.value})
        .then((response) => {
          console.log('비밀번호 성공');
          console.log(response);
          alert('비밀번호가 정상적으로 변경되었습니다. 다시 로그인해주세요.');
          router.push({ name: 'login' });
          return response;
        })
    }
}
const memberEmail = computed(() => {
    return store.getters['member/memberEmail'];
})





</script>

<template>
    <div class="img-wrap">
        <img src="/img/landing_main.png" />
    </div>
    <v-sheet width="300" class="mx-auto">
        <v-form @submit.prevent ref="form">
            <v-text-field type="password" v-model="memberPw" label="비밀번호 입력"></v-text-field>
            <v-text-field type="password" v-model="memberPwCheck" label="새 비밀번호 입력"></v-text-field>
            <v-btn type="submit" @click="editPw" block class="mt-2 mb-6">변경</v-btn>
        </v-form>
    </v-sheet>
</template>

<style scoped>
.img-wrap {
    margin-top: 10%;
    text-align: center;
}

img {
    width: 300px;
}
</style>