import { diary } from '../api';

export default {
  namespaced: true,
  state: {
    diaryOption: {
      diaryYear: new Date().getFullYear(),
      diaryMonth: null
    },
    diaryList:[],
    colorList:[]
  },
  getters: {
    diaryOption(state) {
      return state.diaryOption
    },
    diaryList(state) {
      return state.diaryList
    },
    colorList(state) {
      return state.colorList
    }
  },
  mutations: {
    setDiaryYear(state, diaryYear){
      state.diaryOption.diaryYear = diaryYear;
      console.log(state.diaryOption.diaryYear);
    },
    setDiaryMonth(state, diaryMonth){
      console.log('2: ', diaryMonth);
      state.diaryOption.diaryMonth = diaryMonth;
    },
    setDiaryList(state, diaryList) {
      state.diaryList = diaryList;
    },
    setColorList(state, colorList) {
      state.colorList = colorList;
    }
  },
  actions: {
    // eslint-disable-next-line
    getDiaryList({ commit, state }, memberId) {
      console.log("3: ", state.diaryOption);
      return diary.getDiaryList(memberId, state.diaryOption.diaryYear, state.diaryOption.diaryMonth )
      .then((response) => {
        console.log(response.data.data);
        commit('setDiaryList', response.data.data);
      })
    },
    getColorList({ commit, state }) {
      console.log(state.diaryList.diaryId);
      return diary.getColorList(state.diaryList.diaryId)
      .then((response) => {
        console.log(response.data.data);
        commit('setColorList', response.data.data);
      })
    },
    updateMainColor({ state }, mainColor) {
      return diary.updateMainColor(state.diaryList.diaryId, mainColor)
      .then((response) => {
        console.log(response.data.data);
    })
    } 
  }
};