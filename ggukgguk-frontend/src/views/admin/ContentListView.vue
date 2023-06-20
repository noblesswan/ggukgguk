<script setup>
import ContentListContainer from '@/components/admin/ContentListContainer.vue';
import AdminMainLayout from './AdminMainLayout.vue';
</script>

<template>
    <admin-main-layout>
        <div class="page-title">
            <h1 class="mb-8">컨텐츠 관리</h1>
        </div>

        <content-list-container />
    </admin-main-layout>
</template>

<style>
.page-title {
    width: 100%;
    text-align: right
}
</style>