import apiFactory from "./apiFactory"
import store from '../store'

const axios = apiFactory.getInstance();

export default {
  //회원별 알림 목록 조회
  getNotifyList() {
    return axios.get('/notification',
      {
        headers: {
          Authorization: `Bearer ${store.getters['auth/accessToken']}`
        }
      });
  },

  // 읽지 않은 알림 개수 (footer 빨간색)
  UnreadNotify() {
    return axios.get('/notification/unreadCount',
      {
        headers: {
          Authorization: `Bearer ${store.getters['auth/accessToken']}`
        }
      });
  },

  // 알림을 읽은 항목
  putNotify(notificationId, notificationIsRead) {
    console.log("알림번호" + notificationId);
    console.log("읽음확인" + notificationIsRead);
    return axios.put(`/notification/${notificationId}`, {
      notificationId,
      notificationIsRead
    }, {
      headers: {
        Authorization: `Bearer ${store.getters['auth/accessToken']}`
      }
    });
  }

}