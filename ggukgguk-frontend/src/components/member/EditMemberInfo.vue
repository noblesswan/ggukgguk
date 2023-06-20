<script setup>
import { onMounted, ref , computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const memberId = ref('');
const memberPw = ref('');
const memberCheckPw = ref('');
const memberName = ref('');
const memberEmail = ref('');
const memberBirth = ref('');
const memberNickname = ref('');
const memberPhone = ref('');
const form = ref(null);

const store = useStore();
const router = useRouter();

onMounted(() => {
  fetchUpdatedMemberInfo();
});

const passwordMatchRule = (value) => {
  if (value !== memberPw.value) {
    return '비밀번호가 일치하지 않습니다.';
  }
  return true;
};

const cancel = () =>{
  router.push({ name: "recordMain" });
}


const memberInfo = computed(() => {
    return store.getters['auth/memberInfo'];
})

async function modify() {
  await store.dispatch('member/modfiyMemberInfo', {
      memberId: memberId.value,
      memberPw: memberPw.value || null,
      memberName: memberName.value,
      memberNickname: memberNickname.value,
      memberEmail: memberEmail.value,
      memberPhone: memberPhone.value,
      memberBirth: memberBirth.value,
    })
    .then(() => {
      alert('회원정보 수정이 되었습니다.');
      fetchUpdatedMemberInfo();
      router.push({ name: 'recordMain' });
    })
    .catch((error) => {
      console.error('회원 정보 수정 실패');
      console.error(error);
    });
}

async function fetchUpdatedMemberInfo() {

  memberId.value = memberInfo.value.memberId;
    await store.dispatch('member/informationMemberDetail', memberId.value)
      .then((response) => {
        const memberData = response.data.data;
        memberName.value = memberData.memberName;
        memberEmail.value = memberData.memberEmail;
        memberBirth.value = memberData.memberBirth;
        memberNickname.value = memberData.memberNickname;
        memberPhone.value = memberData.memberPhone;
      })
      .catch((error) => {
        console.error('회원 세부정보 조회 실패');
        console.error(error);
      });
  
  // memberId.value = store.getters['auth/memberInfo'].memberId;
  // store.dispatch('member/informationMemberDetail', memberId.value)
  //   .then((response) => {
  //     const memberData = response.data.data;
  //     memberName.value = memberData.memberName;
  //     memberEmail.value = memberData.memberEmail;
  //     memberBirth.value = memberData.memberBirth;
  //     memberNickname.value = memberData.memberNickname;
  //     memberPhone.value = memberData.memberPhone;
  //   })
  //   .catch((error) => {
  //     console.error('회원 세부정보 조회 실패');
  //     console.error(error);
  //   });
}

const BirthRules = [
  (value) => {
    return (
      (!!value || "생년월일이 비워져있습니다.") &&
      ((value && /^\d{4}-\d{2}-\d{2}$/.test(value)) || 'YYYY-MM-DD 형식으로 입력해주세요.')
    );
  },
];

const phoneRules = [
  (value) => {
    return (
      (!!value || "휴대폰번호를 입력해주세요.") &&
      ((value && /^[0-9]+$/.test(value)) || "숫자만 입력해주세요")
    );
  }
]

</script>

<template>
    <v-card>
        <v-card-title>
            <span class="headline">{{ memberInfo.memberNickname }} 님 회원정보 </span>
        </v-card-title>
        <v-card-text>
            <v-form :ref="form">
                <v-text-field v-model="memberId" disabled label="ID"></v-text-field>
                <v-text-field type="password" v-model="memberPw" label="새로운 비밀번호 입력"></v-text-field>
                <v-text-field type="password" v-model="memberCheckPw" :rules="[passwordMatchRule]"
                    label="새로운 비밀번호 한번 더 입력"></v-text-field>
                <v-text-field v-model="memberEmail" disabled label="이메일"></v-text-field>
                <v-text-field v-model="memberBirth" :rules="BirthRules" label="생일"></v-text-field>
                <v-text-field v-model="memberNickname" label="닉네임"></v-text-field>
                <v-text-field v-model="memberPhone" :rules="phoneRules" label="휴대폰번호"></v-text-field>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="cancel">취소</v-btn>
            <v-btn color="blue darken-1" text @click="modify">수정</v-btn>
        </v-card-actions>
    </v-card>
</template>
