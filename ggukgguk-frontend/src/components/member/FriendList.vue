<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

const store = useStore();
const router = useRouter();
const dialog = ref(false);
const memberId = computed(() => {
    return store.getters['auth/memberInfo'].memberId;
})

const friendList = computed(() => {
    return store.getters['friend/friendList'];
})

// 나의 친구 정보 가져오기
onMounted(() => {
    getFriendList();
})
function getFriendList() {
    store.dispatch('friend/getFriendList', memberId);
}
// 친구 추가 및 차단으로 이동
function goToFriendAndblock() {
    router.push('/friend/new');
}
// 카톡 주소록의 친구들에게 나의 꾹꾹 아이디를 공유하기
function sendLinkDefault() {
    // 꾹꾹 회원으로 로그인 시, 나의 아이디를 공유하기 위해 먼저 카카오 로그인을 먼저 수행해야 함.
    if (!window.Kakao.isInitialized()) {
        window.Kakao.init("f0b4268c10a9c956df9816637eede528");
    }

    window.Kakao.Link.sendDefault({
        objectType: 'feed',
        content: {
            title: '꾹꾹 회원 아이디 공유',
            description: '친구분의 아이디는 ' + memberId.value +
                '이며 당신과 교류하고 싶습니다. #꾹꾹 #일기 #교환일기',
            imageUrl:
                'https://app.ggukgguk.online/img/landing_main.png',
            link: {
                mobileWebUrl: 'https://app.ggukgguk.online/login',
                webUrl: 'https://app.ggukgguk.online/login',
            },
        },
        buttons: [
            {
                title: '자세히 보기',
                link: {
                    mobileWebUrl: 'https://app.ggukgguk.online/login',
                    webUrl: 'https://app.ggukgguk.online/login',
                },
            }
        ],
    })
}

// 친구 차단하기 
function refuseFriend(friendId) {
    store.dispatch('friend/delRelationshipFriend', friendId)
        .then(() => {
            dialog.value = true;
    });
}

watch(friendList, () => {
    noMoreDown.value = false;
})

// let lastScrollPosition = 0;

const isLoadingForScrollEvent = ref(false);
const isLoading = ref(false);
const noMoreDown = ref(false);

function handleScroll() {
    const scrollY = window.scrollY || document.documentElement.scrollTop;
    const documentHeight = document.documentElement.scrollHeight;
    const windowHeight = window.innerHeight;

    // 스크롤이 맨 아래에 도달했는지 확인
    if (Math.ceil(scrollY + windowHeight) >= documentHeight && !isLoadingForScrollEvent.value) {
        if (noMoreDown.value) {
            return;
        }

        isLoadingForScrollEvent.value = true;
        getFriendsDown();
    }

    // lastScrollPosition = scrollY;
}

window.addEventListener('scroll', handleScroll);

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
})

function getFriendsDown() {
    isLoadingForScrollEvent.value = true;
    isLoading.value = true;

    store.dispatch("friend/getFriendList", memberId.value)
        .then((data) => {
            isLoadingForScrollEvent.value = false;
            isLoading.value = false;
            if (data?.length === 0) noMoreDown.value = true;
        })
        .catch((error) => {
            console.error('친구 리스트 조회 실패');
            console.error(error);
        })
}


</script>  
<template>
    <v-container>
        <v-card-title>친구 목록</v-card-title>
        <v-row>
            <v-col>
                <v-btn @click="sendLinkDefault">친구에게 아이디 공유하기</v-btn>
            </v-col>
            <v-col cols="auto" class="text-end">
                <v-btn @click="goToFriendAndblock">친구 추가 </v-btn>
            </v-col>
        </v-row>
    </v-container>
    <v-container>
        <v-row>
            <v-col v-for="friend in friendList" :key="friend.memberId" :id="friend.memberId" cols="12" xs="12">
                <v-card class="card" :style="{ borderWidth: '2px' }">
                    <v-card-text>{{ friend.memberNickname }} 님</v-card-text>
                    <v-card-text :style="{ fontStyle: 'italic' }"> {{ friend.memberId }} </v-card-text>
                    <v-card-text :style="{ fontStyle: 'italic' }"> {{ friend.memberEmail }} </v-card-text>
                    <v-card-actions class="justify-end pt-1 pb-0">
                        <v-btn @click="refuseFriend(friend.memberId)"> 차단
                            <v-dialog v-model="dialog" width="auto">
                                <v-card>
                                    <v-card-text>
                                        친구의 관계를 마무리하였습니다.
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn color="primary" block @click="dialog = false">닫기</v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-btn>
                    </v-card-actions>
                    <v-divider></v-divider>
                </v-card>
            </v-col>
        </v-row>
    </v-container></template>