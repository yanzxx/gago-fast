<template>
	<div class="container">
    <van-form @submit="submit" label-align="top" ref="formRef">

      <van-field
          v-model="formData.orgName"
          is-link
          readonly
          name="orgName"
          label="所属机构"
          placeholder="请选择所属机构"
          required
          :rules="[{ required: true, message: '请选择所属机构' }]"
          @click="selectPreOrg"
      />

      <snowy-org-picker ref="snowyOrgPickerRef" v-model="formData.orgName" @confirm="confirmOrg" placeholder="请选择所属机构">
      </snowy-org-picker>

      <van-field
          v-model="formData.name"
          name="name"
          label="岗位名称"
          placeholder="请输入岗位名称"
          required
          :rules="[{ required: true, message: '请输入岗位名称' }]"
      />

      <van-field
          v-model="formData.categoryName"
          is-link
          readonly
          name="categoryName"
          label="岗位分类"
          placeholder="请选择岗位分类"
          required
          :rules="[{ required: true, message: '请选择机构分类' }]"
          @click="selectPreSel"
      />

      <snowy-sel-picker ref="snowySelPickerRef" :map="{key: 'value', label: 'text'}" v-model="formData.categoryName"
                        :rangeData="positionCategoryOptions" @confirm="confirmSel" placeholder="请选择岗位分类"></snowy-sel-picker>

      <van-field name="sortCode" label="排序" required :rules="[{ required: true, message: '请选择排序' }]">
        <template #input>
          <van-stepper v-model="formData.sortCode" background="#2979FF" color="#fff" :step="1" :max="100" />
        </template>
      </van-field>

      <div style="margin: 16px;">
        <van-button block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
	</div>
</template>

<script setup>
	import { positionDetail, submitForm } from '@/api/biz/bizPositionApi.js'
  import { orgTree } from '@/api/biz/bizOrgApi.js'
  import { ref, onMounted, defineEmits } from "vue";
  import SnowyOrgPicker from '@/components/snowy-org-picker.vue';
  import SnowySelPicker from '@/components/snowy-sel-picker.vue';
  import tool from '@/plugins/tool';
  import router from '@/router';

  const formRef = ref()
  const snowyOrgPickerRef = ref()
  const snowySelPickerRef = ref()
  let formData = ref({
    sortCode: 99
  })
  const positionCategoryOptions = tool.dictList('POSITION_CATEGORY')

  const emits = defineEmits(['formBack'])

  onMounted(() => {
    const option = router.currentRoute.value.query
    if(!option.id){
      return
    }
    loadData(option)
  })

  const loadData = (option) => {
    let parentName = ''
    orgTree().then(res1 => {
      positionDetail({
        id: option.id
      }).then(res => {
        const category = res?.data.category
        const orgId = res?.data.orgId
        formData.value = res?.data
        formData.value.categoryName = positionCategoryOptions.filter(item => {
          return item.value == category
        })[0].text
        recursionGetParantName(res1.data, orgId)
        formData.value.orgName = parentName
      })
    })

    function recursionGetParantName(data, id) {
      for (let i = 0; i < data.length; i++) {
        const item = data[i]
        if(item.id == id) {
          parentName = item.name
          break
        } else {
          if (item.children) {
            recursionGetParantName(item.children, id)
          }
        }
      }
    }
  }

  const submit = () => {
    formRef.value.validate().then(() => {
      submitForm(formData.value, !formData.value.id).then(respond => {
        router.replace({
          path: '/position'
        })
        // emits('formBack', {
        //   data: respond.data
        // })
        // formData = ref({
        //   sortCode: 99
        // })
      })
    })
  }

  const selectPreOrg = () => {
    snowyOrgPickerRef.value.open()
  }

  const selectPreSel = () => {
    snowySelPickerRef.value.open()
  }

  const confirmOrg = (data) => {
    formData.value.orgId = data.curSelOrgId
    formData.value.orgName = data.curSelOrg.name
  }

  const confirmSel = (data) => {
    formData.value.category = data.curSelDataKey
    formData.value.categoryName = data.curSelData.text
  }

</script>

<style lang="scss">
.container {
  margin: 6px;
  border-radius: 5px;
  background-color: #fff;

  .btn-sub {
    background-color: #2979ff;
  }
}
</style>
