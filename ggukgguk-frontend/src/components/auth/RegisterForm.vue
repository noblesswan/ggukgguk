<script setup>
import { watch,onMounted, ref } from "vue";
import { useStore } from "vuex";
import { auth } from '../../api';
import { useRouter } from "vue-router";
import axios from 'axios'

const store = useStore();
const router = useRouter();

// 변수 초기화
const memberId = ref("");
const memberPw = ref("");
const memberName = ref("");
const memberNickname = ref("");
const memberEmail = ref("");
const memberPhone = ref("");
const memberBirth = ref("");
const memberAuthority = "NORMAL";
const duplicated = ref(false); // 아이디 중복값 확인
const appearCertification = ref(false);  // 인증 번호 창 출력 
const certificationNumber = ref(""); // 인증번호 조회
const memberAllowEmail = ref(false); // 메일 인증 여부
const IdRules_duplited = ref(false);
const is_vaild = ref(false);

// 각 요소별로 입력 조건 명시
// 정규식 패턴 게시글 참고 
// https://xively.tistory.com/62
const IdRules = [
  (value) => {
    return (
      (!!value || "아이디를 입력해주세요.") &&
      ((value && value.length <= 16) || "16자 이내로 입력하세요.") &&
      (!IdRules_duplited.value || "아이디가 중복되었습니다. 다시 작성해주세요")
    );
  }
];

const EmailRules = [
  (value) => {
    if (value.length > 0) {
      //@와 .이 들어갔는지, 그리고 .뒤에 영어가 2자리 이상인지 체크한다.
      const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return pattern.test(value) || '이메일 형식이 옳지 않습니다.';
    }
  }
];

const phoneRules = [
  (value) => {
    return (
      (!!value || "휴대폰번호를 입력해주세요.") &&
      ((value && /^[0-9]+$/.test(value)) || "숫자만 입력해주세요")
    );
  }
]
const passwordRules = [
  (value) => {
    return (
      (!!value || "비밀번호를 입력해주세요.") &&
      ((value && value.length >= 8) || "8자 이상으로 입력하세요.") &&
      ((value && /\d/.test(value)) || "숫자를 최소 하나 이상 포함해주세요.") &&
      ((value && /[A-Z]/i.test(value)) || "영문자를 최소 하나 이상 포함해주세요.") &&
      ((value && /[!@#$%^&*]/.test(value)) || "특수문자를 최소 하나 이상 포함해주세요.")
    );
  },
];

const BirthRules = [
  (value) => {
    return (
      (!!value || "생년월일이 비워져있습니다.") &&
      ((value && /^\d{4}-\d{2}-\d{2}$/.test(value)) || 'YYYY-MM-DD 형식으로 입력해주세요.')
    );
  },
];


async function totalCheck() {
  if (memberAllowEmail.value == true && duplicated.value == true
    && memberPw.value != null && serviceAgree.value == true
    && personalinformationAgree.value == true) {
    is_vaild.value = true;
  } else {
    is_vaild.value = false;
  }
}

// watch(memberId, (newVal,oldVal) => {
// })

// 회원 가입 메서드 수행
function register() {
  totalCheck()// 통합적인 가입 필수 요건이 지켜는지 확인
  if (is_vaild.value != false) {
    console.log("form.vue 회원 아이디 : " + memberId.value);
    store
      .dispatch("auth/register", {
        memberId: memberId.value,
        memberPw: memberPw.value,
        memberName: memberName.value,
        memberNickname: memberNickname.value,
        memberEmail: memberEmail.value,
        memberPhone: memberPhone.value,
        memberBirth: memberBirth.value,
        memberAuthority: memberAuthority,
      })
      .then((response) => {
        alert("회원 가입에 성공했습니다.등록한 아이디로 다시 로그인 해주세요.");
        console.log("성공");
        console.log(response);
        router.push({ name: "login" }); // 성공시 로그인 url로 이동.
      })
      .catch((error) => {
        console.error("실패");
        console.error(error);
      });
  } else {
    alert('꾹꾹 페이지의 회원 가입 요건을 충족되지 않았습니다. 다시 확인해 주세요.')
  }
}

// 아이디 중복 검사.
function checkDuplicateId() {
  if (memberId.value != '') {
    store.dispatch("auth/duplicateId", memberId.value).then((response) => {
      console.log("검색 성공");
      console.log(response);
      if (response.data.status == "success") {
        alert("이미 가입된 아이디가 있습니다. 다시 작성해주세요");
        IdRules_duplited.value = true;
        memberId.value = "";
      } else {
        alert("가입이 가능합니다.");
        IdRules_duplited.value = false;
        duplicated.value = true;
      }
    });
  } else {
    alert("아이디를 작성하지 않았습니다.");
  }
}
// 메일 인증코드 요청.
function methodToExecuteWhenTemplateAppears() {
  if (memberEmail.value != '') {
    store.dispatch('auth/handleCertification', {
      sendTo: memberEmail.value
    }).then((response) => {
      appearCertification.value = true;
      alert("해당 메일에 인증 코드를 전송하였습니다.")
      console.log("인증번호 전달 완료")
      console.log(response);
    }).catch(() => {
      alert("이미 가입되어 있는 이메일입니다. 다른 이메일을 작성해주세요. ")
      memberEmail.value = '';
    });
  } else {
    alert("이메일을 작성하지 않았습니다.");
  }
}

// 인증번호 확인
function cetificationCheck() {
  return auth.findAuthenticationCode({
    sendTo: memberEmail.value,
    certificationNumber: certificationNumber.value
  }).then((response) => {
    console.log("인증번호 일치")
    console.log(response);
    memberAllowEmail.value = true; // 메일 인증 성공.
    alert('인증번호가 일치하여 계속해서 회원 가입을 진행해 주세요.');
    return response;
  }).catch(() => {
    alert('인증번호가 일치하지 않습니다. 다시 인증해주세요');
  });
}

// 꾹꾹 서비스 이용 동의 및 개인 정보 동의 여부 
const serviceAgree = ref(false);
const personalinformationAgree = ref(false);

function showContentDetailDiag() {
  termOfServicetermsDiagVisible.value = true;
}
function showPersonalInfoDiag() {
  personalInformationDiagVisible.value = true;
}

// 꾹꾹 서비스 이용 동의 및 개인 정보 다이어로그 표시여부
const termOfServicetermsDiagVisible = ref(false);
const personalInformationDiagVisible = ref(false);


// 개인정보처리 서비스 및 이용서비스 본문 내용
const serviceContent = ref('');
const prsonalInfoContent = ref('');


// 이용약관 본문 내용 가져오기
const termOfServiceContent = () => {
  axios.get('/subscriptionTerms/register.txt').then((response) => {
    serviceContent.value = response.data
  })
}
// 개인정보 동의 본문 내용 가져오기
const personalInfoServiceContent = () => {
  axios.get('/subscriptionTerms/personInfo.txt').then((response) => {
    prsonalInfoContent.value = response.data
  })
}

// 이용약관에 동의한 경우 
function termOfServiceAgreeDiag() {
  termOfServicetermsDiagVisible.value = false;
  serviceAgree.value = true;
}

// 개인정보 처리에 동의한 경우
function personalinfoServiceAgreeDiag() {
  personalInformationDiagVisible.value = false;
  personalinformationAgree.value = true;
}

onMounted(() => {
  alert("[알려드립니다] \n" +
    "메일 전송을 위해 사용하는 Amazon Simple Email Service(SES)가 " +
    "테스트 상태로 세팅되어 있기 때문에 allowList에 등록되지 않은 " +
    "이메일로는 메일 전송이 불가능합니다. 따라서 현재는 메일 인증을 이용한 " +
    "회원가입이 불가능합니다. \n" + 
    "심사위원께서는 소셜 로그인을 이용하여 회원 등록을 하시거나, " +  
    "QR코드와 함께 제공해드린 데모용 계정으로 로그인하실 수 있습니다. \n\n" +
    "이 메시지는 프로덕션 빌드에서는 표시되지 않습니다.");
  termOfServiceContent();
  personalInfoServiceContent();
});

// 회원ID값이 변화를 감지하여 빨간색글자로 '중복되었습니다 문구'를 나타내는 여부
// 사용자가 memberId 입력 필드에 새로운 값(빈 문자열이 아닌 값)을 입력하면 => 중복 경고 메시지를 숨김
watch(memberId, newValue => {
  if(newValue !== '') {
    IdRules_duplited.value = false;
  }
});


</script>
<template>
  <v-card-title class="text-center">회원가입</v-card-title>
  <v-sheet width="300" class="mx-auto">
    <form>
      <v-row>
        <v-col cols="10">
          <v-text-field v-model="memberId" :counter="16" :rules="IdRules" :success="!!memberId" label="id"></v-text-field>
          <!-- <v-container class="duplicate-indicator">
          {{
            dupblicated
              ? `${memberId} ID is not available.` 
              : ""
          }}
        </v-container> -->
        </v-col>
        <v-col cols="2">
          <v-btn class="check-duplicate" @click="checkDuplicateId">
            중복확인
          </v-btn>
        </v-col>
      </v-row>
      <v-text-field type="password" v-model="memberPw" :rules="passwordRules" :counter="128"
        label="비밀번호 입력"></v-text-field>

      <v-text-field v-model="memberName" :counter="16" label="이름"></v-text-field>

      <v-text-field v-model="memberNickname" label="닉네임"></v-text-field>
      <v-row>
        <v-col cols="10">
          <v-text-field v-model="memberEmail" label="이메일" :rules="EmailRules"></v-text-field>
        </v-col>
        <v-col cols="2">
          <v-btn @click="methodToExecuteWhenTemplateAppears">
            인증하기
          </v-btn>
        </v-col>
      </v-row>
      <v-form>
        <v-row v-if="appearCertification">
          <v-col cols="10">
            <v-text-field v-model="certificationNumber" label="인증번호 입력"></v-text-field>
          </v-col>
          <v-col cols="2">
            <v-btn @click="cetificationCheck">
              전송하기
            </v-btn>
          </v-col>
        </v-row>
      </v-form>
      <v-text-field v-model="memberPhone" :rules="phoneRules" label="휴대번호 입력"></v-text-field>

      <v-text-field v-model="memberBirth" :rules="BirthRules" label="생년월일"></v-text-field>

      <v-row>
        <v-col cols="10">
          <v-checkbox class="checked-agree" label="이용약관" v-model="serviceAgree" disabled></v-checkbox>
        </v-col>
        <v-col cols="2">
          <v-dialog v-model="termOfServicetermsDiagVisible" width="auto">
            <template v-slot:activator="{ props }">
              <v-btn color="primary" @click="showContentDetailDiag" v-bind="props">
                보기
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span class="text-h5">꾹꾹 서비스의 이용약관</span>
              </v-card-title>
              <v-card-text>
                <pre class="terms-text">{{ serviceContent }}</pre>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green-darken-1" variant="text" @click="termOfServicetermsDiagVisible = false">
                  거절
                </v-btn>
                <v-btn v-model="serviceAgree" color="green-darken-1" variant="text" @click="termOfServiceAgreeDiag">
                  동의
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10">
          <v-checkbox label="개인정보동의" v-model="personalinformationAgree" disabled></v-checkbox>
        </v-col>
        <v-col cols="2">
          <v-dialog v-model="personalInformationDiagVisible" width="auto">
            <template v-slot:activator="{ props }">
              <v-btn color="primary" @click="showPersonalInfoDiag" v-bind="props">
                보기
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span class="text-h5">개인정보동의</span>
              </v-card-title>
              <v-card-text>
                <pre class="terms-text">{{ prsonalInfoContent }}</pre>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="green-darken-1" variant="text" @click="personalInformationDiagVisible = false">
                  거절
                </v-btn>
                <v-btn v-model="serviceAgree" color="green-darken-1" variant="text" @click="personalinfoServiceAgreeDiag">
                  동의
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-col>
      </v-row>
      <!-- <v-checkbox value="1" label="이용약관" @click="showContentDetailDiag"></v-checkbox>
      <v-checkbox value="1" label="개인정보처리" type="checkbox"></v-checkbox> -->
      <v-col cols="auto">
        <v-btn size="large" class="register-btn" @click="register">등록</v-btn>
      </v-col>

    </form>
  </v-sheet>
</template>
<style scoped>
.terms-text {
  white-space: pre-wrap;
  /* 텍스트 형식 유지 */
  font-family: monospace;
  /* 고정폭 글꼴 */
}

.checked-agree {
  margin-bottom: -50px;
  /* 원하는 값으로 조절하세요. */
}

.register-btn {
  width: 500px;
  /* 원하는 값으로 조절하세요. */
}
</style>