import apiFactory from "./apiFactory"
import store from '../store'

const axios = apiFactory.getInstance();

export default {
    // 회원 정보 조회
    memberInformation(memberId) {
        console.log(memberId);
        return axios.get(`/member/${memberId}`,
            {
                headers: {
                    Authorization: `Bearer ${store.getters['auth/accessToken']}`
                }
            });
    },

    // 친구 목록 조회
    getFriendList() {
        return axios.get(`/friend/list`);
    },

    // 회원 정보 수정
    memberModify({ memberId, memberPw, memberName, memberNickname,
        memberEmail, memberPhone, memberBirth, memberAllowEmail }) {
        return axios.put(`/member/${memberEmail}`,
            {
                memberId, memberPw, memberName, memberNickname,
                memberEmail, memberPhone, memberBirth, memberAllowEmail
            },
            {
                headers: {
                    Authorization: `Bearer ${store.getters['auth/accessToken']}`
                }
            });
    }
} 