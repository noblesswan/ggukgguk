<script setup>
import GoBackHeader from '@/components/common/GoBackHeader.vue';
import FindFriend from '../../components/member/FindFriend.vue';
import RecordBottom from '@/components/record/RecordBottom.vue';
</script>
<template>
    <go-back-header />
    <v-container fluid ma-0 pa-0 fill-height> </v-container>
    <v-container fluid ma-0 pa-0 fill-height> </v-container>    
    <v-container style="max-height: 300px; overflow-y: auto;">
    <find-friend/>
    </v-container>
    <record-bottom />
</template>