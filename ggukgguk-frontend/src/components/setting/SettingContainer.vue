<script setup>
import { ref, computed, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();

const MENUS = {
    logout: 'login',
    modifyMemberInfo: 'MemberInfo',
    initLocalStorage: 'initLocalStorage',
    noticeList: 'settingNotice'
}

const memberInfo = computed(() => {
    return store.getters['auth/memberInfo'];
})

const allowEmail = ref(memberInfo.value.memberAllowEmail);

function goTo(from) {
    const name = MENUS[from];

    if (name === 'initLocalStorage') {
        window.localStorage.clear();
        router.push({ name: MENUS['logout'] });
        return;
    }
        
    router.push({ name });
}

function updateEmailAllow() {
    const newMemberInfo = {...memberInfo.value};
    newMemberInfo.memberAllowEmail = allowEmail.value;
    store.dispatch('member/modfiyMemberInfo', newMemberInfo)
    .then(() => {
        console.log('이메일 수신 여부 업데이트 성공');
        store.commit('auth/updateMemberAllowEmail', allowEmail.value);
    })
    .catch((error) => {
        console.error('이메일 수신 여부 업데이트 실패');
        console.error(error);
        alert('이메일 수신 여부 업데이트에 실패했습니다.\n문제가 반복되면 고객센터로 문의해주세요.');
    });
}

watch(allowEmail, () => {
    updateEmailAllow();
});

</script>

<template>
    <h1>설정</h1>
    <v-card>
        <v-list>
            <v-list-item>
                <v-list-item-title>계정 정보</v-list-item-title>
            </v-list-item>
            <v-list-item>
                <v-card>
                    <v-card-subtitle>
                        {{ `${memberInfo.memberNickname}(${memberInfo.memberId})님` }}
                    </v-card-subtitle>
                    <v-card-actions>
                        <v-btn @click="goTo('logout')">로그아웃</v-btn>
                    </v-card-actions>
                </v-card>
            </v-list-item>
            <v-list-item value="modifyMemberInfo" @click="goTo('modifyMemberInfo')">
                <v-list-item-title>회원정보 수정</v-list-item-title>
            </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list>
            <v-list-item value="initLocalStorage" @click="goTo('initLocalStorage')">
                <v-list-item-title>어플리케이션 초기화</v-list-item-title>
                <v-list-item-subtitle>어플리케이션이 사용하는 로컬스토리지를 초기화합니다.</v-list-item-subtitle>
            </v-list-item>
            <v-list-item>
                <v-list-item-title>이메일 수신 설정</v-list-item-title>
                <v-list-item-subtitle>주요 알림을 이메일로 수신할 것인지 여부를 선택할 수 있습니다.</v-list-item-subtitle>
                <v-list-item-action>
                    <v-switch v-model="allowEmail" color="primary"></v-switch>
                    <div class="mt-2" style="height: 56px;">{{ allowEmail ? '수신 허용' : '수신 거부' }}</div>
                </v-list-item-action>
            </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list>
            <v-list-item value="noticeList" @click="goTo('noticeList')">
                <v-list-item-title>공지사항</v-list-item-title>
                <v-list-item-subtitle>꾹꾹의 공지사항을 확인할 수 있습니다.</v-list-item-subtitle>
            </v-list-item>
        </v-list>
    </v-card>
</template>