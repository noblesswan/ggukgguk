<script setup>
import CalendarMain from '../../components/record/CalendarMain.vue';
import RecordHeader from '../../components/record/RecordHeader.vue';
import RecordBottom from '../../components/record/RecordBottom.vue';

</script>

<template>
    <record-header />
    <v-main>
        <v-container>
            <calendar-main />
        </v-container>
    </v-main>
    <record-bottom />
</template>