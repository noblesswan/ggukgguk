<script setup>
import { onMounted, defineProps, ref, watch } from 'vue';

let map = '';
let marker = null;
const mapContainer = ref(null);

const props = defineProps({
  recordLocationX: Number,
  recordLocationY: Number
})

watch(props, () => {
  updateCoord();
})

function initMap() {
  // let container = document.getElementById("map");
  console.log(props.recordLocationX);
  console.log(props.recordLocationY);
  let container = mapContainer.value;
  let options = {
    // eslint-disable-next-line
    center: new kakao.maps.LatLng(props.recordLocationY, props.recordLocationX),
    level: 3,
    draggable: false // 지도를 움직이지 못하게 함
  };
  // eslint-disable-next-line
  map = new kakao.maps.Map(container, options);
}

// 선택된 장소를 지도에 표시
function showOnMap() {
  // eslint-disable-next-line
  const latLng = new kakao.maps.LatLng(props.recordLocationY, props.recordLocationX);
  map.setCenter(latLng);
  // eslint-disable-next-line
  marker = new kakao.maps.Marker({
    position: latLng,
  });
  marker.setMap(map);
}

function updateCoord() {
  console.log('위치 업데이트');
  console.log('> 좌표:',props.recordLocationY, props.recordLocationX);
  // eslint-disable-next-line
  // const latLng = new kakao.maps.LatLng(props.recordLocationY, props.recordLocationX);
  // marker.setPosition(latLng);
  // map.setCenter(latLng);
  initMap();
  showOnMap();
}

onMounted(() => {
  initMap();
  showOnMap();
});

</script>

<template>
  <div ref="mapContainer" class="map"></div>
</template>

<style scoped>
.map {
  width: 100%;
  height: 100%;
}
</style>