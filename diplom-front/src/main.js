import Vue from 'vue';
import vuetify from './plugins/vuetify'; // path to vuetify export

//import 'material-design-icons-iconfont/dist/material-design-icons.css';
import VuetifyDialog from 'vuetify-dialog';
import 'vuetify-dialog/dist/vuetify-dialog.css';
import { router } from './router';
import App from '@/App';
import { $http } from '@/service/http';

Vue.use(VuetifyDialog, {
    context: {
        vuetify,
        router
    }
});

Vue.prototype.$http = $http;

Vue.prototype.$bus = new Vue();

new Vue({
    vuetify,
    router,
    render: h => h(App)
}).$mount('#app');


