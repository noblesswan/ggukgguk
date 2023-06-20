<script setup>
import AdminMainLayout from './AdminMainLayout.vue';
import AdminMainContainer from '@/components/admin/AdminMainContainer.vue';
</script>

<template>
    <admin-main-layout>
        <div class="page-title">
            <h1 class="mb-8">관리자 메인</h1>
        </div>

        <admin-main-container />
    </admin-main-layout>
</template>

<style>
.page-title {
    width: 100%;
    text-align: right
}
</style>