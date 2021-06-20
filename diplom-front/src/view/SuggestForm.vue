<template>
    <v-card>
        <v-card-title>Предложение списка</v-card-title>
        <v-card-text style="text-align: center">
            <v-btn @click="query">Применить</v-btn>
        </v-card-text>
    </v-card>
</template>

<script>
import { $http } from '@/service/http';

export default {
    name: 'SuggestForm',
    data() {
        return {
            active: true
        };
    },
    methods: {
        query() {
            $http.post('/application/suggest').then((response) => {
                this.$bus.$emit('accepted', response.data);
                this.$emit('submit');
            });
        }
    }
};
</script>

<style scoped>
</style>