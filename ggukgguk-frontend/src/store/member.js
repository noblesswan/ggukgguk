import { member } from "@/api";

export default {
    namespaced: true,

    // [변수들의 집합]
    state: {
        memberDetailInfo: {},
        friendList: [],
        memberEmail: ''
    },
    // [state의 변수들을 get 호출]
    getters: {
        memberDetailInfo(state) {
            return state.memberDetailInfo;
        },
        friendList(state) {
            return state.friendList;
        },
        memberEmail(state) {
            return state.memberEmail;
        }
    },
    // [변수들을 조작하는 함수들]
    mutations: {
        setMemberDetailInfo(state, memberDetailInfo) {
            state.memberDetailInfo = memberDetailInfo;
        },
        setFriendList(state, newFriendList) {
            state.friendList = newFriendList;
        },
        setMemberEmail(state, memberEmail) {
            state.memberEmail = memberEmail;
        }
    },
    // [비동기 처리를 하는 함수들]
    actions: {
        // 회원 정보 조회
        informationMemberDetail({ commit }, memberId) {
            return member.memberInformation(memberId)
                .then((response) => {
                    console.log('회원정보 조회 완료');
                    console.log(response);
                    commit('setMemberDetailInfo', response.data.data);
                    return response;
                })
        },
        // 친구 목록 조회
        fetchFriendList({ commit }) {
            return member.getFriendList()
                .then((response) => {
                    commit('setFriendList', response.data.data);
                })
        },
        // 회원 정보 수정
        modfiyMemberInfo({ commit }, { memberId, memberPw, memberName, memberNickname,
            memberEmail, memberPhone, memberBirth, memberAllowEmail }) {
        
                return member.memberModify({ memberId, memberPw, memberName, memberNickname, memberEmail, memberPhone, memberBirth, memberAllowEmail })
                .then((response) =>{
                    console.log('회원정보 수정 완료');
                    console.log(response);
                    commit('auth/updateMember', response.data.data, { root: true });
                    commit('setMemberDetailInfo', response.data.data);
                });
        }
    }
}