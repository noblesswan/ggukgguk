<script setup>
import NoticeListContainer from '@/components/admin/NoticeListContainer.vue';
import AdminMainLayout from './AdminMainLayout.vue';
</script>

<template>
    <admin-main-layout>
        <div class="page-title">
            <h1 class="mb-8">공지사항</h1>
        </div>

        <notice-list-container />
    </admin-main-layout>
</template>

<style>
.page-title {
    width: 100%;
    text-align: right
}
</style>
