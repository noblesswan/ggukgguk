<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useStore } from 'vuex';

const store = useStore();

const isLoading = ref(false);

const window = ref([0, 0]);

const detailDiagVisible = ref(false);
const detailDaigContent = ref({});

const optionFromStore = computed(() => {
    return store.getters['admin/batchOption'];
})

const optionSelected = ref(optionFromStore);

watch(
    () => optionSelected.value.page,
    () => {
        fetchDetail();
    }
);

watch(
    () => optionSelected.value.jobName,
    () => {
        optionSelected.value.page = 1;
        fetchDetail();
    }
);

const recentBatch = computed(() => {
    return store.getters['admin/recentBatch'];
});

const batchDetailTotal = computed(() => {
    return store.getters['admin/batchDetail'].total;
});

const batchDetailTotalPage = computed(() => {
    const computed = Math.ceil(batchDetailTotal.value / 10);
    if (!computed) { // NaN인 경우 페이지네이션 컴포넌트의 Validation 로직에 걸리게 되므로 한 번 걸러준다
        return 1;
    }
    return computed;
});

const batchDetailList = computed(() => {
    return store.getters['admin/batchDetail'].list;
});

function fetchRecent() {
    isLoading.value = true;
    store.dispatch('admin/getRecentBatch')
        .then(() => {
            isLoading.value = false;
            console.log('최근 로그 불러오기 성공');
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('최근 로그 불러오기 실패');
            console.error(error);
        });
}

function fetchDetail() {
    isLoading.value = true;
    store.dispatch('admin/getBatchDetail')
        .then(() => {
            isLoading.value = false;
            console.log('상세 로그 불러오기 성공');
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('상세 로그 불러오기 실패');
            console.error(error);
        });
}

const JOBS = [{
        label: '키워드 추출',
        value: 'extractKeywordJob'
    },
    {
        label: '유해 콘텐츠 모니터링',
        value: 'checkModContentJob'
}];
const selectedJobName = ref(optionSelected.value.jobName);
watch(selectedJobName, () => {
    store.commit('admin/updateBatchOption', { jobName: selectedJobName.value });
});

function showDetailDiag(executionInfoRow) {
    detailDaigContent.value = executionInfoRow;
    detailDiagVisible.value = true;
}


onMounted(() => {
    fetchRecent();
    fetchDetail();
});
</script>

<template>
    <v-overlay class="loading-overlay" v-model="isLoading" scroll-strategy="block" persistent>
        <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
    </v-overlay>

    <v-dialog
      v-model="detailDiagVisible"
      width="auto"
    >
      <v-card>
        <v-card-title>
            실행 내역 상세
        </v-card-title>
        <v-card-text>
          <ul>
              <li>실행 ID: {{ detailDaigContent.jobExecutionId }}</li>
              <li>버전: {{ detailDaigContent.version }}</li>
              <li>인스턴스 ID: {{ detailDaigContent.jobInstanceId }}</li>
              <li>시작 시각: {{ detailDaigContent.startTime }}</li>
              <li>종료 시각: {{ detailDaigContent.endTime }}</li>
              <li>상태: {{ detailDaigContent.status }}</li>
              <li>종료 코드: {{ detailDaigContent.exitCode }}</li>
              <li>종료 메시지: {{ detailDaigContent.exitMessage }}</li>
              <li>생성 시각: {{ detailDaigContent.createTime }}</li>
              <li>최종 수정 시각: {{ detailDaigContent.lastUpdated }}</li>
        </ul>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" block @click="detailDiagVisible = false">닫기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-col>
        <v-row>
            <v-col>
                <h2 class="section-title">키워드 추출</h2>
                <v-window v-model="window[0]" show-arrows>
                    <v-window-item v-for="execution in recentBatch['extractKeywordJob']" :key="execution.jobExecutionId">
                        <v-card
                            height="200px"
                            variant="outlined"
                            class="d-flex flex-column align-center justify-center"
                            @click="showDetailDiag(execution)"
                        >
                            <div class="d-flex justify-center align-center">
                                <span class="text-h2" :class="execution.status === 'COMPLETED' ?
                                    'text-blue-lighten-1'
                                    : 'text-red-lighten-1'">{{ execution.status }}</span>
                            </div>
                            <ul class="mt-2">
                                <li>인스턴스 ID &nbsp; {{ execution.jobInstanceId }}</li>
                                <li>실행 ID &nbsp; {{ execution.jobExecutionId }}</li>
                                <li>시작 &nbsp; {{ execution.createTime }}</li>
                                <li>종료 &nbsp; {{ execution.endTime }}</li>
                            </ul>
                        </v-card>
                    </v-window-item>
                </v-window>
            </v-col>
            <v-col>
                <h2 class="section-title">유해 컨텐츠 모니터링</h2>
                <v-window v-model="window[1]" show-arrows>
                    <v-window-item v-for="execution in recentBatch['checkModContentJob']" :key="execution.jobExecutionId">
                        <v-card
                            height="200px"
                            variant="outlined"
                            class="d-flex flex-column align-center justify-center"
                            @click="showDetailDiag(execution)"
                        >
                            <div class="d-flex justify-center align-center">
                                <span class="text-h2" :class="execution.status === 'COMPLETED' ?
                                    'text-blue-lighten-1'
                                    : 'text-red-lighten-1'">{{ execution.status }}</span>
                            </div>
                            <ul class="mt-2">
                                <li>인스턴스 ID &nbsp; {{ execution.jobInstanceId }}</li>
                                <li>실행 ID &nbsp; {{ execution.jobExecutionId }}</li>
                                <li>시작 &nbsp; {{ execution.createTime }}</li>
                                <li>종료 &nbsp; {{ execution.endTime }}</li>
                            </ul>
                        </v-card>
                    </v-window-item>
                </v-window>
            </v-col>
        </v-row>
    </v-col>

    <v-col class="mt-8">
        <v-row>
            <h2 class="section-title">상세 조회</h2>
        </v-row>
        <v-row>
            <v-select v-model="selectedJobName"
                :items="JOBS"
                item-title="label"
                item-value="value"
                label="배치 작업 선택"></v-select>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center">
            <v-table>
                <thead>
                    <tr>
                        <th class="text-left">
                            실행 ID
                        </th>
                        <th class="text-left">
                            버전
                        </th>
                        <th class="text-left">
                            인스턴스 ID
                        </th>
                        <th class="text-left">
                            시작 시각
                        </th>
                        <th class="text-left">
                            종료 시각
                        </th>
                        <th class="text-left">
                            상태
                        </th>
                        <th class="text-left">
                            종료 코드
                        </th>
                        <th class="text-left">
                            세부 정보
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in batchDetailList" :key="item.jobExecutionId">
                        <td>{{ item.jobExecutionId }}</td>
                        <td>{{ item.version }}</td>
                        <td>{{ item.jobInstanceId }}</td>
                        <td>{{ item.startTime }}</td>
                        <td>{{ item.endTime }}</td>
                        <td>{{ item.status }}</td>
                        <td>{{ item.exitCode }}</td>
                        <td>
                            <v-btn @click="showDetailDiag(item)">보기</v-btn>
                        </td>
                    </tr>
                </tbody>
            </v-table>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center">
            <v-pagination v-model="optionSelected.page" :total-visible="6" :length="batchDetailTotalPage"></v-pagination>
        </v-row>
    </v-col>
</template>

<style scoped>
.loading-overlay {
    display: flex;
    justify-content: center;
    align-items: center;
}

.section-title {
    font-size: 24px;
    font-weight: 800;
    margin-bottom: 10px;
}
</style>