import apiFactory from "./apiFactory"
import store from '../store'
const axios = apiFactory.getInstance();

export default {
    // 나의 친구 목록 조회하기
    getFriendList({ memberId }) {
        return axios.get('/friend/list', {
            memberId
        })
    },
    // 친구 아이디 일부 값으로 친구 조회하기 
    getPartialIdSearch(memberId) {
        console.log("axios: " + memberId);
        return axios.get(`/friend`, {
            params: { memberId },
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },
    // 친구 요청
    addFriend(toMemberId) {
        console.log("axios: " + toMemberId);
        return axios.post(`/friend/send/${toMemberId}`, {
            params: { toMemberId },
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },
    // 친구 차단
    deleteFriend(toMemberId) {
        console.log("axios: " + toMemberId);
        return axios.delete(`/friend/${toMemberId}`, {
            params: { toMemberId },
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },

    // 친구 요청 수락.
    applyFriendRequest(toMemberId) {
        // console.log("로그인 한 ID :", memberId);
        console.log("친구 요청한 ID : ", toMemberId);
        return axios.post(`/friend/accept/${toMemberId}`, {
            toMemberId
        }, {
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },
    // 나의 친구요청 테이블 조회
    selectMyFriendRequestList({ friendRequestId }) {
        console.log(friendRequestId);
        return axios.get(`/friend/requestFriendlist`,
            {
                params: {
                    friendRequestId: friendRequestId
                },
                headers: {
                    Authorization: `Bearer ${store.getters['auth/accessToken']}`
                }
            });
    },
    // 상대방에게 친구 요청 안내 메일 전송
    friendRequestInfo({ sendTo }) {
        return axios.get(`/friend/mailCertification`,
            {
                params: {
                    sendTo
                },
                headers: {
                    Authorization: `Bearer ${store.getters['auth/accessToken']}`
                }
            });

    }
}