<script setup>
import { onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import AdminHeader from '@/components/admin/AdminHeader.vue'

const store = useStore();
const router = useRouter();

const memberInfo = computed(() => {
    return store.getters['auth/memberInfo'];
});

onMounted(() => {
    if(memberInfo.value?.memberAuthority === 'NORMAL')
        router.push({ name: 'login'});
});
</script>

<template>
    <v-layout>
        <admin-header />

        <v-main>
            <v-container>
                <slot></slot>
            </v-container>
        </v-main>
    </v-layout>
</template>