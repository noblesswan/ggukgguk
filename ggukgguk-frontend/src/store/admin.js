import { admin } from '../api';

export default {
  namespaced: true,
  state: {
    noticeOption: {
      page: 1,
      size: 10
    },
    contentOption: {
      page: 1,
      size: 10
    },
    memberOption: {
      page: 1,
      size: 10
    },
    analysisData: {
      totalMember: 0,
      todayMember: 0,
      totalContent: 0,
      todayContent: 0
    },
    noticeTotal: 0,
    adminMain: [],
    noticeList: [],
    contentList: [],
    contentDetail: [],
    memberList: [],
    memberDetail: [],
    dailyReport: {},
    recentBatch: {},
    batchDetail: [],
    batchOption: {
      page: 1,
      size: 10,
      jobName: 'extractKeywordJob'
    },
    mediaFileDetail: {},
    mediaFileRecheckRequest: [],
    mediaFileRecheckRequestOption: {
      page: 1,
      size: 10,
      mediaFileId: null,
      mediaFileRecheckRequestStatus: null
    },
    mediaFileRecheckRequestTotal: 0
  },

  getters: {

    // 다른 컴포넌트에서 이 값을 호출할 때 사용
    getAdminMain(state) {
      return state.adminMain
    },
    noticeOption(state) {
      return state.noticeOption
    },
    noticeList(state) {
      return state.noticeList
    },
    noticeTotal(state) {
      return state.noticeTotal;
    },
    analysisData(state) {
      return state.analysisData;
    },
    contentOption(state) {
      return state.contentOption
    },
    contentList(state) {
      return state.contentList
    },
    contentTotal(state) {
      return state.contentTotal;
    },
    contentDetail(state) {
      return state.contentDetail;
    },
    memberOption(state) {
      return state.memberOption
    },
    memberList(state) {
      return state.memberList
    },
    memberTotal(state) {
      return state.memberTotal;
    },
    memberDetail(state) {
      return state.memberDetail;
    },
    dailyReport(state) {
      return state.dailyReport;
    },
    recentBatch(state) {
      return state.recentBatch;
    },
    batchDetail(state) {
      return state.batchDetail;
    },
    batchOption(state) {
      return state.batchOption;
    },
    mediaFileDetail(state) {
      return state.mediaFileDetail;
    },
    mediaFileRecheckRequest(state) {
      return state.mediaFileRecheckRequest;
    },
    mediaFileRecheckRequestTotal(state) {
      return state.mediaFileRecheckRequestTotal;
    },
    mediaFileRecheckRequestOption(state) {
      return state.mediaFileRecheckRequestOption;
    }
  },
  mutations: {

    // actions가 호출
    // actions로부터 데이터를 받음

    setAdminMain(state, newAdminMain) {
      state.adminMain = newAdminMain
    },
    addNotice(state, notice) {
      state.noticeList.push(notice);
    },
    setNoticePage(state, page) {
      state.noticeOption.page = page;
    },
    setNoticeList(state, noticeList) {
      state.noticeList = noticeList;
    },
    setNoticeTotal(state, noticeTotal) {
      state.noticeTotal = noticeTotal;
    },
    setDailyReportAll(state, dailyReport) {
      state.dailyReport = dailyReport;
    },
    setAnalysisData(state, analysisData) {
      state.analysisData = analysisData;
    },
    setContentPage(state, page) {
      state.contentOption.page = page;
    },
    setContentList(state, contentList) {
      state.contentList = contentList;
    },
    setContentTotal(state, contentTotal) {
      state.contentTotal = contentTotal;
    },
    setContentDetail(state, contentDetail) {
      state.contentDetail = contentDetail;
    },
    setMemberPage(state, page) {
      state.memberOption.page = page;
    },
    setMemberList(state, memberList) {
      state.memberList = memberList;
    },
    setMemberTotal(state, memberTotal) {
      state.memberTotal = memberTotal;
    },
    setMemberDetail(state, memberDetail) {
      state.memberDetail = memberDetail;
    },
    setRecentBatch(state, recentBatch) {
      state.recentBatch = recentBatch;
    },
    setBatchDetail(state, batchDetail) {
      state.batchDetail = batchDetail;
    },
    updateBatchOption(state, batchOption) {
      state.batchOption = {...state.batchOption, ...batchOption};
    },
    setMediaFileDetail(state, mediaFileDetail) {
      state.mediaFileDetail = mediaFileDetail;
    },
    setMediaFileRecheckRequest(state, mediaFileRecheckRequest) {
      state.mediaFileRecheckRequest = mediaFileRecheckRequest;
    },
    setMediaFileRecheckRequestTotal(state, mediaFileRecheckRequestTotal) {
      state.mediaFileRecheckRequestTotal = mediaFileRecheckRequestTotal;
    },
    updateMediaFileRecheckRequestOption(state, mediaFileRecheckRequestOption) {
      state.mediaFileRecheckRequestOption =
        {...state.mediaFileRecheckRequestOption, ...mediaFileRecheckRequestOption};
    }
  },

  actions: {
    setAdminMain(context, newAdminMain) {
      context.commit("setAdminMain", newAdminMain);
    },
    getAdminMain() {
      return admin.adminMain()
        .then((response) => {
          console.log(response)
          //commit('setAdminMain', response.data.data.)
        })
    },
    // eslint-disable-next-line
    uploadNotice({ commit }, { noticeTitle, noticeContent }) {
      return admin.addNotice({ noticeTitle, noticeContent });
    },
    getNoticeList({ commit, state }) {
      const { page, size } = state.noticeOption;

      return admin.getNoticeList({ page, size })
      .then((response) => {
        commit('setNoticeList', response.data.data.list);
        commit('setNoticeTotal', response.data.data.total)
      })
    },
    getDailyReportAll({ commit }) {
      return admin.getDailyReportAll()
      .then((response) => {
        commit('setDailyReportAll', response.data.data);
      })
    },
    getAnalysisData({ commit }) {
      return admin.getAnalysisData()
        .then((response) => {
          commit('setAnalysisData', response.data.data)
        })
    },
    getContentList({ commit, state }) {
      const { page, size } = state.contentOption;

      return admin.getContentList({ page, size })
        .then((response) => {
          commit('setContentList', response.data.data.list);
          commit('setContentTotal', response.data.data.total)
        })
    },
    getMemberList({ commit, state }) {
      const { page, size } = state.memberOption;

      return admin.getMemberList({ page, size })
        .then((response) => {
          console.log(response);
          commit('setMemberList', response.data.data.list);
          commit('setMemberTotal', response.data.data.total)
        })
    },
    getRecentBatch({ commit }) {
      return admin.getRecentBatch()
        .then((response) => {
          commit('setRecentBatch', response.data.data);
        })
    },
    getBatchDetail({ commit, state }) {
      const { jobName, page, size } = state.batchOption;

      return admin.getBatchDetail({ jobName, page, size })
        .then((response) => {
          commit('setBatchDetail', response.data.data);
        })
    },
    getMediaFileDetail({ commit }, mediaFileId) {
      return admin.getMediaFileDetail(mediaFileId)
        .then((response) => {
          commit('setMediaFileDetail', response.data.data);
        })
    },
    // eslint-disable-next-line
    uploadMediaFileRecheckRequest({ commit }, { mediaFileId, mediaFileRecheckRequestClaim }) {
      return admin.postMediaFileRecheckRequest({ mediaFileId, mediaFileRecheckRequestClaim });
    },
    getMediaFileRecheckRequest({ commit, state }) {
      const { mediaFileId, mediaFileRecheckRequestStatus, page, size } = state.mediaFileRecheckRequestOption;

      return admin.getMediaFileRecheckRequest({ mediaFileId, mediaFileRecheckRequestStatus, page, size })
        .then((response) => {
          commit('setMediaFileRecheckRequest', response.data.data.list);
          commit('setMediaFileRecheckRequestTotal', response.data.data.total)
        })
    },
  }
}
