<script setup>
import DiaryMain from '../../components/record/DiaryMain.vue';
import RecordHeader from '../../components/record/RecordHeader.vue';
import RecordBottom from '../../components/record/RecordBottom.vue';

</script>

<template>
    <record-header />
    <v-main>
        <v-container>
            <diary-main />
        </v-container>
    </v-main>
    <record-bottom />
</template>