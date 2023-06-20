import axios from 'axios';
import store from '../store';
import router from '../router';

let instance;

function getInstance() {
    if (instance) {
        return instance;
    }

    instance = axios.create({
        // eslint-disable-next-line
        baseURL: process.env.VUE_APP_BASE_URI,
        withCredentials: true
    });

    instance.interceptors.request.use((config) => {
        const accessToken = store.getters['auth/accessToken'];
        if (accessToken !== '') {
            config.headers = {
                ...config.headers,
                'Authorization': `Bearer ${accessToken}`
            };
        }

        return config;
    })

    instance.interceptors.response.use((response) => {
        return response;
    }, async (error) => {
        if (error.response?.status !== 401) return Promise.reject(error);

        const refreshToken = store.getters['auth/refreshToken'];
        try {
            const refreshResonse = await instance.post('/auth/refresh', {
                refreshToken
            });
            const newToken = refreshResonse.data.data.accessToken;
            console.log('새로운 토큰으로 갱신함:');
            store.commit('auth/updateAccessToken', { accessToken: newToken });
            error.config.headers = {
                ...error.config.headers,
                Authorization: `Bearer ${newToken}`,
            };

            const retryRefresh = await axios.request(error.config);
            console.log('액세스 토큰 만료되어 갱신하여 다시 요청 시도 [성공]');
            return retryRefresh;
        } catch (errorRefreshing) {
            console.log('액세스 토큰 만료되어 갱신하여 다시 요청 시도 [실패]');
            router.push({name: 'login'});
            return Promise.reject(errorRefreshing);
        }
    })

    return instance;
}

export default { getInstance };