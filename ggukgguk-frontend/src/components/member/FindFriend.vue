<script setup>
import { friend } from '@/api';
import { ref, computed } from 'vue';
import { useStore } from 'vuex';

const FindList = ref([]);
const store = useStore();
const findId = ref("");

const check = ref(false);
const sendDialog = ref(false);
const resendDialog = ref(false);

// 본인 로그인 아이디 가져오기 
const memberId = computed(() => {
    return store.getters['auth/memberInfo'].memberId;
})

// 친구 추가하기
async function addFriend(friendId, memberEmail) {
    // console.log("테스트");
    console.log(friendId);
    console.log(memberEmail);
    await store.dispatch('friend/additionFriendId', friendId)
        .then(() => {
            sendDialog.value = true;
        }).catch(() => {
            resendDialog.value =true;
        })
    friendRequestEmail(memberEmail);
    check.value = true;
}

// 친구 차단하기 
// function refuseFriend(friendId) {
//     store.dispatch('friend/delRelationshipFriend', friendId);
//     // .then(() => {
//     // alert('상대방을 차단하였습니다.');
//     // });
// }
// 나의 친구 목록 조회.=> 친구들의 ID 값만 가져오기
const friendId = computed(() => {
    return store.getters['friend/friendList'].map(friend => friend.memberId);
});

// 추가 및 버튼 생성 조건 
function isFriend(memberId) {
    return friendId.value.includes(memberId);
}

// 내가 로그인한 ID가 아닌 회원 ID 조회
const filteredFriendList = computed(() => {
    return FindList.value.filter(friend => friend.memberId !== memberId.value);
});

// await await 키워드를 사용하여 비동기 작업을 수행합니다.
// await 키워드를 사용하여 비동기 작업이 완료될 때까지 기다립니다. 이 때, 해당 비동기 작업이 Promise를 반환하는 함수여야 합니다.
// 아이디 일부값을 찾기
async function findFriendList() {
    try {
        const response = await friend.getPartialIdSearch(findId.value);
        FindList.value = response.data.data;
    } catch (error) {
        console.error(error);
        // Handle error here
    }
}

// 상대방에게 친구 요청 안내 메일 전송
async function friendRequestEmail(memberEmail) {
    console.log(memberEmail)
    return await friend.friendRequestInfo({
        sendTo: memberEmail,
    }).then((response) => {
        console.log("친구 요청 안내메일 전송")
        return response;
    }).catch(() => {

    });
}

</script>

<template>
    <v-container>
        <v-card-title>친구 추가</v-card-title>
    </v-container>
    <v-card class="mx-auto" width="300">
        <template v-slot:title>
            아이디 찾기
        </template>
        <v-text-field v-model="findId" label="찾고자 하는 친구 아이디"></v-text-field>
        <v-card-actions class="d-flex justify-center">
            <v-btn variant="outlined" @click="findFriendList">
                검색
            </v-btn>
        </v-card-actions>
    </v-card>
    <v-container>
        <v-row>
            <v-col v-for="friend in filteredFriendList" :key="friend.memberId" :id="friend.memberId" cols="12" xs="12">
                <v-card v-if="findId != memberId" class="card" :style="{ borderWidth: '2px' }">
                    <v-card-text>{{ friend.memberNickname }} 님</v-card-text>
                    <v-card-text :style="{ fontStyle: 'italic' }"> {{ friend.memberId }} </v-card-text>
                    <v-card-text :style="{ fontStyle: 'italic' }"> {{ friend.memberEmail }} </v-card-text>
                    <v-card-text class="text-end">
                        <span>
                            <!-- () => { detailNotify(notify.referenceId, notify.notificationTypeId); readNotify(notify.notificationId) } -->
                            <v-btn :disabled="check" v-if="!isFriend(friend.memberId)"
                                @click="() => { addFriend(friend.memberId, friend.memberEmail) }">추가</v-btn>
                            <v-dialog v-model="sendDialog" width="auto">
                                <v-card>
                                    <v-card-text>
                                        친구신청을 보냈습니다.
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn color="primary" block @click="sendDialog = false">닫기</v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                            <v-dialog v-model="resendDialog" width="auto">
                                <v-card>
                                    <v-card-text>
                                        이미 친구신청을 보냈습니다.
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn color="primary" block @click="resendDialog = false">닫기</v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                            <!-- <v-btn v-if="isFriend(friend.memberId)" @click="refuseFriend(friend.memberId)">차단</v-btn> -->
                        </span>
                    </v-card-text>
                    <v-divider></v-divider>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>