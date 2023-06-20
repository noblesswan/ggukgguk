<script setup>
    import { computed, ref, onMounted } from 'vue';
    import { useStore } from 'vuex';
    import { useRouter } from 'vue-router';
    import RecordMap from './RecordMap.vue';

    const store = useStore();
    const BASE_URI = window.baseURI;
    const MAX_COMMENT_LENGTH = 512;
    const MIN_WORD_COUNT = 3;
    const isLoading = ref(false);

    const router = useRouter();

    const record = computed(() => {
        return store.getters['record/record'];
    })

    const recordComment = ref('');
    const recordIsOpen = ref(true);

    onMounted(() => {
        console.log(record.value);
        recordComment.value = record.value.recordComment;
        recordIsOpen.value = record.value.recordIsOpen;
    })

    const commentLength = computed(() => {
        return recordComment.value.length;
    });

    function formatDate(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    }

    function setTodayDate() {
        store.commit('record/setStartDateStr', formatDate(new Date()));
    }

    function updateRecord() {
        if (recordComment.value.split(' ').length < MIN_WORD_COUNT) {
            alert(MIN_WORD_COUNT + '단어 이상 입력해주세요');
            return;
        }

        if (recordComment.value.length > MAX_COMMENT_LENGTH) {
            alert(MAX_COMMENT_LENGTH + '자를 초과할 수 없습니다.');
            return;
        }

        isLoading.value = true;

        store.dispatch('record/updateRecord', { recordId: record.value.recordId, recordComment: recordComment.value, memberId: record.value.memberId })
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
</script>

<template>
    <v-overlay
        v-model="isLoading"
        scroll-strategy="block"
        persistent
    >
        <v-progress-circular
        color="primary"
        indeterminate
        size="64"
        ></v-progress-circular>
    </v-overlay>

    <v-container>
        <h1>조각 수정</h1>
            <div class="mt-10">
            <div class="media-wrapper">
                <div class="preview-map my-8" v-if="record.recordLocationX !== 0 && record.recordLocationY !== 0">
                    <record-map class="map-container"
                        :record-location-x="record.recordLocationX"
                        :record-location-y="record.recordLocationY"/>
                </div>
                <div class="preview my-8">
                    <video v-if="record.mediaTypeId==='video'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" autoplay playsinline controls class="media"></video>
                    <img v-if="record.mediaTypeId==='image'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media">
                    <audio v-if="record.mediaTypeId==='audio'" controls :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media"></audio>
                </div>
            </div>
            <div>
                <v-textarea
                v-model="recordComment"
                label="조각 내용"
                variant="outlined" />
                <div class="comment-length">{{ commentLength }} / {{ MAX_COMMENT_LENGTH }}</div>
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
                <v-btn icon="mdi-arrow-up-thin-circle-outline" color="primary" @click="updateRecord"></v-btn>
            </div>
        </div>
    </v-container>
</template>

<style scoped>

h1 {
  text-align: center;  
  font-size: 24px;
}

.toolbar {
    text-align: center;
    margin-bottom: 15px;
}

.media-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: auto;
}

.media {
  margin: 2px;
  width: 100%;
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

.v-overlay {
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