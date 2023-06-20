import axios from "axios";

export default {
    kakaoLocalSearch(query) {
        return axios.get('https://dapi.kakao.com/v2/local/search/keyword.json',
        {
            params: { query },
            headers: {
                Authorization: `KakaoAK ${process.env.VUE_APP_KAKAO_REST_KEY}`
            }
        });
    }
}