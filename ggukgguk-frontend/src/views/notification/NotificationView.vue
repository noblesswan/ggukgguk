<script setup>
import GoBackHeader from '@/components/common/GoBackHeader.vue';
import NotificationList from '../../components/notification/NotificationList.vue';
import RecordBottom from '@/components/record/RecordBottom.vue';

</script>

<template>
    <go-back-header />
    <v-main>
        <v-container>
            <notification-list/>
        </v-container>
    </v-main>
    <record-bottom />
</template>