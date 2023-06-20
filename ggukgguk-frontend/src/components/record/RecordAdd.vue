<script setup>
    import { computed, ref, onMounted, watch } from 'vue';
    import { useStore } from 'vuex';
    import { useRouter } from 'vue-router';
    import CaptureImage from './media/CaptureImage.vue';
    import CaptureVideo from './media/CaptureVideo.vue';
    import CaptureAudio from './media/CaptureAudio.vue';
    import CaptureLocation from './media/CaptureLocation.vue';
    import RecordMap from './RecordMap.vue';

    const store = useStore();
    const memberId = computed(() => {
        return store.getters['auth/memberInfo'].memberId;
    });
    const MAX_COMMENT_LENGTH = 512;
    const MIN_WORD_COUNT = 3;
    const isLoading = ref(false);

    const router = useRouter();

    let isMobile = false;

    onMounted(() => {
        if (navigator.userAgent.match(/Android/i) || navigator.userAgent.match(/webOS/i) || navigator.userAgent.match(/iPhone/i) ||
            navigator.userAgent.match(/iPad/i) || navigator.userAgent.match(/iPod/i) || navigator.userAgent.match(/BlackBerry/i) ||
            navigator.userAgent.match(/Windows Phone/i)) {
                isMobile = true;
        }
    })

    const recordComment = ref('');
    const commentLength = computed(() => {
        return recordComment.value.length;
    });

    const friendSearch = ref('');
    const friendListRaw = computed(() => {
        return store.getters['member/friendList'];
    });
    const friendWholeList = computed(() => {
        return friendListRaw.value.map(item => {
            return {
                title: `${item.memberNickname} (${item.memberName})`,
                value: item.memberId
            }
        })
    });
    const friendsSearchResultList = ref([]);
    const friendSearchVisible = ref(false);

    const recordIsOpen = ref(true);

    watch(friendListRaw, () => {
        friendsSearchResultList.value = friendWholeList.value;
    });

    watch(friendSearch, (newVal) => {
        friendsSearchResultList.value = friendWholeList.value.map((item) => {
            if (item?.value.includes(newVal)) {
                return item;
            } else if (item?.title.includes(newVal)) {
                return item;
            } else {
                return null;
            }
        })
    });

    const captureImageVisible = ref(false);
    const recordImage = ref(null);
    const recordImageUrl = ref('');

    const captureVideoVisible = ref(false);
    const recordVideo = ref(null);
    const recordVideoUrl = ref('');

    const captureAudioVisible = ref(false);
    const recordAudio = ref(null);
    const recordAudioUrl = ref('');

    const captureLocationVisible = ref(false);
    const recordLocationX = ref(0);
    const recordLocationY = ref(0);


    function openInputGallery() {
        document.querySelector("input#input-media-from-gallery").click();
    }

    function onInputChange(e) {
        const input = e.target;
        if (input.files && input.files[0]) {
            recordImage.value = input.files[0];
        }

        recordImageUrl.value = URL.createObjectURL(recordImage.value);
    }

    function openInputImage() {
        if (isMobile)
            document.querySelector("input#input-image-from-camera").click();
        else
            captureImageVisible.value = true;
    }

    function handleImageCapture(pictureBlob) {
        recordVideo.value = null;
        recordVideoUrl.value = '';
        recordAudio.value = null;
        recordAudio.value = '';

        recordImage.value = pictureBlob;
        recordImageUrl.value = URL.createObjectURL(pictureBlob);
        captureImageVisible.value = false;
    }

    function openVideoCamera() {
        captureVideoVisible.value = true;
    }

    function handleVideoCapture(videoBlob) {
        recordImage.value = null;
        recordImageUrl.value = '';
        recordAudio.value = null;
        recordAudio.value = '';

        recordVideo.value = videoBlob;
        recordVideoUrl.value = URL.createObjectURL(videoBlob);
        captureVideoVisible.value = false;
    }

    function openAudioRecoder() {
        captureAudioVisible.value = true;
    }

    function handleAudioCapture(audioBlob) {
        recordImage.value = null;
        recordImageUrl.value = '';
        recordVideo.value = null;
        recordVideoUrl.value = '';
        recordAudio.value = audioBlob;
        recordAudioUrl.value = URL.createObjectURL(audioBlob);
        captureAudioVisible.value = false;
    }

    function formatDate(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    }

    function setTodayDate() {
        store.commit('record/setStartDateStr', formatDate(new Date()));
    }

    function uploadRecord() {
        if (recordComment.value.split(' ').length < MIN_WORD_COUNT) {
            alert(MIN_WORD_COUNT + '단어 이상 입력해주세요');
            return;
        }

        if (recordComment.value.length > MAX_COMMENT_LENGTH) {
            alert(MAX_COMMENT_LENGTH + '자를 초과할 수 없습니다.');
            return;
        }

        // 친구 검사 로직
        if (friendSearch.value !== '') { // 받는 사람을 적은 경우 검사
            let freindValidation = false;
            friendListRaw.value.forEach((item) => {
                if (item.memberId === friendSearch.value) freindValidation = true;
            })

            if (!freindValidation) {
                alert('받는 사람 아이디를 잘못 입력했습니다. \n보내지 않으려면 공란으로 남겨주세요.');
                return;
            }
        }

        isLoading.value = true;
        const formData = new FormData();
        formData.append('recordComment', recordComment.value);
        formData.append('memberId', memberId.value);
        formData.append('recordIsOpen', recordIsOpen.value);

        if (friendSearch.value !== '') {
            formData.append('recordShareTo', friendSearch.value);
        }

        const mediaFileBlob = recordImage.value ? recordImage.value
            : recordVideo.value ? recordVideo.value
            : recordAudio.value;
        formData.append('mediaFile', mediaFileBlob);
        
        formData.append('recordLocationX', recordLocationX.value);
        formData.append('recordLocationY', recordLocationY.value);

        store.dispatch('record/addRecord', formData)
        .then((response) => {
            isLoading.value = false;
            console.log('성공');
            console.log(response);

            setTodayDate();
            router.push({ name: 'recordMain' });
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('실패');
            console.error(error);
        })
    }

    function openLocationRecoder() {
        captureLocationVisible.value = true;
    }

    function handleLocationCapture(coord) {
        const { y, x } = coord;
        recordLocationY.value = y;
        recordLocationX.value = x;
        console.log(recordLocationY.value, recordLocationX.value);
        captureLocationVisible.value = false;
    }

    function showFriendSearch() {
        friendSearchVisible.value = true;
        store.dispatch('member/fetchFriendList')
        .catch((error) => {
            alert('회원 목록을 가져오는 중 오류가 발생했습니다.');
            console.error(error);
        })
    }

    function handleSelectFriend(e) {
        friendSearch.value = e.id;
        friendSearchVisible.value = false;
    }
</script>

<template>
    <v-overlay
        v-model="isLoading"
        scroll-strategy="block"
        persistent
        class="loading-overlay"
    >
        <v-progress-circular
        color="primary"
        indeterminate
        size="64"
        ></v-progress-circular>
    </v-overlay>

    <v-container :class="captureAudioVisible
        || captureImageVisible
        || captureVideoVisible
        || captureLocationVisible ? 'no-scroll' : ''">
        <h1>새로운 조각</h1>

        <div class="mt-10">
            <div class="toolbar">
                <input type="file" id="input-media-from-gallery" accept="image/*" @change="onInputChange">
                <input type="file" id="input-image-from-camera" accept="image/*" capture="environment" @change="onInputChange">
                <v-btn-group
                    divided
                    variant="outlined"
                >
                    <v-btn icon="mdi-image-outline" @click="openInputGallery"></v-btn>
                    <v-btn icon="mdi-camera-outline" @click="openInputImage"></v-btn>
                    <v-btn icon="mdi-video-outline" @click="openVideoCamera"></v-btn>
                    <v-btn icon="mdi-microphone-outline" @click="openAudioRecoder"></v-btn>
                    <v-btn icon="mdi-map-marker-outline" @click="openLocationRecoder"></v-btn>
                </v-btn-group>
            </div>

            <div class="preview-map my-8" v-if="recordLocationX !== 0 && recordLocationY !== 0">
                <record-map class="map-container"
                    :record-location-x="recordLocationX"
                    :record-location-y="recordLocationY"/>
            </div>

            <div class="preview my-8">
                <img v-if="recordImageUrl !== ''" :src="recordImageUrl" class="media-preview">
                <video v-if="recordVideoUrl !== ''" :src="recordVideoUrl" class="media-preview" autoplay playsinline controls />
                <audio :src="recordAudioUrl" controls v-if="recordAudioUrl != ''"></audio>
            </div>

            <div>
                <v-textarea
                v-model="recordComment"
                label="조각 내용"
                variant="outlined" />
                <div class="comment-length">{{ commentLength }} / {{ MAX_COMMENT_LENGTH }}</div>

                <div class="mt-16">
                    <v-text-field
                    v-model="friendSearch"
                    @click="showFriendSearch"
                    label="받는사람"
                    variant="outlined" />
                </div>

                <v-card v-if="friendSearchVisible" class="mb-8">
                    <v-list @click:select="handleSelectFriend" :items="friendsSearchResultList"></v-list>
                </v-card>
            </div>
            
            <v-row>
                <v-col>
                    <v-switch
                        v-model="recordIsOpen"
                        :label="recordIsOpen ? '공개' : '비공개'"
                        color="primary"
                        hide-details
                    ></v-switch>
                </v-col>
            </v-row>

            <div class="toolbar">
                <v-btn icon="mdi-arrow-up-thin-circle-outline" color="primary" @click="uploadRecord"></v-btn>
            </div>
        </div>

        <capture-image v-if="captureImageVisible" @captured="handleImageCapture" @abort="() => {captureImageVisible = false}" />
        <capture-video v-if="captureVideoVisible" @captured="handleVideoCapture" @abort="() => {captureVideoVisible = false}" />
        <capture-audio v-if="captureAudioVisible" @captured="handleAudioCapture" @abort="() => {captureAudioVisible = false}" />
        <capture-location v-if="captureLocationVisible" @captured="handleLocationCapture" @abort="() => {captureLocationVisible = false}" />
    </v-container>
</template>

<style scoped>
.no-scroll {
    overflow: hidden;
    height: 80vh;
}

h1 {
  text-align: center;  
  font-size: 24px;
}

.toolbar {
    text-align: center;
    margin-bottom: 15px;
}

video {
    height: 600px;
}

.comment-length {
    font-size: 85%;
}

.preview {
    text-align: center;
}

.preview-map {
    display: flex;
    justify-content: center;
    width: 100%;
}

.media-preview {
    width: 70%;
}

.map-container {
    z-index: 0;
    height: 400px;
    width: 400px;
}

input {
    display: none;
}

.loading-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 768px) { 
    .media-preview {
        width: 100%;
    }
} 
</style>