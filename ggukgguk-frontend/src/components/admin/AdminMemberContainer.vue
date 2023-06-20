<script setup>
import { computed, onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { admin } from '@/api';

const store = useStore();
const router = useRouter();


onMounted(() => {
    getMemberList();
})

const memberList = computed(() => {
    return store.getters['admin/memberList'];
})

const currentPage = computed(() => {
    return store.getters['admin/memberOption'].page;
});

const totalPage = computed(() => {
    const totalItem = store.getters['admin/memberTotal'];
    const pageSize = store.getters['admin/memberOption'].size;
    console.log(totalItem, pageSize);
    return Math.ceil(totalItem / pageSize);
})
const memberOption = computed(() => {
    return store.getters['admin/memberOption'];
});

watch(currentPage, () => {
    store.dispatch("admin/getMemberList")
        .catch((error) => {
            console.error('멤버관리 조회 실패');
            console.error(error);
        })
})

function getMemberList() {
    store.dispatch("admin/getMemberList")
        .then(() => {
            console.log('성공');
        })
        .catch((error) => {
            console.error('멤버관리 조회 실패');
            console.error(error);
        })
}
function deleteMember(memberId){
    admin.deleteMember({memberId})
        .then(() => {
            router.go(0);
        })
        .catch((error) => {
            console.error(error);
        })
}
</script>

<template>
    <v-col class="mt-8">
        <v-row class="d-flex flex-column align-center justify-center">
            <v-table style="width: 100%">
                <thead>
                    <tr>
                        <th class="text-center">
                            회원아이디
                        </th>
                        <th class="text-center">
                            닉네임
                        </th>
                        <th class="text-center">
                            이메일
                        </th>
                        <th class="text-center">
                            연락처
                        </th>
                        <th class="text-center">
                            생년월일
                        </th>
                        <th class="text-center">
                            회원 탈퇴
                        </th>

                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in memberList" :key="item.memberId" class="text-center">
                        <td>{{ item.memberId }}</td>
                        <td>{{ item.memberName }}</td>
                        <td>{{ item.memberEmail }}</td>
                        <td>{{ item.memberPhone }}</td>
                        <td>{{ item.memberBirth }}</td>
                        <v-btn color="primary" @click="() => deleteMember(item.memberId)">탈퇴</v-btn>
                    </tr>
                </tbody>
            </v-table>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center">
            <v-pagination v-model="memberOption.page" :total-visible="5" :length="totalPage"></v-pagination>
        </v-row>
    </v-col>
</template>
