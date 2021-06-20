<template>
    <v-card data-app class="class">
        <v-card-title style="place-content: center">
            Добавление заявления
        </v-card-title>
        <v-card-text :key="key">
            <v-form ref="form">
                <div class="autocomplete">
                    <v-text-field v-model="user.lastName"
                                  label="Фамилия"
                                  @input="onChange"
                                  @focus="onChange"
                                  @blur="onBlur"
                                  :rules="rules"/>
                    <ul
                        v-show="isOpen"
                        class="autocomplete-results">
                        <li
                            v-for="(result, i) in autocompleteHints"
                            :key="i"
                            class="autocomplete-result">
                            <div @click.stop="autocompleteSelect(result)">
                                {{ result.lastName + ' ' + result.firstName + ' ' + result.middleName }}
                            </div>
                        </li>
                    </ul>
                </div>
                <v-text-field v-model="user.firstName"
                              placeholder="Имя"
                              :rules="rules"/>
                <v-text-field v-model="user.middleName"
                              label="Отчество(если есть)"/>
                <v-text-field v-model="user.email"
                              label="Email"/>
                <v-text-field v-model="user.group"
                              placeholder="Группа"
                              :rules="rules"/>
                <v-text-field type="date" v-model="user.date"
                              placeholder="Дата"
                              :rules="rules"/>
                <v-select placeholder="Категории"
                          v-model="user.categories"
                          multiple
                          :items="categories"
                          item-text="name"
                          item-value="id"
                          :rules="rules"/>
                <v-select placeholder="Тип заявления"
                          v-model="user.documentKind"
                          multiple
                          :items="documentKinds"
                          item-text="name"
                          item-value="id"
                          :rules="rules"/>
            </v-form>
            <div style="text-align: center">
                <v-btn @click="submitApplication" :disabled="valid"> Подтвердить</v-btn>
            </div>
        </v-card-text>
    </v-card>
</template>

<script>
import { $http } from '@/service/http';

function notEmpty(value) {
    if (value) {
        return true;
    } else {
        return 'Необходимо заполнить';
    }
}

export default {
    name: 'SubmitApplicationForm',
    data() {
        return {
            rules: [notEmpty],
            isOpen: false,
            autocompleteHints: [],
            key: 1,
            user: {
                firstName: undefined,
                lastName: undefined,
                middleName: undefined,

                date: undefined,

                categories: [],
                email: undefined,
                documentKind: []
            },
            categories: [],
            documentKinds: []
        };
    },
    computed: {
        valid() {
            return !(this.user.firstName &&
                this.user.lastName &&
                this.user.date &&
                this.user.categories.length &&
                this.user.documentKind.length);
        }
    },
    mounted() {
        $http.get('documentKinds')
            .then(response => this.documentKinds = response.data);
        $http.get('categories')
            .then(response => this.categories = response.data);

        setTimeout(() => {
            const closeBtn = document.getElementsByClassName('vuedl-layout__closeBtn')[0];
            closeBtn.style.right = '15px';
        }, 100);
    },
    methods: {
        submitApplication() {
            console.log(this.user);
            $http.post('application/save', this.user).then((response) => {
                this.$bus.$emit('accepted', response.data);
                this.$emit('submit');
            }, () => {
                this.$emit('submit');
            });
        },
        autocompleteSelect(result) {
            this.user = result;
            this.user.firstName = result.firstName;
            this.user.lastName = result.lastName;
            this.user.middleName = result.middleName;

            this.user.categories = result.categories.map(c => c.id);
            this.user.email = result.email;
            this.isOpen = false;

            this.key++;
        },
        onChange() {
            let lastName = this.user.lastName;
            if (lastName) {
                $http.get('/api/person', { params: { lastName } }).then((response) => {
                    this.autocompleteHints = response.data;
                    this.isOpen = this.autocompleteHints.length > 0;
                });
            } else {
                this.autocompleteHints = [];
                this.isOpen = false;
            }
        },
        onBlur() {
            setTimeout(() => {
                this.isOpen = false;
            }, 300);
        }
    }
};
</script>

<style scoped lang="scss">
.autocomplete {
    position: relative;
}

.autocomplete-results {
    padding: 0;
    position: absolute;
    margin: 0;
    border: 1px solid #eeeeee;
    min-height: 1em;
    max-height: 6em;
    width: 100%;
    background-color: white;
    z-index: 10;
    overflow: auto;
}

.autocomplete-result {
    list-style: none;
    text-align: left;
    padding: 4px 2px;
    z-index: 10;
    cursor: pointer;
}

.autocomplete-result:hover {
    background-color: #4AAE9B;
    color: white;
}

</style>