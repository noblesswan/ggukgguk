<script setup>
import { ref} from "vue";
import { useStore } from "vuex";
import { auth } from '../../api';
import { useRouter } from "vue-router";

const store = useStore();
const memberId = ref("");
const memberEmail = ref("");
const doubleCheck = ref(false);
const emptyMember = ref(false);
const certificationNumber = ref("");
const router = useRouter();

// watch(doubleCheck, (newValue) => {
//     if (newValue === true) {
//         methodToExecuteWhenTemplateAppears();
//     }
// });

// 인증번호 전달.
function methodToExecuteWhenTemplateAppears() {
    console.log('doubleCheck가 true이면 실행되는 메서드 코드 작성');
    store.dispatch('auth/handleCertificationPw', {
        sendTo: memberEmail.value
    }).then((response) => {
        console.log("인증번호 전달 완료")
        console.log(response);
    });
}

// 인증번호 확인
function cetificationCheck() {
    return auth.findAuthenticationCode({
        sendTo: memberEmail.value,
        certificationNumber: certificationNumber.value
    }).then((response) => {
        console.log("인증번호 일치")
        console.log(response);
        alert('인증번호가 일치하여 비밀번호 수정페이지로 이동합니다.');
        store.commit('member/setMemberEmail', memberEmail); 
        router.push({ name: "modifyPw"});
        
        // router.push({ name: "modifyPw", query: { email: memberEmail.value } });
        // 이동할 라우터 페이지 명시.
        return response;
    }).catch((error) => {
        console.error(error);
        alert('인증번호가 일치하지 않습니다.');
    });
}

// 비빌번호 찾기
function findPwByIdEmail() {
    store.dispatch("auth/handleFindPw",
        {
            memberEmail: memberEmail.value,
            memberId: memberId.value
        })
        .then(() => {
            console.log("테스트 수행");
            emptyMember.value = false;
            methodToExecuteWhenTemplateAppears();
            doubleCheck.value = true;
        }).catch(() => {
            emptyMember.value = true;
            // doubleCheck.value = false;
            alert('등록된 아이디와 이메일 주소가 일치하지 않습니다.');
        });
}

</script>
<template>
    <div class="img-wrap">
        <img src="/img/landing_main.png" />
    </div>
    <v-sheet width="300" class="mx-auto">
        <v-form @submit.prevent ref="form">
            <v-text-field v-model="memberId" label="아이디 입력"></v-text-field>
            <v-text-field v-model="memberEmail" label="이메일 주소 입력"></v-text-field>
            <v-btn type="submit" @click="findPwByIdEmail" block class="mt-2 mb-6">비밀번호 찾기</v-btn>
        </v-form>
    </v-sheet>
    <v-container v-if="doubleCheck" class="text-center">
        <v-form>
            <v-card class="mx-auto" width="300">
                <template v-slot:title>
                    메일 인증번호를 전송하였습니다.
                    <v-text-field v-model="certificationNumber" label="인증번호 입력"></v-text-field>
                    <v-btn @click="cetificationCheck" block class="mt-2 mb-6">인증번호 확인</v-btn>
                </template>
            </v-card>
        </v-form>
    </v-container>
    <v-card v-if="emptyMember" class="mx-auto" width="300">
        <template v-slot:title>
            가입된 이메일 주소가 아닙니다.
        </template>
        <v-card-text></v-card-text>
    </v-card>
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