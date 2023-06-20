<script setup>
import { ref, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'

const store = useStore();

const isLoading = ref(false);

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const chartOptions = ref({
    responsive: true,
    yAxes: [
        {
            ticks: {
                stepSize: 1,
                beginAtZero: true
            }
        }
    ]
});

/**
 * 서버에서 가져온 값으로부터
 * 데이터가 존재하지 않는 일자의 값을
 * 0으로 만들어 반환한다.
 */
const combinedData = computed(() => {
    const rawData = store.getters['admin/dailyReport'];

    const result = {}; // 처리 한 값을 담을 배열

    Object.keys(rawData).forEach((key) => { // 서버에서 가져온 값을 키값을 기준으로 순회
        const zeroCheckTable = {}; // 데이터가 존재하지 않는 일자 확인용
        [...Array(8).keys()].forEach((item) => { // [0, 1, ..., 7]
            const dateCur = new Date();
            dateCur.setDate(dateCur.getDate() - item);
            zeroCheckTable[dateToStr(dateCur)] = false;
            /* 아래와 같은 테이블로 초기화된다.
                {
                    '2023-05-14': false,
                    '2023-05-13': false,
                        ...
                    '2023-05-07': false
                }
            */
        });

        const targetArr = [...rawData[key]]; // 현재 순서에서 확인할 배열

        targetArr.forEach((item) => {
            zeroCheckTable[item.date] = true; // 일자가 존재하면 위에서 만든 테이블에 기록
        })

        Object.keys(zeroCheckTable).forEach((tableKey) => {
            if (!zeroCheckTable[tableKey]) { // 데이터가 없는 일자인 경우 '0' 데이터 삽입
                targetArr.push({
                    date: tableKey,
                    count: 0
                })
            }
        })

        targetArr.sort((a, b) => { // 일자를 기준으로 정렬
            if (new Date(a.date) > new Date(b.date)) return 1;
            if (new Date(a.date) < new Date(b.date)) return -1;
            return 0;
        });

        result[key] = targetArr; // 순회한 배열을 결과 오브젝트에 삽입
    });

    return result;
})

const memberChartData = computed(() => {
    const newMember = combinedData.value.newMember;

    return {
        labels: newMember?.map((item) => {
            return item.date;
        }),
        datasets: [{
            label: '일자별 회원가입수',
            data: newMember?.map((item) => {
                return item.count;
            }),
            backgroundColor: '#3AA73A'
        }]
    }
})

const recordChartData = computed(() => {
    const newRecord = combinedData.value.newRecord;

    return {
        labels: newRecord?.map((item) => {
            return item.date;
        }),
        datasets: [{
            label: '일자별 조각 등록수',
            data: newRecord?.map((item) => {
                return item.count;
            }),
            backgroundColor: '#3AA73A'
        }]
    }
})

const replyChartData = computed(() => {
    const newReply = combinedData.value.newReply;

    return {
        labels: newReply?.map((item) => {
            return item.date;
        }),
        datasets: [{
            label: '일자별 댓글 등록수',
            data: newReply?.map((item) => {
                return item.count;
            }),
            backgroundColor: '#3AA73A'
        }]
    }
})

onMounted(() => {
    requestAnalysisData();
    requestDailyReport();
})

const analysisData = computed(() => {
    return store.getters['admin/analysisData'];
})

function requestDailyReport() {
    store.dispatch('admin/getDailyReportAll')
        .then(() => {
            console.log('일자별 추이 데이터 가져오기 성공');
        })
        .catch((error) => {
            console.error('일자별 추이 데이터 가져오기 성공');
            console.error(error);
        });
}

function requestAnalysisData() {
    isLoading.value = true;

    store.dispatch('admin/getAnalysisData')
        .then(() => {
            console.log('성공');
            isLoading.value = false;
        })
        .catch((error) => {
            console.error("관리자 페이지 불러오기 실패");
            console.error(error);
            isLoading.value = false;
        })
}

const dateToStr = (date) => {
    const yy = date.getFullYear();
    const MM = (date.getMonth() + 1).toString().padStart(2, '0');
    const dd = date.getDate().toString().padStart(2, '0');

    return `${yy}-${MM}-${dd}`;
}
</script>

<template>
    <v-overlay v-model="isLoading" scroll-strategy="block" persistent>
        <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
    </v-overlay>

    <v-row>
        <v-col cols="6">
            <v-card title="주요 지표" variant="outlined">
                <v-card-text>
                    <ul class="mx-8">
                        <li>전체 회원 수: {{ analysisData.totalMember }}</li>
                        <li>오늘 가입 수: {{ analysisData.todayMember }}</li>
                        <li>전체 조각 수: {{ analysisData.totalContent }}</li>
                        <li>오늘 조각 수: {{ analysisData.todayContent }}</li>
                    </ul>
                </v-card-text>
            </v-card>
        </v-col>
        <v-col cols="6">
            <v-card title="일자별 회원가입 수" variant="outlined">
                <Line id="member-chart" :options="chartOptions" :data="memberChartData" />
            </v-card>
        </v-col>
    </v-row>

    <v-row>
        <v-col cols="6">
            <v-card title="일자별 조각 등록 수" variant="outlined">
                <Line id="record-chart" :options="chartOptions" :data="recordChartData" />
            </v-card>
        </v-col>

        <v-col cols="6">
            <v-card title="일자별 댓글 등록 수" variant="outlined">
                <Line id="reply-chart" :options="chartOptions" :data="replyChartData" />
            </v-card>
        </v-col>
    </v-row>
</template>

<style scoped>
.v-overlay {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>