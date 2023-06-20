<script setup>
import RecordHeader from '../../components/record/RecordHeader.vue'
import RecordUnaccepted from '../../components/record/RecordUnaccepted.vue';
import RecordBottom from '@/components/record/RecordBottom.vue';

</script>

<template>
    <record-header />
    <v-main>
        <v-container>
            <record-unaccepted />
        </v-container>
    </v-main>
    <record-bottom />
</template>