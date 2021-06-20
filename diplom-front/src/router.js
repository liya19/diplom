import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

export const router = new VueRouter({
    mode: 'history',

    routes: [
        {
            path: '/applications',
            name: 'applications',
            component: () => import('./view/Applications')
        },
        {
            path: '/createApplication',
            name: 'createApplication',
            component: () => import('./view/SubmitApplicationForm')
        },
        {
            path: '/issuedApplications',
            name: 'issuedApplications',
            component: () => import('./view/IssuedApplications')
        },
        {
            path: '/admin',
            name: 'adminPage',
            component: () => import('./view/AdminPage')
        }
    ]
});
