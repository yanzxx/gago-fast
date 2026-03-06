<template>
  <div class="page">
    <div class="card">
      <van-form @submit="submit">
        <van-field label="编号模式" required>
          <template #input>
            <van-radio-group v-model="form.codeMode" direction="horizontal">
              <van-radio name="AUTO">自动</van-radio>
              <van-radio name="RULE">规则</van-radio>
              <van-radio name="MANUAL">手工</van-radio>
            </van-radio-group>
          </template>
        </van-field>

        <van-field v-model="form.collarCode" label="项圈编号" placeholder="请输入项圈编号" :readonly="form.codeMode !== 'MANUAL'" required />
        <van-field v-model="form.camelTag" label="骆驼标识" placeholder="请输入骆驼耳标/名称" required />
        <van-field
          v-model="form.orgName"
          label="养殖场名称"
          placeholder="请选择养殖场"
          is-link
          readonly
          required
          @click="openFarmSelector"
        />
        <van-field v-model="form.registerDate" label="登记日期" type="date" required />

        <van-field label="骆驼照片" required>
          <template #input>
            <div class="upload-card-wrap">
              <van-uploader
                v-model="camelFileList"
                :max-count="1"
                :preview-size="[96, 96]"
                accept="image/*"
                upload-icon="plus"
              >
                <div class="ant-upload-card">
                  <van-icon name="plus" size="18" />
                  <span>上传照片</span>
                </div>
              </van-uploader>
            </div>
          </template>
        </van-field>

        <van-field label="项圈照片" required>
          <template #input>
            <div class="upload-card-wrap">
              <van-uploader
                v-model="collarFileList"
                :max-count="1"
                :preview-size="[96, 96]"
                accept="image/*"
                upload-icon="plus"
              >
                <div class="ant-upload-card">
                  <van-icon name="plus" size="18" />
                  <span>上传照片</span>
                </div>
              </van-uploader>
            </div>
          </template>
        </van-field>

        <van-field v-model="form.remark" label="备注" type="textarea" maxlength="200" placeholder="选填" show-word-limit />

        <div class="btn-row">
          <van-button block type="primary" native-type="submit">提交登记</van-button>
        </div>
      </van-form>
    </div>

    <van-action-sheet
      v-model:show="showFarmSelector"
      :actions="farmActions"
      cancel-text="取消"
      close-on-click-action
      @select="onSelectFarm"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { showFailToast, showSuccessToast } from 'vant'
import { buildCodeByRule, existsCode, saveRecord } from './storage'
import { orgTree } from '@/api/biz/bizOrgApi'

const form = reactive({
  codeMode: 'AUTO',
  collarCode: buildCodeByRule('XC'),
  camelTag: '',
  orgId: '',
  orgName: '',
  registerDate: new Date().toISOString().slice(0, 10),
  camelPhoto: '',
  collarPhoto: '',
  remark: ''
})

const camelFileList = ref([])
const collarFileList = ref([])
const showFarmSelector = ref(false)
const farmActions = ref([])

const flattenFarmOptions = (nodes = [], collector = []) => {
  ;(nodes || []).forEach((node) => {
    const id = node?.id || node?.value
    const name = node?.name || node?.title
    if (id && name) {
      collector.push({
        name: String(name),
        value: String(id)
      })
    }
    if (node?.children?.length) {
      flattenFarmOptions(node.children, collector)
    }
  })
  return collector
}

const loadFarmOptions = async () => {
  try {
    const res = await orgTree()
    farmActions.value = flattenFarmOptions(res?.data || [])
    if (!form.orgId && farmActions.value.length) {
      form.orgId = farmActions.value[0].value
      form.orgName = farmActions.value[0].name
    }
  } catch (e) {
    farmActions.value = []
  }
}

watch(() => form.codeMode, (mode) => {
  if (mode === 'AUTO') form.collarCode = `AUTO${Date.now()}`
  if (mode === 'RULE') form.collarCode = buildCodeByRule('XC')
  if (mode === 'MANUAL') form.collarCode = ''
})

const getFileName = (item) => item?.file?.name || item?.url || ''
const openFarmSelector = () => {
  if (!farmActions.value.length) {
    showFailToast('暂无可选养殖场')
    return
  }
  showFarmSelector.value = true
}

const onSelectFarm = (action) => {
  form.orgId = action?.value || ''
  form.orgName = action?.name || ''
  showFarmSelector.value = false
}

const submit = () => {
  if (!form.collarCode || !form.camelTag || !form.orgName || !form.registerDate) {
    showFailToast('请完整填写必填项')
    return
  }
  if (!camelFileList.value.length || !collarFileList.value.length) {
    showFailToast('请上传骆驼照片和项圈照片')
    return
  }
  if (existsCode(form.collarCode)) {
    showFailToast(`编号重复：${form.collarCode}`)
    return
  }
  form.camelPhoto = getFileName(camelFileList.value[0])
  form.collarPhoto = getFileName(collarFileList.value[0])
  saveRecord({ ...form, status: '已登记' })
  showSuccessToast('登记成功')
  form.camelTag = ''
  form.camelPhoto = ''
  form.collarPhoto = ''
  form.remark = ''
  camelFileList.value = []
  collarFileList.value = []
  if (form.codeMode === 'AUTO') form.collarCode = `AUTO${Date.now()}`
  if (form.codeMode === 'RULE') form.collarCode = buildCodeByRule('XC')
  if (form.codeMode === 'MANUAL') form.collarCode = ''
}

onMounted(() => {
  loadFarmOptions()
})
</script>

<style scoped lang="scss">
.page {
  margin: 6px;
  --van-primary-color: #1f8a70;
  --van-button-primary-background: #1f8a70;
  --van-button-primary-border-color: #1f8a70;
  --van-radio-checked-icon-color: #1f8a70;
}
.card { background: #fff; border-radius: 10px; padding: 10px; }
.btn-row { margin-top: 14px; }

.upload-card-wrap {
  width: 100%;
}

.ant-upload-card {
  width: 96px;
  height: 96px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c8c8c;
  font-size: 12px;
  gap: 4px;
  background: #fafafa;
}

:deep(.van-uploader__upload) {
  width: 96px;
  height: 96px;
  margin: 0;
  border: none;
  background: transparent;
}

:deep(.van-uploader__preview) {
  margin: 0;
}
</style>
