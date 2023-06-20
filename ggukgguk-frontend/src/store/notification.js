import { notification } from '../api';

export default {
    namespaced: true,
    state: {
        notifyList: [],
        unReadNotify: ''
    },
    getters: {
        notifyList(state) {
            return state.notifyList;
        },
        unReadNotify(state) {
            return state.unReadNotify;
        }
    },
    mutations: {
        setNotifyList(state, notifyList) {
            state.notifyList = notifyList;
        },
        setUnReadNotify(state, unReadNotify) {
            state.unReadNotify = unReadNotify;
        }
    },
    actions: {
        // 알림 조회
        getNotifyList({ commit }) {
            return notification.getNotifyList()
                .then((response) => {
                    commit('setNotifyList', response.data.data);
                    return response;
                })
        },
        // 안읽은 알림 수 조회
        getUnReadNotify({ commit }) {
            return notification.UnreadNotify()
                .then((response) => {
                    console.log("store에 안 읽음 알림 수 처리");
                    commit('setUnReadNotify', response.data.data);
                    return response;
                })
        },

        // 알림 읽음 수행.
        readNotify({ commit }, { notificationId, notificationIsRead }) {
            return notification.putNotify(notificationId, notificationIsRead)
                .then((response) => {
                    console.log("store에 알림 읽음 처리");
                    return notification.getNotifyList()
                        .then(updatedResponse => {
                            commit('setNotifyList', updatedResponse.data.data);
                            return response;
                        });
                });
        }

    }
}