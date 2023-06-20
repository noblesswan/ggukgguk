<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();

const MENUS = {
    logout: 'login',
    main: 'adminMain',
    content: 'contentList',
    member: 'memberList',
    batch: 'batchStatus',
    notice: 'noticeList'
}

const memberInfo = computed(() => {
    return store.getters['auth/memberInfo'];
});

const memberRole = computed(() => {
    const { memberAuthority } = memberInfo.value;
    return memberAuthority === 'SYSTEM_ADMIN'
        ? '시스템 관리자'
        : memberAuthority === 'SERVICE_ADMIN'
            ? '서비스 관리자'
            : '잘못된 접근입니다.';
});

function goTo(from) {
    const name = MENUS[from];
    router.push({ name });
}

</script>

<template>
    <v-navigation-drawer>
        <v-list>
            <v-list-item style="text-align: center">
                <h1 style="color: #0f2a0f">꾹꾹 서비스관리</h1>
            </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list>
            <v-list-item
                :title="`${memberInfo.memberName} 관리자님`"
                :subtitle="memberRole"
            >
                <v-list-item-action>
                    <v-btn variant="text" class="mt-2" @click="goTo('logout')">로그아웃</v-btn>
                </v-list-item-action>
            </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list density="compact" nav>
            <v-list-item
                prepend-icon="mdi-google-analytics"
                title="메인 (통계)"
                value="main"
                @click="goTo('main')"
            ></v-list-item>
            <v-list-item
                prepend-icon="mdi-account-multiple"
                title="컨텐츠 관리"
                value="content"
                @click="goTo('content')"
            ></v-list-item>
            <v-list-item
                prepend-icon="mdi-account-group"
                title="회원 관리"
                value="member"
                @click="goTo('member')"
            ></v-list-item>
            <v-list-item
                prepend-icon="mdi-application-cog"
                title="배치작업 현황"
                value="batch"
                @click="goTo('batch')"
            ></v-list-item>
            <v-list-item
                prepend-icon="mdi-newspaper-variant-multiple"
                title="공지사항"
                value="notice"
                @click="goTo('notice')"
            ></v-list-item>
        </v-list>
    </v-navigation-drawer>
</template>