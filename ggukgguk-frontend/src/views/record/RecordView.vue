<script setup>
import RecordHeader from '../../components/record/RecordHeader.vue'
import RecordMain from '../../components/record/RecordMain.vue';
import RecordBottom from '@/components/record/RecordBottom.vue';

</script>

<template>
    <record-header />
    <v-main>
        <record-main/>
    </v-main>
    <record-bottom />
</template>