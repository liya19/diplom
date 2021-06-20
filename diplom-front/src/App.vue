<template>
    <div id="app">
        <div class="header">
            <div class="header__left">
                <v-btn @click="goToApplicationList()">
                    Все заявления
                </v-btn>
                <v-btn @click="goToIssuedApplications()">
                    Выданные заявления
                </v-btn>
                <v-btn @click="goToAdminPage">
                    Админка
                </v-btn>
            </div>
        </div>
        <router-view class="content"/>
    </div>
</template>

<script>

export default {
    name: 'App',
    components: {},
    methods: {
        goToApplicationList() {
            this.$router.push('/applications');
        },
        goToIssuedApplications() {
            this.$router.push('/issuedApplications');
        },
        goToAdminPage() {
            this.$router.push('/admin');
        }
    }
};
</script>

<style lang="scss">
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
}

.header {
    height: 64px;
    width: 100%;
    display: inline-flex;
    align-items: center;

    &__left {
        width: 50%;

        > * {
            margin-left: 10px;
        }
    }

    &__right {
        text-align: end;
        width: 50%;

        > * {
            margin-right: 10px;
        }
    }
}
</style>
