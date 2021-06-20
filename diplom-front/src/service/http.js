import axios from 'axios';
import qs from 'qs';


let instance = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    paramsSerializer(p) {
        return qs.stringify(p, { arrayFormat: 'comma' });
    }
});

export const $http = instance;