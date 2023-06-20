import apiFactory from "./apiFactory"
import store from '../store'

const axios = apiFactory.getInstance();

export default {
    getRecordList( memberId, startDateStr, keyword, friendId ) {
        return axios.get('/record',
        {
            params: { memberId, startDateStr, keyword, friendId },
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },
    
    addReply( memberId, recordId, replyContent ) {
        return axios.post('/record/reply',
        {
            memberId, recordId, replyContent
        },
        {
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },

    editReply( memberId, recordId, replyId, replyContent ) {
        return axios.put(`/record/reply/${replyId}`,
        {
            memberId, recordId, replyId, replyContent
        },
        {
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },

    deleteReply( recordId, replyId, memberId ) {
        console.log(memberId);
        return axios.delete(`/record/reply/${replyId}`,
        {
            data: {
                recordId,
                replyId,
                memberId
              },
            headers: {
                Authorization: `Bearer ${store.getters['auth/accessToken']}`
            }
        });
    },

    postRecord(formData) {
        return axios.post('/record', formData);
    },

    updateRecord(recordId, recordComment, memberId) {
        return axios.put(`/record/${recordId}`, {
            memberId,
            recordId,
            recordComment
        });
    },

    deleteRecord(recordId, memberId){
        return axios.delete(`/record/${recordId}`,
        {
            data: {
                memberId,
                recordId
            }
        });
    },

    getFriendListByRecord({ memberId }) {
        return axios.get('/record/friend', {
            memberId
        })
    },

    getUnacceptedRecordList(memberId) {
        return axios.get('/record/unaccepted',
        {
            params: { memberId }
        });
    },

    updateUnaccepted(recordId, recordShareTo) {
        return axios.put(`/record/unaccepted/${recordId}`, {
            recordId,
            recordShareTo
        })
    },

    getRecord( recordId ) {
        return axios.get(`/record/${recordId}`);
    }
};