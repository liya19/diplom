<template>
    <div :key="componentKey">
        <v-data-table
            data-app
            :headers="header"
            :footer-props="footer"
            :items="applications">
            <template #item.group="{ item }">
                <div v-if="!item.editMode"> {{ item.group }}</div>
                <v-text-field v-else v-model="item.group"/>
            </template>
            <template #item.firstName="{ item }">
                <div> {{ item.firstName }}</div>
            </template>
            <template #item.lastName="{ item }">
                <div> {{ item.lastName }}</div>
            </template>
            <template #item.middleName="{ item }">
                <div> {{ item.middleName }}</div>
            </template>
            <template #item.categories="{ item }">
                <div>
                    <div v-for="category of item.categories" :key="category.id">
                        {{ category.name }}
                    </div>
                </div>
            </template>
            <template #item.kind="{ item }">
                <div> {{ item.documentKind.name }}</div>
            </template>
            <template #item.date="{ item }">
                <div v-if="!item.editMode"> {{ item.date.toLocaleDateString('ru-RU') }}</div>
                <div v-else class="editable-field">
                    <v-text-field type="date" v-model="item.date"/>
                </div>
            </template>
        </v-data-table>
    </div>
</template>

<script>
import { $http } from '@/service/http';

const headers = [
    {
        text: 'Группа',
        value: 'group',
        align: 'center',
        width: '100px'
    },
    {
        text: 'Имя',
        value: 'firstName',
        align: 'center',
        width: '200px'
    }, {
        text: 'Фамилия',
        value: 'lastName',
        align: 'center',
        width: '200px'
    }, {
        text: 'Отчество',
        value: 'middleName',
        align: 'center',
        width: '200px'
    }, {
        text: 'Категории',
        value: 'categories',
        align: 'center',
        width: '200px'
    }, {
        text: 'Тип',
        value: 'kind',
        align: 'center',
        width: '200px'
    }, {
        text: 'Дата',
        value: 'date',
        align: 'center',
        width: '200px'
    }
];
const FOOTER_PROPS = {
    itemsPerPageOptions: [10, 25, 50],
    showFirstLastPage: true,
    showCurrentPage: true
};
export default {
    name: 'IssuedApplications',
    data() {
        return {
            applications: [],
            header: headers,
            footer: FOOTER_PROPS,
            componentKey: 1,
            categories: [],
            documentKinds: []
        };
    },
    mounted() {
        this.$bus.$on('issued', (applications) => {
            this.applications.forEach(a => {
                a.editMode = false;
                a.date = new Date(a.date);
            });
            this.applications = applications;
        });
        $http.get('applications/issued').then((response) => {
            this.applications = response.data;
            this.applications.forEach(a => {
                a.editMode = false;
                a.date = new Date(a.date);
            });
        });
    },
    methods: {}
};

</script>

<style scoped>

</style>