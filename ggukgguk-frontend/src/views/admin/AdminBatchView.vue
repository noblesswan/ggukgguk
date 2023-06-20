<script setup>
import AdminMainLayout from './AdminMainLayout.vue';
import BatchStatusContainer from '@/components/admin/BatchStatusContainer.vue';
</script>

<template>
    <admin-main-layout>
        <div class="page-title">
            <h1 class="mb-8">배치 작업</h1>
        </div>

        <batch-status-container />
    </admin-main-layout>
</template>

<style>
.page-title {
    width: 100%;
    text-align: right
}
</style>