<script setup>
import { computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';

const router = useRouter();
const route = useRoute();
const store = useStore();

const MENUS = {
    home: 'recordMain',
    bell: 'notificationView',
    record: 'recordAdd',
    people: 'MyFriend',
    cog: 'settingMain'
};

// 읽지 않은 알림 수 조회
const unNotifyList = computed(() => {
  return store.getters['notification/unReadNotify'];
})


function setFriendId(friendId) {
    store.commit('record/setFriendId', friendId);
}

function goTo(from) {
    const name = MENUS[from];
    setFriendId(null);

    if (name === 'recordMain' && route.name === 'recordMain') {
        window.location.reload();
    }
    router.push({ name });
}

// 읽지 않은 알림 수 
function getunreadNotify() {
  store.dispatch("notification/getUnReadNotify")
    .then((response) => {
      console.log("읽지 않은 알림의 수 : " + response.data.data);
      console.log(response)
    }).catch(() => {
    });
}


onMounted(() => {
    getunreadNotify();
})


</script>

<template>
    <v-bottom-navigation>
        <v-btn value="home" @click="goTo('home')">
            <v-icon>mdi-home</v-icon>
        </v-btn>

        <v-btn value="bell" @click="goTo('bell')">
            <v-icon>mdi-bell</v-icon>
            <div v-if="unNotifyList > 0" class="red-dot"></div>
        </v-btn>

        <v-btn value="record" class="record-btn" color="red" @click="goTo('record')">
            <v-icon>mdi-circle</v-icon>
        </v-btn>

        <v-btn value="people" @click="goTo('people')">
            <v-icon>mdi-account-multiple</v-icon>
        </v-btn>

        <v-btn value="cog" @click="goTo('cog')">
            <v-icon>mdi-cog</v-icon>
        </v-btn>
    </v-bottom-navigation>
</template>

<style scoped>
.record-btn {
    font-size: xx-large !important;
    color: #3AA73A;
}

.v-bottom-navigation .v-bottom-navigation__content > .v-btn {
    min-width: 70px;
}
.red-dot {
  position: absolute;
  top: 5px;
  right: 10px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #3AA73A;
}
</style>