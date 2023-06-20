<script setup>
import { onMounted, defineEmits, defineProps } from 'vue';

const emit = defineEmits(['mounted']);
const props = defineProps(['date', 'color']);

onMounted(() => {
    emit('mounted');
});

</script>

<template>
    <div :style="color ? `color: ${color}` : 'color: black'">
        {{ props.date }}
        <hr :style="color ? `background-color: ${color}` : 'background-color: black'" />
    </div>
</template>

<style scoped>
hr {
    height:1px;
    border:0;
}
</style>