<script setup>
import { onMounted, ref, defineEmits } from 'vue';
import { external } from '@/api';

const map = ref(null);
const marker = ref(null);
const mapContainer = ref(null);
const query = ref('');
const resultList = ref([]);
const coord = ref(null);

const emit = defineEmits(['captured', 'abort']);

function initMap() {
  // eslint-disable-next-line
  const latLng = new kakao.maps.LatLng(37.506, 127.041);
  let options = {
    // eslint-disable-next-line
    center: latLng,
    level: 3,
  };
  // eslint-disable-next-line
  map.value = new kakao.maps.Map(mapContainer.value, options);
  map.value.setCenter(latLng);

  // eslint-disable-next-line
  marker.value = new kakao.maps.Marker({
    position: latLng,
  });
  marker.value.setMap(map.value);

  // eslint-disable-next-line
  kakao.maps.event.addListener(map.value, 'click', function(mouseEvent) {        
    
    // 클릭한 위도, 경도 정보를 가져옵니다 
    // eslint-disable-next-line
    const latlng = mouseEvent.latLng;
    console.log(mouseEvent);
    // 마커 위치를 클릭한 위치로 옮깁니다
    marker.value.setPosition(latlng);

    console.log('마커 위치 업데이트');

    coord.value = {
      x: latlng.La,
      y: latlng.Ma
    }
  });
}

function searchLocal() {
  external.kakaoLocalSearch(query.value)
  .then((response) => {
    console.log(response);
    resultList.value = response.data.documents
  })
  .catch((error) => {
    console.error(error);
  });
}

function selectCurrentLocation() {
  const success = (position) => {
    const result = {
      y: position.coords.latitude,
      x: position.coords.longitude
    }
    selectResult(result);
  }

  const error = () => {
    alert('현재 위치를 가져오는데 실패했습니다.');
  }
  navigator.geolocation.getCurrentPosition(success, error);
}

function selectResult(result) {
  // eslint-disable-next-line
  const latLng = new kakao.maps.LatLng(result.y, result.x);

  // eslint-disable-next-line
  marker.value.setPosition(latLng)
  marker.value.setMap(map.value);
  map.value.setCenter(latLng);

  coord.value = {
    x: result.x,
    y: result.y
  }

  resultList.value = [];
}

onMounted(() => {
  initMap();
  selectCurrentLocation();
});

function selectCoord() {
  emit('captured', coord.value);
}
</script>

<template>
    <div class="wrapper">
      <div class="inner">
        <div class="map-control">
          <div class="map-toolbar">
            <div class="map-toolbar-left">
              <input type="text" class="map-search" placeholder=" 키워드 입력 후 엔터" v-model="query" @keyup.enter="searchLocal">
              <v-btn prepend-icon="mdi-check" @click="selectCoord">
                선택
              </v-btn>
            </div>
            <div class="map-toolbar-right">
              <v-btn icon="mdi-crosshairs-gps" @click="selectCurrentLocation"></v-btn>
              <v-btn icon="mdi-window-close" @click="() => {emit('abort')}"></v-btn>
            </div>
          </div>
          <div class="result mt-4" v-if="resultList.length > 0">
            <v-list>
              <v-list-item v-for="result in resultList" :key="result.id" @click="selectResult(result)">
                <v-list-item-title>{{ result.place_name }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </div>
        </div>
        <div class="map-container" ref="mapContainer">
        </div>
      </div>
    </div>
</template>

<style scoped>
    .inner {
      width: 100%;
      height: 100%;
      text-align: center;
    }

    .map-container {
      height: 100%;
      width: 100%;
    }

    .map-control {
      position: absolute;
      padding: 20px;
      top: 10;
      left: 10;
      z-index: 100;
      width: 100%;
    }
    
    .map-toolbar {
      display: flex;
      justify-content: space-between;
    }

    .map-toolbar-left {
      display: flex;
      align-items: center;
      justify-content: space-around;
    }

    .map-search {
      background-color: #FFFFFF;
      border: 1px black solid;
      font-size: x-large;
      width: 200px;
    }

    .result {
      width: 385px;
    }

    .wrapper {
        background-color: #FFFFFF;
        position: absolute;
        top: 0;
        left: 0;
        height: 100vh;
        width: 100vw;
        text-align: center;
        display: flex;
        align-items: center;
        z-index: 1100;
    }
</style>