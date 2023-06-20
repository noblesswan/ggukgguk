import { auth } from '../api';

export default {
  namespaced: true,
  state: {
    memberInfo: {},
    accessToken: '',
    refreshToken: '',
  },
  getters: {
    memberInfo(state) {
      return state.memberInfo;
    },
    accessToken(state) {
      return state.accessToken;
    },
    refreshToken(state) {
      return state.refreshToken;
    },
    isLogin(state) {
      if (Object.keys(state.memberInfo).length === 0
        || state.accessToken === ''
        || state.refreshToken === '') {
        return false;
      }
      return true;
    }
  },
  mutations: {
    setMemberInfo(state, {
      memberInfo, accessToken, refreshToken,
    }) {
      state.memberInfo = memberInfo;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
    },
    updateAccessToken(state, { accessToken }) {
      state.accessToken = accessToken;
    },
    updateMemberAllowEmail(state, isAllow) {
      state.memberInfo.memberAllowEmail = isAllow;
    },
    updateMember(state, memberInfo){
      state.memberInfo = memberInfo; 
      state.memberInfo.memberPw = '';
    }
  },
  actions: {
    login({ commit }, { memberId, memberPw }) {
      return auth.login({ memberId, memberPw })
        .then((response) => {
          console.log('로그인 완료');
          console.log(response);
          commit('setMemberInfo', response.data.data);
          return response;
        })
    },
    logout({ commit }) {
      commit('setMemberInfo', {
        memberInfo: {},
        accessToken: '',
        refreshToken: ''
      });
    },
    // 회원 가입
    // eslint-disable-next-line
    register({ }, { memberId, memberPw, memberName, memberNickname,
      memberEmail, memberPhone, memberBirth, memberAuthority }) {
      console.log(memberId);
      return auth.addMember({
        memberId, memberPw, memberName, memberNickname,
        memberEmail, memberPhone, memberBirth, memberAuthority
      });
    },

    // 아이디 중복 가입
    // eslint-disable-next-line
    duplicateId({ }, memberId) {
      return auth.duplicateId(memberId)
        .then((response) => {
          console.log('리스폰스 받음');
          console.log(response);
          return response;
        })
    },

    // 카카오 인가코드 전달.
    handleKakaoAuth({ commit }, AccessToken) {
      // console.log("테스트 :" + code);
      return auth.directKakaoAuth(AccessToken).then((response) => {
        console.log('리스폰스 받음');
        console.log(response);
        commit('setMemberInfo', response.data.data);
        return response;
      });
    },

    // 구글 로그인 
    // eslint-disable-next-line
    handleGoogleAuth({ commit }, token) {
      return auth.directGoogleUrl(token).then((response) => {
        console.log(response);
        commit('setMemberInfo', response.data.data);
        return response
      })
    },

    // 이메일로 아이디찾기
    // eslint-disable-next-line
    handleFindId({ }, email) {
      return auth.findIdByEmail(email)
        .then((response) => {
          console.log('리스폰스 받음');
          console.log(response);
          return response;
        })
    },

    // 아이디와 이메일로 비밀번호 찾기
    // eslint-disable-next-line
    handleFindPw({ }, { memberEmail, memberId }) {
      return auth.findPwByIdEmail({ memberEmail, memberId })
        .then((response) => {
          console.log('리스폰스 받음');
          console.log(response);
          return response;
        })
    },

    // 메일 인증번호 요청하기 
    // eslint-disable-next-line
    handleCertification({ }, { sendTo }) {
      return auth.requestAuthenticationCode({ sendTo })
        .then((response) => {
          console.log('storehandleCertification 처리');
          console.log(response);
          return response;
        })
    },

    // 비밀번호 찾기시 메일 인증번호 요청하기 
    // eslint-disable-next-line
    handleCertificationPw({ }, { sendTo }) {
      return auth.requestPwAuthenticationCode({ sendTo })
        .then((response) => {
          console.log('storehandleCertification 처리');
          console.log(response);
          return response;
        })
    },
  }
};