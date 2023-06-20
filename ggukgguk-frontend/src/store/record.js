import { record } from '../api';

export default {
  namespaced: true,
  state: {
    recordOption: {
      startDateStr: new Date().toISOString().substring(0, 10),
      keyword: null,
      friendId: null
    },
    recordList: [],
    friendListByRecord: [],
    record: [],
    unacceptedRecordList: [],
  },
  getters: {
    recordOption(state) {
      return state.recordOption
    },
    recordList(state) {
      return state.recordList
    },
    record(state) {
      return state.record
    },
    friendListByRecord(state) {
      return state.friendListByRecord
    },
    unacceptedRecordList(state) {
      return state.unacceptedRecordList
    }
  },
  mutations: {
    setStartDateStr(state, startDateStr){
      state.recordOption.startDateStr = startDateStr;
      console.log(state.recordOption.startDateStr);
    },
    setKeyword(state, keyword){
      console.log(keyword);
      state.recordOption.keyword = keyword;
    },
    setFriendId(state, friendId){
      console.log(friendId);
      state.recordOption.friendId = friendId;
    },
    setRecordList(state, recordList) {
      state.recordList = recordList;
    },
    setRecordsUp(state, recordList) {
      if (recordList !== [])
        state.recordList = [...recordList, ...state.recordList];
    },
    setRecordsDown(state, recordList) {
      if (recordList !== [])
        state.recordList = [...state.recordList, ...recordList];
    },
    updateReplyList(state, {recordId, newReplyList}) {
      const index = state.recordList.findIndex(record => record.recordId === recordId);
      state.recordList[index].replyList = newReplyList;
    },
    setFriendListByRecord(state, friendListByRecord) {
      state.friendListByRecord = friendListByRecord;
    },
    setRecord(state, record) {
      state.record = record;
    },
    setUnacceptedRecordList(state, unacceptedRecordList) {
      state.unacceptedRecordList = unacceptedRecordList;
    }
  },
  actions: {
    // eslint-disable-next-line
    getRecordList({ commit, state }, memberId) {
      console.log(state.recordOption);
      return record.getRecordList( memberId, state.recordOption.startDateStr, state.recordOption.keyword, state.recordOption.friendId )
      .then((response) => {
        console.log(response.data.data);
        commit('setRecordList', response.data.data);
        return response.data.data;
      })
    },

    getRecordsUp({ commit, state }, memberId) {
      return record.getRecordList( memberId, state.recordOption.startDateStr, state.recordOption.keyword, state.recordOption.friendId )
      .then((response) => {
        console.log(response.data.data);
        commit('setRecordsUp', response.data.data);
        return response.data.data;
      })
    },

    getRecordsDown({ commit, state }, memberId) {
      return record.getRecordList( memberId, state.recordOption.startDateStr, state.recordOption.keyword, state.recordOption.friendId )
      .then((response) => {
        console.log(response.data.data);
        commit('setRecordsDown', response.data.data);
        return response.data.data;
      })
    },

    // eslint-disable-next-line
    addReply( { commit }, { memberId, recordId, replyContent } ) {
      return record.addReply( memberId, recordId, replyContent )
      .then((response) => {
        commit('updateReplyList', 
        { recordId : recordId,
          newReplyList : response.data.data 
      });
      })
    },
    
    editReply( { commit }, { memberId, recordId, replyId, replyContent } ) {
      return record.editReply( memberId, recordId, replyId, replyContent )
      .then((response) => {
        commit('updateReplyList',
        {
          recordId : recordId,
          newReplyList : response.data.data
        });
      })
    },

    deleteReply( { commit }, { recordId, replyId, memberId } ) {
      return record.deleteReply( recordId, replyId, memberId )
      .then((response) => {
        commit('updateReplyList',
        {
          recordId : recordId,
          newReplyList : response.data.data
        });
      })
    },

    // eslint-disable-next-line
    addRecord({ }, formData) {
      return record.postRecord(formData);
    },

    // eslint-disable-next-line
    updateRecord({ }, { recordId, recordComment, memberId } ) {
      return record.updateRecord(recordId, recordComment, memberId);
    },

    // eslint-disable-next-line
    deleteRecord({ }, { recordId, memberId } ) {
      return record.deleteRecord(recordId, memberId);
    },

    // eslint-disable-next-line
    getFriendListByRecord({ commit }, memberId) {
      return record.getFriendListByRecord(memberId)
        .then((response) => {
          console.log(response.data.data);
          commit('setFriendListByRecord', response.data.data);
        })
    },
    
    // eslint-disable-next-line
    getUnacceptedRecordList({ commit, state }, memberId ) {
      return record.getUnacceptedRecordList( memberId )
      .then((response) => {
        console.log(response.data.data);
        commit('setUnacceptedRecordList', response.data.data);
        return response.data.data;
      })
    },

    // eslint-disable-next-line
    updateUnaccepted({ }, { recordId, recordShareTo } ) {
      return record.updateUnaccepted( recordId, recordShareTo )
      .then((response) => {
        console.log(response.data.data);
      })
    },

    // eslint-disable-next-line
    getRecord({ }, { recordId }) {
      return record.getRecord( recordId )
    }
  }

};