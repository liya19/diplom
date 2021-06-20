<template>
    <div class="admin">
        <v-tabs v-model="tab" grow>
            <v-tab href="#categories">
                Категории
            </v-tab>
            <v-tab href="#kinds">
                Виды документов
            </v-tab>
            <v-tab href="#general">
                Бюджет
            </v-tab>
        </v-tabs>
        <v-tabs-items v-model="tab">
            <v-tab-item value="categories">
                <div class="categories" :key="categoryComponentKey">
                    <v-card>
                        <v-form class="add-category">
                            <v-text-field
                                placeholder="Категория"
                                v-model="category.name">
                            </v-text-field>
                            <v-text-field
                                placeholder="Приоритет"
                                v-model="category.priority">
                            </v-text-field>
                            <v-icon @click="addCategory">
                                mdi-plus
                            </v-icon>
                        </v-form>
                    </v-card>
                    <v-data-table
                        data-app
                        :headers="categoryHeaders"
                        :items="categories">
                        <template #item.name="{ item }">
                            <div v-if="!item.editMode"> {{ item.name }}</div>
                            <v-text-field v-else v-model="item.name"/>
                        </template>
                        <template #item.priority="{ item }">
                            <div v-if="!item.editMode"> {{ item.priority }}</div>
                            <v-text-field v-else v-model="item.priority"/>
                        </template>
                        <template #item.actions="{ item }">
                            <div>
                                <v-icon @click="editCategory(item)" v-if="!item.editMode">
                                    mdi-pencil
                                </v-icon>
                                <v-icon @click="applyCategory(item)" v-if="item.editMode">
                                    mdi-check
                                </v-icon>
                                <v-icon @click="discardCategory(item)" v-if="item.editMode">
                                    mdi-close
                                </v-icon>
                                <v-icon @click="deleteCategory(item)">
                                    mdi-delete
                                </v-icon>
                            </div>
                        </template>
                    </v-data-table>
                </div>
            </v-tab-item>
            <v-tab-item value="kinds">
                <div class="documentKind" :key="documentKindComponentKey">
                    <v-card>
                        <v-form class="add-category">
                            <v-text-field
                                placeholder="Вид"
                                v-model="documentKind.name">

                            </v-text-field>
                            <v-text-field
                                placeholder="Сумма"
                                v-model="documentKind.sum">

                            </v-text-field>
                            <v-icon @click="addDocumentKind()">
                                mdi-plus
                            </v-icon>
                        </v-form>
                    </v-card>
                    <v-data-table
                        data-app
                        :headers="documentKindHeader"
                        :items="documentKinds">
                        <template #item.name="{ item }">
                            <div v-if="!item.editMode"> {{ item.name }}</div>
                            <v-text-field v-else v-model="item.name"/>
                        </template>
                        <template #item.sum="{ item }">
                            <div v-if="!item.editMode"> {{ item.sum }}</div>
                            <v-text-field v-else v-model="item.sum"/>
                        </template>
                        <template #item.actions="{ item }">
                            <div>
                                <v-icon @click="editDocumentKind(item)" v-if="!item.editMode">
                                    mdi-pencil
                                </v-icon>
                                <v-icon @click="applyDocumentKind(item)" v-if="item.editMode">
                                    mdi-check
                                </v-icon>
                                <v-icon @click="discardDocumentKind(item)" v-if="item.editMode">
                                    mdi-close
                                </v-icon>
                                <v-icon @click="deleteDocumentKind(item)">
                                    mdi-delete
                                </v-icon>
                            </div>
                        </template>
                    </v-data-table>
                </div>
            </v-tab-item>
            <v-tab-item value="general">
                <div class="me-6">
                    <div style="width: 80%; display:inline-flex; align-items: baseline">
                        <v-text-field type="number" label="Бюджет" v-model="budget"/>
                        <v-icon @click="setBudget" class="pl-4">mdi-plus</v-icon>
                    </div>
                    <div v-if="budgetAnalytics">
                        <v-row style="padding: 10px">
                            <v-col>Месяц</v-col>
                            <v-col>Предложенные системой</v-col>
                            <v-col>Назначенные средства</v-col>
                            <v-col>Сколько осталось</v-col>
                        </v-row>
                        <div v-for="budgetInfo in budgetAnalytics.budgetInfos" :key="budgetInfo.id"
                             style="padding: 10px">
                            <v-row>
                                <v-col>{{ months[new Date(budgetInfo.dateTo).getMonth() + 1] }}</v-col>
                                <v-col>{{ budgetInfo.fullBudget }}</v-col>
                                <v-col>{{ budgetInfo.remainingBudget }}</v-col>
                                <v-col
                                    :style="'color: ' + (budgetInfo.fullBudget >= budgetInfo.remainingBudget? 'green':'red')">
                                    {{ budgetInfo.fullBudget - budgetInfo.remainingBudget }}
                                </v-col>
                            </v-row>
                        </div>
                        <div style="text-align: end">
                            Оставшийся бюджет:
                            {{ budgetAnalytics.currentBudget.remainingBudget }}
                        </div>
                    </div>
                </div>
            </v-tab-item>
        </v-tabs-items>
    </div>
</template>

<script>
import { $http } from '@/service/http';

const months = {
    1: 'Январь',
    2: 'Февраль',
    3: 'Март',
    4: 'Апрель',
    5: 'Май',
    6: 'Июнь',
    7: 'Июль',
    8: 'Август',
    9: 'Сентябрь',
    10: 'Октябрь',
    11: 'Ноябрь',
    12: 'Декабрь'
};
const category_headers = [
    {
        text: 'Категория',
        value: 'name',
        align: 'center',
        width: '100px'
    }, {
        text: 'Приоритет',
        value: 'priority',
        align: 'center',
        width: '100px'
    }, {
        text: 'Действия',
        value: 'actions',
        align: 'center',
        width: '100px'
    }
];
const document_kind_headers = [
    {
        text: 'Вид',
        value: 'name',
        align: 'center',
        width: '100px'
    }, {
        text: 'Сумма',
        value: 'sum',
        align: 'center',
        width: '100px'
    }, {
        text: 'Действия',
        value: 'actions',
        align: 'center',
        width: '100px'
    }
];

export default {
    name: 'AdminPage',
    data() {
        return {
            tab: undefined,
            budget: undefined,
            months: months,
            budgetAnalytics: undefined,
            documentKindHeader: document_kind_headers,
            categoryHeaders: category_headers,
            categories: [],
            documentKinds: [],
            categoryComponentKey: 1,
            documentKindComponentKey: 999,
            category: {
                name: undefined,
                priority: undefined
            },
            documentKind: {
                name: undefined,
                sum: undefined
            }
        };
    },
    mounted() {
        this.getBudget();
        $http.get('documentKinds')
            .then(response => this.documentKinds = response.data);
        $http.get('categories')
            .then(response => this.categories = response.data);
    },
    methods: {
        setBudget() {
            const budget = parseInt(this.budget);
            $http.post('system/budget', null, { params: { budget } }).then(() => {
                this.getBudget();
            });
        },
        getBudget() {
            $http.get('system/budget/analytics').then((response) => {
                this.budgetAnalytics = response.data;
                this.budget = response.data?.currentBudget?.fullBudget;
            });
        },
        deleteCategory(item) {
            $http.delete('category/delete', { params: { id: item.id } });
        },
        editCategory(item) {
            item.copy = { ...item };
            item.editMode = true;
            this.categoryComponentKey += 1;
        },
        deleteDocumentKind(item) {
            $http.delete('documentKind/delete', { params: { id: item.id } });
        },
        editDocumentKind(item) {
            item.copy = { ...item };
            item.editMode = true;
            this.documentKindComponentKey += 1;
        },
        addCategory() {
            if (this.category.name && this.category.priority) {
                $http.post('category/save', this.category).then(() => {
                    this.categories.push(this.category);
                    this.category = {
                        name: undefined,
                        priority: undefined
                    };
                    this.categoryComponentKey += 1;
                }, () => {
                    this.categories.push(this.category);
                    this.category = {
                        name: undefined,
                        priority: undefined
                    };
                    this.categoryComponentKey += 1;
                });
            }
        },
        addDocumentKind() {
            if (this.documentKind.name && this.documentKind.sum) {
                $http.post('documentKind/save', this.documentKind).then(() => {
                    this.documentKinds.push(this.documentKind);
                    this.documentKind = {
                        name: undefined,
                        sum: undefined
                    };
                    this.documentKindComponentKey += 1;
                }, () => {
                    this.documentKinds.push(this.documentKind);
                    this.documentKind = {
                        name: undefined,
                        sum: undefined
                    };
                    this.documentKindComponentKey += 1;
                });
            }
        },
        applyDocumentKind(item) {
            item.editMode = false;
            $http.patch('documentKind', item);
            this.documentKindComponentKey += 1;
        },
        discardDocumentKind(item) {
            item.name = item.copy.name;
            item.sum = item.copy.sum;
            item.editMode = false;
            this.documentKindComponentKey += 1;
        },
        applyCategory(item) {
            item.editMode = false;
            $http.patch('category', item);
            this.categoryComponentKey += 1;
        },
        discardCategory(item) {
            item.name = item.copy.name;
            item.priority = item.copy.priority;
            item.editMode = false;
            this.categoryComponentKey += 1;
        }
    }
};
</script>

<style scoped>


.add-category {
    display: grid;
    grid-template-columns: 2fr 2fr 1fr;
    grid-gap: 10px;
    margin-left: 20px;
}
</style>