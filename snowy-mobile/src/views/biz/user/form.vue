<template>
  <div class="container">

    <van-tabs v-model:active="curView" type="card">
      <van-tab title="基础信息">
        <van-form label-align="top" ref="formRef">
          <van-field
              v-model="formData.account"
              name="account"
              label="账号"
              placeholder="请输入账号"
              required
              :rules="[{ required: true, message: '请输入账号' }]"
          />
          <van-field
              v-model="formData.name"
              name="name"
              label="姓名"
              placeholder="请输入姓名"
              required
              :rules="[{ required: true, message: '请输入姓名' }]"
          />
          <van-field name="gender" label="单选框">
            <template #input>
              <van-radio-group v-model="formData.gender" direction="horizontal">
                <van-radio name="1">男</van-radio>
                <van-radio name="2">女</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field
              v-model="formData.nickname"
              name="nickname"
              label="昵称"
              placeholder="请输入昵称"
              required
              :rules="[{ required: true, message: '请输入昵称' }]"
          />
          <van-field
              v-model="formData.phone"
              name="phone"
              label="手机号"
              placeholder="请输入手机号"
          />
          <van-field
              v-model="formData.email"
              name="email"
              label="邮箱"
              placeholder="请输入邮箱"
          />

          <van-field
              v-model="formData.birthday"
              is-link
              readonly
              name="calendar"
              label="出生日期"
              placeholder="点击选择出生日期"
              @click="showCalendar = true"
          />

          <van-action-sheet v-model:show="showCalendar" title="出生日期">
            <Calendar
                backgroundText
                :selectMode="'select'"
                class-name="select-mode"
                :select-date="selectModeDate"
                completion="true"
                @onSelect="onConfirm1"
            />
          </van-action-sheet>

          <van-field
              v-model="formData.orgName"
              is-link
              readonly
              name="orgName"
              label="选择组织"
              placeholder="选择组织"
              required
              :rules="[{ required: true, message: '请选择所属机构' }]"
              @click="selectPreOrg"
          />

          <snowy-org-picker ref="snowyOrgPickerRef" v-model="formData.orgName" @confirm="confirmOrg"
                            placeholder="请选择组织">
          </snowy-org-picker>

          <van-field
              v-model="formData.positionName"
              is-link
              readonly
              name="positionName"
              label="选择职位"
              placeholder="请选择职位"
              required
              :rules="[{ required: true, message: '请选择机构分类' }]"
              @click="selectPreSel"
          />

          <snowy-sel-picker ref="snowySelPickerRef" :map="{key: 'id', label: 'name'}" v-model="formData.positionName"
                            :rangeData="positionData" @confirm="confirmSel" placeholder="请选择职位"></snowy-sel-picker>

          <van-field
              v-model="formData.directorName"
              is-link
              readonly
              name="directorName"
              label="指定主管"
              placeholder="请选择主管"
              @click="selectPreUser"
          />

          <snowy-user-picker ref="snowyUserPickerRef" v-model="formData.directorName" @confirm="confirmUser"
                             placeholder="请选择主管">
          </snowy-user-picker>

          <van-field
              v-model="formData.empNo"
              name="empNo"
              label="员工编号"
              placeholder="请输入员工编号"
          />

          <van-field
              v-model="formData.positionLevel"
              name="positionLevel"
              label="职位级别"
              placeholder="请输入职位级别"
          />

          <van-field
              v-model="formData.entryDate"
              is-link
              readonly
              name="entryDate"
              label="入职日期"
              placeholder="点击选择入职日期"
              @click="showCalendar1 = true"
          />

          <van-action-sheet v-model:show="showCalendar1" title="入职日期">
            <Calendar
                backgroundText
                :selectMode="'select'"
                class-name="select-mode"
                :select-date="selectModeDate"
                completion="true"
                @onSelect="onConfirm2"
            />
          </van-action-sheet>

<!--          <formPosition v-model="formData.positionJson" ref="positionJsonRef"></formPosition>-->

        </van-form>
      </van-tab>
      <van-tab title="更多信息">
        <van-form label-align="top">
          <van-field
              v-model="formData.nation"
              is-link
              readonly
              name="categoryName"
              label="民族"
              placeholder="请选择民族"
              @click="selectPreSelNation"
          />

          <snowy-sel-picker ref="snowySelPickerNationRef" :map="{key: 'value', label: 'text'}" v-model="formData.nation"
                            :rangeData="nationOptions" @confirm="confirmSelNation" placeholder="请选择民族"></snowy-sel-picker>

          <van-field
              v-model="formData.nativePlace"
              name="nativePlace"
              label="籍贯"
              placeholder="请输入籍贯"
          />

          <van-field
              v-model="formData.nativePlace"
              name="nativePlace"
              label="家庭住址"
              rows="2"
              autosize
              type="textarea"
              placeholder="请输入家庭住址"
          />

          <van-field
              v-model="formData.mailingAddress"
              name="mailingAddress"
              label="家庭住址"
              rows="2"
              autosize
              type="textarea"
              placeholder="请输入家庭住址"
          />

          <van-field
              v-model="formData.idCardType"
              is-link
              readonly
              name="idCardType"
              label="证件类型"
              placeholder="请选择证件类型"
              @click="selectPreSelIdCardType"
          />

          <snowy-sel-picker ref="snowySelPickerIdCardTypeRef" :map="{key: 'value', label: 'text'}" v-model="formData.idCardType"
                            :rangeData="idcardTypeOptions" @confirm="confirmSelIdCardType"
                            placeholder="请选择证件类型"></snowy-sel-picker>

          <van-field
              v-model="formData.idCardNumber"
              name="idCardNumber"
              label="证件号码"
              placeholder="请输入证件号码"
          />

          <van-field
              v-model="formData.cultureLevel"
              is-link
              readonly
              name="cultureLevel"
              label="文化程度"
              placeholder="请选择文化程度"
              @click="selectPreSelCultureLevel"
          />

          <snowy-sel-picker ref="snowySelPickerCultureLevelRef" :map="{key: 'value', label: 'text'}" v-model="formData.cultureLevel"
                            :rangeData="cultureLevelOptions" @confirm="confirmSelCultureLevel"
                            placeholder="请选择文化程度"></snowy-sel-picker>

          <van-field
              v-model="formData.politicalOutlook"
              name="politicalOutlook"
              label="政治面貌"
              placeholder="请输入政治面貌"
          />

          <van-field
              v-model="formData.college"
              name="college"
              label="毕业学校"
              placeholder="请输入毕业学校"
          />

          <van-field
              v-model="formData.education"
              name="education"
              label="学历"
              placeholder="请输入学历"
          />

          <van-field
              v-model="formData.eduLength"
              name="eduLength"
              label="学制"
              placeholder="请输入学制"
          />

          <van-field
              v-model="formData.degree"
              name="degree"
              label="学位"
              placeholder="请输入学位"
          />

          <van-field
              v-model="formData.homeTel"
              name="homeTel"
              label="家庭电话"
              placeholder="请输入家庭电话"
          />

          <van-field
              v-model="formData.officeTel"
              name="officeTel"
              label="办公电话"
              placeholder="请输入办公电话"
          />

          <van-field
              v-model="formData.emergencyContact"
              name="emergencyContact"
              label="紧急联系人"
              placeholder="请输入紧急联系人"
          />

          <van-field
              v-model="formData.emergencyPhone"
              name="emergencyPhone"
              label="紧急联系电话"
              placeholder="请输入紧急联系电话"
          />

          <van-field
              v-model="formData.emergencyAddress"
              name="emergencyAddress"
              label="紧急联系人地址"
              rows="2"
              autosize
              type="textarea"
              placeholder="请输入紧急联系人地址"
          />

        </van-form>
      </van-tab>
    </van-tabs>

    <van-button type="primary" @click="submit" style="margin-top: 10px;width: 100%;">提交</van-button>
  </div>
</template>

<script setup>
import {
  onMounted,
  reactive,
  ref
} from "vue"
import tool from '@/plugins/tool.js'
import {
  userDetail,
  userPositionSelector,
  submitForm,
} from '@/api/biz/bizUserApi.js'
import SnowyOrgPicker from '@/components/snowy-org-picker.vue'
import SnowyUserPicker from '@/components/snowy-user-picker.vue'
import SnowySelPicker from '@/components/snowy-sel-picker.vue'
// import formPosition from './form-position.vue'
import XEUtils from "xe-utils"
import router from '@/router'
import Calendar from "mpvue-calendar"

const currentDate = new Date()
const currentYear = currentDate.getFullYear()
const currentMonth = currentDate.getMonth() + 1
const currentDay = currentDate.getDate()

const selectModeDate = ref(`${currentYear}-${currentMonth}-${currentDay}`)

const curView = ref(0)
const formRef = ref()
const showCalendar = ref(false)
const showCalendar1 = ref(false)
// 表单数据
let formData = ref({})
// 职位
const positionData = ref([])
const snowyOrgPickerRef = ref()
const snowyUserPickerRef = ref()
const snowySelPickerRef = ref()
// 民族
const nationOptions = tool.dictList('NATION')
const snowySelPickerNationRef = ref()
// 身份证件
const idcardTypeOptions = tool.dictList('IDCARD_TYPE')
const snowySelPickerIdCardTypeRef = ref()
// 文化程度
const cultureLevelOptions = tool.dictList('CULTURE_LEVEL')
const snowySelPickerCultureLevelRef = ref()
// 职位
// const positionJsonRef = ref()

onMounted(() => {
  const option = router.currentRoute.value.query
  if (!option.id) {
    return
  }
  userDetail({
    id: option.id
  }).then(res => {
    console.log('res ===>', res)
    formData.value = res?.data
    if (!formData.value.orgId) {
      return
    }
    positionParam.orgId = formData.value.orgId
    loadPositionSelector(true)
    // directorRef.value.loadUserData(true, {orgId: formData.value.orgId})
  })
})

const onConfirm1 = (date) => {
  formData.value.birthday = date;
  showCalendar.value = false;
};

const onConfirm2 = (date) => {
  formData.value.entryDate = date;
  showCalendar1.value = false;
};

const selectPreOrg = () => {
  snowyOrgPickerRef.value.open()
}

const selectPreUser = () => {
  snowyUserPickerRef.value.open()
}

const selectPreSel = () => {
  snowySelPickerRef.value.open()
}

const selectPreSelNation = () => {
  snowySelPickerNationRef.value.open()
}

const selectPreSelIdCardType = () => {
  snowySelPickerIdCardTypeRef.value.open()
}

const selectPreSelCultureLevel = () => {
  snowySelPickerCultureLevelRef.value.open()
}

const confirmUser = (data) => {
  formData.value.directorId = data.curSelUserId
  formData.value.directorName = data.curSelUser.name
}

const confirmSel = (data) => {
  formData.value.positionId = data.curSelDataKey
  formData.value.positionName = data.curSelData.name

  // if(!XEUtils.isEmpty(data.curSelDataKey)){
  //   getPositionListByIdList({
  //     idList: [data.curSelDataKey]
  //   }).then(res => {
  //     callback(res.data[0])
  //   })
  // }
}

const confirmSelNation = (data) => {
  formData.value.nation = data.curSelDataKey
}

const confirmSelIdCardType = (data) => {
  formData.value.idCardType = data.curSelDataKey
}

const confirmSelCultureLevel = (data) => {
  formData.value.cultureLevel = data.curSelDataKey
}

const confirmOrg = (data) => {
  formData.value.orgId = data.curSelOrgId
  formData.value.orgName = data.curSelOrg.name

  formData.value.positionId = null
  positionParam.orgId = data.curSelOrgId
  loadPositionSelector(true)
  formData.value.directorId = null
  // directorRef.value.loadUserData(true, {orgId: data.curSelOrgId})
}

// 职位参数
const positionParam = reactive({
  current: 1,
  size: 10
})
// 职位分页加载
const loadPositionSelector = (isReset) => {
  if (isReset) {
    positionParam.current = 1
    positionData.value = []
  }
  userPositionSelector(positionParam).then(res => {
    if (XEUtils.isEmpty(res?.data)) {
      return
    }
    positionData.value = positionData.value.concat(res.data)
    positionParam.current++
  })
}

const submit = () => {
  // // 子表单数据给父表单数据赋值，并校验子表单数据
  // positionJsonRef.value.formListEmitAndValidate().then(() => {
  //   // 父表单校验
  //   formRef.value.validate().then(() => {
  //     submitForm(formData.value, !formData.value.id).then(() => {
  //
  //     })
  //   }).catch(err => {
  //     console.error('父表单错误信息：', err);
  //   })
  // }).catch(err => {
  //   console.error('子表单错误信息：', err);
  // })

  formRef.value.validate().then(() => {
    submitForm(formData.value, !formData.value.id).then(() => {
      router.replace({
        name: 'user'
      })
    })
  }).catch(err => {
    console.error('父表单错误信息：', err);
  })
}
</script>

<style lang="scss" scoped>
.container {
  margin: 6px;
  border-radius: 5px;
  background-color: #fff;
  padding: 6px;
  margin-bottom: 45px;

  .mpvue-calendar {
    position: relative;
    width: 96%;
    margin-left: 2%;
  }
}
</style>
