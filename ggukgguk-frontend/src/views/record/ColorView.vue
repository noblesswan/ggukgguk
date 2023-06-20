<script setup>
import ColorMain from '../../components/record/ColorMain.vue';
import RecordHeader from '../../components/record/RecordHeader.vue';
import RecordBottom from '../../components/record/RecordBottom.vue';

</script>

<template>
    <record-header />
    <v-main>
        <v-container>
            <color-main />
        </v-container>
    </v-main>
    <record-bottom />
</template>