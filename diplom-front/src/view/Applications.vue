<template>
    <div :key="componentKey">
        <div style="text-align: end" class="d-flex gap" :key="submitKey">
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon x-large slot="activator" v-on="on" v-if="!submitMode" @click="enableSubmitMode()">
                        mdi-pencil
                    </v-icon>
                </template>
                <span>Режим редактирования</span>
            </v-tooltip>

            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon v-if="submitMode" x-large slot="activator" v-on="on" @click="submit()"
                            style="margin-right: 10px">mdi-check
                    </v-icon>
                </template>
                <span>Подтвердить</span>
            </v-tooltip>
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon v-if="submitMode" x-large slot="activator" v-on="on" @click="decline()"
                            style="margin-right: 10px">mdi-close
                    </v-icon>
                </template>
                <span>Отменить</span>
            </v-tooltip>
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon x-large slot="activator" v-on="on" @click="importForm">mdi-table-arrow-left</v-icon>
                </template>
                <span>Импортировать список</span>
            </v-tooltip>
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon x-large slot="activator" v-on="on" @click="downloadExcel">mdi-file-download-outline</v-icon>
                </template>
                <span>Скачать</span>
            </v-tooltip>
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon x-large slot="activator" v-on="on" @click="algorithm()">mdi-puzzle-outline</v-icon>
                </template>
                <span>Алгоритм</span>
            </v-tooltip>
            <v-tooltip bottom>
                <template #activator="{ on }">
                    <v-icon x-large slot="activator" v-on="on" @click="goToApplicationCreatePage()">mdi-text-box-plus
                    </v-icon>
                </template>
                <span>Создание заявления</span>
            </v-tooltip>
        </div>
        <v-data-table
            data-app
            :headers="header"
            hide-default-footer
            disable-pagination
            :sort-desc.sync="sortDesc"
            :sort-by.sync="defaultSort"
            :items="applications">
            <template #item.group="{ item }">
                <div v-if="!item.editMode"> {{ item.group }}</div>
                <v-text-field v-else v-model="item.group"/>
            </template>
            <template #item.firstName="{ item }">
                <div v-if="!item.editMode"> {{ item.firstName }}</div>
                <div v-else class="editable-field">
                    <v-text-field v-model="item.firstName"/>
                </div>
            </template>
            <template #item.lastName="{ item }">
                <div v-if="!item.editMode"> {{ item.lastName }}</div>
                <div v-else class="editable-field">
                    <v-text-field v-model="item.lastName"/>
                </div>
            </template>
            <template #item.middleName="{ item }">
                <div v-if="!item.editMode"> {{ item.middleName }}</div>
                <div v-else class="editable-field">
                    <v-text-field v-model="item.middleName"/>
                </div>
            </template>
            <template #item.categories="{ item }">
                <div v-if="!item.editMode">
                    <div v-for="category of item.categories" :key="category.id">
                        {{ category.name }}
                    </div>
                </div>
                <div v-else class="editable-field">
                    <v-select placeholder="Категории"
                              v-model="item.categories"
                              multiple
                              dense
                              :items="categories"
                              item-text="name"
                              item-value="id"/>
                </div>
            </template>
            <template #item.kind="{ item }">
                <div v-if="!item.editMode"> {{ item.documentKind.name }}</div>
                <div v-else class="editable-field">
                    <v-select placeholder="Тип заявления"
                              v-model="item.documentKind"
                              :items="documentKinds"
                              item-text="name"
                              item-value="id"/>
                </div>
            </template>
            <template #item.date="{ item }">
                <div v-if="!item.editMode"> {{ item.date.toLocaleDateString('ru-RU') }}</div>
                <div v-else class="editable-field">
                    <v-text-field type="date" v-model="item.date"/>
                </div>
            </template>
            <template #item.status="{ item }">
                <div v-if="!submitMode">
                    <div v-if="item.status === 'ISSUED'">
                        <v-icon color="green" v-if="!item.editMode"> mdi-check</v-icon>
                        <v-checkbox v-model="item.issued" v-if="item.editMode"/>
                    </div>
                    <div v-if="item.status === 'SUGGESTED'">
                        <v-icon color="green" v-if="!item.editMode"> mdi-check</v-icon>
                        <v-checkbox v-model="item.issued" v-if="item.editMode"/>
                    </div>
                    <div v-else>
                        <v-icon v-if="!item.editMode"> mdi-close</v-icon>
                        <v-checkbox v-model="item.issued" v-if="item.editMode"/>
                    </div>
                </div>
                <div v-else>
                    <v-checkbox v-model="item.issued"/>
                </div>
            </template>
            <template #item.buttons="{ item }" v-if="!this.issued">
                <div>
                    <v-icon @click="remove(item)">
                        mdi-delete
                    </v-icon>
                    <v-icon @click="edit(item)" v-if="!item.editMode">
                        mdi-pencil
                    </v-icon>
                    <v-icon @click="discard(item)" v-if="item.editMode">
                        mdi-close
                    </v-icon>
                    <v-icon @click="apply(item)" v-if="item.editMode">mdi-check</v-icon>
                </div>
            </template>
        </v-data-table>
    </div>
</template>

<script>


import { $http } from '@/service/http';
import SuggestForm from '@/view/SuggestForm';
import ImportForm from '@/view/ImportForm';
import SubmitApplicationForm from '@/view/SubmitApplicationForm';

const headers = [
    {
        text: 'Группа',
        value: 'group',
        align: 'center',
        width: '100px'
    }, {
        text: 'Фамилия',
        value: 'lastName',
        align: 'center',
        width: '100px'
    }, {
        text: 'Имя',
        value: 'firstName',
        align: 'center',
        width: '100px'
    }, {
        text: 'Отчество',
        value: 'middleName',
        align: 'center',
        width: '100px'
    }, {
        text: 'Категории',
        value: 'categories',
        align: 'center',
        width: '100px'
    }, {
        text: 'Тип',
        value: 'kind',
        align: 'center',
        width: '100px'
    }, {
        text: 'Дата',
        value: 'date',
        align: 'center',
        width: '100px'
    }, {
        text: 'Выдана',
        value: 'status',
        align: 'center',
        width: '50px'
    }, {
        text: 'Действия',
        value: 'buttons',
        align: 'center',
        width: '100px'
    }
];
export default {
    name: 'Applications',
    props: {
        issued: Boolean
    },
    data() {
        return {
            applications: [],
            header: headers,
            componentKey: 1,
            categories: [],
            documentKinds: [],
            defaultSort: 'status',
            sortDesc: true,
            submitMode: false,
            submitKey: 9999
        };
    },
    created() {
        this.$bus.$on('accepted', (applications) => {
            if (applications instanceof Array) {
                applications.forEach(a => {
                    a.editMode = false;
                    a.issued = a.status === 'SUGGESTED' || a.status === 'ISSUED';
                    a.date = new Date(a.date);
                });
                this.applications = applications;
                this.componentKey += 1;
            }
            console.log(applications);
        });
    },
    mounted() {
        $http.get('documentKinds')
            .then(response => this.documentKinds = response.data);
        $http.get('categories')
            .then(response => this.categories = response.data);
        if (this.issued) {
            $http.get('applications/issued').then((response) => {
                this.applications = response.data;
                this.applications.forEach(a => {
                    a.editMode = false;
                    a.date = new Date(a.date);
                });
            });
        } else {
            $http.get('applications').then((response) => {
                this.applications = response.data;
                this.applications.forEach(a => {
                    a.editMode = false;
                    a.issued = a.status === 'SUGGESTED' || a.status === 'ISSUED';
                    a.date = new Date(a.date);
                });
            });
        }
    },
    methods: {
        edit(item) {
            item.itemCopy = { ...item };
            item.categories = item.categories.map(c => c = c.id);
            item.documentKind = item.documentKind.id;
            item.date = item.date.toISOString().substring(0, 10);
            item.editMode = true;

            this.componentKey += 1;
        },

        discard(item) {
            item.date = item.itemCopy.date;
            item.firstName = item.itemCopy.firstName;
            item.lastName = item.itemCopy.lastName;
            item.middleName = item.itemCopy.middleName;
            item.editMode = false;
            item.categories = item.itemCopy.categories;
            item.documentKind = item.itemCopy.documentKind;

            this.componentKey += 1;
        },

        remove(item) {
            const params = { id: item.id };
            $http.delete('application/delete', { params }).then(response => {
                console.log(response);
                if (response.status === 200) {
                    this.applications.splice(this.applications.findIndex(value => value.id === item.id), 1);
                    this.componentKey += 1;
                }
            });
        },

        apply(item) {
            if (item.issued) {
                item.status = 'ISSUED';
            }
            $http.patch('application', item).then(
                response => {
                    item.date = new Date(response.data.date);
                    item.firstName = response.data.firstName;
                    item.lastName = response.data.lastName;
                    item.middleName = response.data.middleName;
                    item.editMode = false;
                    item.categories = response.data.categories;
                    item.documentKind = response.data.documentKind;
                }
            );
            this.componentKey += 1;
        },
        enableSubmitMode() {
            this.submitMode = true;
        },
        submit() {
            let listIds = [];
            this.applications.forEach(a => {
                if (a.issued) {
                    listIds.push(a.id);
                }
            });
            let params = {
                listIds: listIds
            };
            $http.get('application/submit', { params }).then(response => {
                this.applications = response.data;
                this.applications.forEach(a => {
                    a.editMode = false;
                    a.issued = a.status === 'SUGGESTED' || a.status === 'ISSUED';
                    a.date = new Date(a.date);
                    this.submitMode = false;
                });
            });
        },
        decline() {
            this.submitMode = false;
            this.applications.forEach(a => {
                a.editMode = false;
                a.issued = a.status === 'SUGGESTED' || a.status === 'ISSUED';
            });
        },
        algorithm() {
            this.$dialog.show(SuggestForm).then(() => {
            });
        },
        downloadExcel() {
            this.$http.get('/application/excel/download', {
                header: 'connection=keep-alive',
                responseType: 'blob'
            }).then(response => {
                console.log(response);
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'applications.xlsx');
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            });
        },
        goToApplicationCreatePage() {
            this.$dialog.show(SubmitApplicationForm);
        },
        importForm() {
            this.$dialog.show(ImportForm).then(() => {
                console.log('success');
            });
        }
    },
    watch: {
        submitMode() {
            this.submitKey += 1;
        }
    }
};
</script>

<style scoped lang="scss">
.editable-field {
    width: 150px;
}

.gap {
    > .v-icon {
        margin-left: 5px;
        margin-right: 5px;
    }
}

.scroll {
    overflow: hidden !important;
}
</style>