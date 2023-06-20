<script setup>
import AdminMemberContainer from '@/components/admin/AdminMemberContainer.vue';
import AdminMainLayout from './AdminMainLayout.vue';
</script>

<template>
    <admin-main-layout>
        <div class="page-title">
            <h1 class="mb-8">회원 관리</h1>
        </div>

        <admin-member-container />
    </admin-main-layout>
</template>

<style>
.page-title {
    width: 100%;
    text-align: right
}
</style>