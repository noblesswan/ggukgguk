<script setup>
import GoBackHeader from '@/components/common/GoBackHeader.vue';
import EditMemberInfo from '../../components/member/EditMemberInfo.vue';
import RecordBottom from '@/components/record/RecordBottom.vue';
</script>

<template>
    <go-back-header />
    <v-main>
        <v-container>
            <edit-member-info />
        </v-container>
    </v-main>
    <record-bottom />
</template>