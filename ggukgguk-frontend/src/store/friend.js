import { friend } from '../api';

export default {
  namespaced: true,
  state: {
    friendList: [],
    requestFriendList: []
  },
  getters: {
    friendList(state) {
      return state.friendList
    },
    requestFriendList(state){
      return state.requestFriendList;
    }
  },
  mutations: {
    setFriendList(state, friendList) {
      state.friendList = friendList;
    },
    setRequestFriendList(state,requestFriendList){
      state.requestFriendList = requestFriendList;
    },
    // 친구를 차단시켰을 때 store의 friendList 배열에서 친구를 제거목적.
    removeFriend(state, friendId) {
      // friendList에 차단한 친구 객체값?을 없애기 위해 해당 인덱스를 찾아서 FriendList에 삭제함.
      // 만약 인덱스가 -1이 아니라면. 즉 값이 있으면 해당 인덱스 위치에 있는 친구 ID의 정보를 비움.
      const index = state.friendList.findIndex(friend => friend.memberId === friendId);
      if (index !== -1) {
        state.friendList.splice(index, 1);
      }
    },
    setFriendsDown(state, friendList) {
      if (friendList !== [])
        state.friendList = [...state.friendList, ...friendList];
    }
  },
  actions: {
    // 친구 목록 조회
    // eslint-disable-next-line
    getFriendList({ commit }, memberId) {
      return friend.getFriendList(memberId)
        .then((response) => {
          console.log(response.data.data);
          commit('setFriendList', response.data.data);
        })
    },

    // 친구 요청 테이블 조회
    getRequestFriendList({commit},{ friendRequestId }) {
      {
        console.log(friendRequestId);
        return friend.selectMyFriendRequestList({ friendRequestId })
          .then((response) => {
            console.log("친구요청 테이블 조회 완료");
            commit('setRequestFriendList', response.data.data);
            return response;
          });
      }
    },

    //친구 추가.
    // eslint-disable-next-line
    additionFriendId({ }, friendId) {
      return friend.addFriend(friendId)
        .then((response) => {
          console.log(response)
        })
    },

    //친구 차단.
    delRelationshipFriend({ commit }, friendId) {
      return friend.deleteFriend(friendId)
        .then((response) => {
          console.log(response);
          console.log('친구를 차단하였고, list를 확인해야 함')
          commit('removeFriend', friendId);
        })
    },

    // 친구목록 내리기
    getFriendsDown({ commit }, memberId) {
    return friend.getFriendList(memberId)
    .then((response) => {
      console.log(response.data.data);
      commit('setFriendsDown', response.data.data);
      return response.data.data;
    })
  },
  }
};