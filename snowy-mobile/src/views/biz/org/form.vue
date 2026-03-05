<template>
	<div class="container">

    <van-form @submit="submit" label-align="top" ref="formRef">

      <van-field
          v-model="formData.parentName"
          is-link
          readonly
          name="parentName"
          label="上级机构"
          placeholder="请选择上级机构"
          required
          :rules="[{ required: true, message: '请选择上级机构' }]"
          @click="selectPreOrg"
      />

      <snowy-org-picker ref="snowyOrgPickerRef" v-model="formData.parentName" :isTopLevel="true" @confirm="confirmOrg" placeholder="请选择上级机构">
      </snowy-org-picker>

      <van-field
          v-model="formData.name"
          name="name"
          label="机构名称"
          placeholder="请输入机构名称"
          required
          :rules="[{ required: true, message: '请输入机构名称' }]"
      />

      <van-field
          v-model="formData.categoryName"
          is-link
          readonly
          name="categoryName"
          label="机构分类"
          placeholder="请选择机构分类"
          required
          :rules="[{ required: true, message: '请选择机构分类' }]"
          @click="selectPreSel"
      />

      <snowy-sel-picker ref="snowySelPickerRef" :map="{key: 'value', label: 'text'}" v-model="formData.categoryName"
                        :rangeData="orgCategoryOptions" @confirm="confirmSel" placeholder="请选择机构分类"></snowy-sel-picker>

      <van-field name="sortCode" label="排序" required :rules="[{ required: true, message: '请选择排序' }]">
        <template #input>
          <van-stepper v-model="formData.sortCode" background="#2979FF" color="#fff" :step="1" :max="100" />
        </template>
      </van-field>

      <van-field
          v-model="formData.directorName"
          is-link
          readonly
          name="directorName"
          label="指定主管"
          placeholder="请选择主管"
          @click="selectPreUser"
      />

      <snowy-user-picker ref="snowyUserPickerRef" v-model="formData.directorName" @confirm="confirmUser" placeholder="请选择主管">
      </snowy-user-picker>

      <div style="margin: 16px;">
        <van-button block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
	</div>
</template>

<script setup>
	import { ref, onMounted, defineEmits } from "vue";
	import SnowyOrgPicker from '@/components/snowy-org-picker.vue';
	import SnowySelPicker from '@/components/snowy-sel-picker.vue';
	import SnowyUserPicker from '@/components/snowy-user-picker.vue';
	import tool from '@/plugins/tool';
	import { orgDetail, submitForm, orgTree } from '@/api/biz/bizOrgApi.js';
  import router from '@/router';

	const formRef = ref()
  const snowyOrgPickerRef = ref()
  const snowySelPickerRef = ref()
  const snowyUserPickerRef = ref()
	let formData = ref({
		sortCode: 99
	})
	const orgCategoryOptions = tool.dictList('ORG_CATEGORY')

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
      // 含有顶级
      const treeData = [{
        id: '0',
        parentId: '-1',
        name: '顶级',
        children: res1.data
      }]

      orgDetail({
        id: option.id
      }).then(res => {
        const category = res?.data.category
        const parentId = res?.data.parentId
        formData.value = res?.data
        formData.value.categoryName = orgCategoryOptions.filter(item => {
          return item.value == category
        })[0].text
        recursionGetParantName(treeData, parentId)
        formData.value.parentName = parentName
      })
    })

    function recursionGetParantName(data, id) {
      for (let i = 0; i < data.length; i++) {
        const item = data[i]
        if(item.id == id) {
          console.log('item',item)
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
          path: '/org'
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

  const selectPreUser = () => {
    snowyUserPickerRef.value.open()
  }

  const confirmOrg = (data) => {
    formData.value.parentId = data.curSelOrgId
    formData.value.parentName = data.curSelOrg.name
  }

  const confirmSel = (data) => {
    formData.value.category = data.curSelDataKey
    formData.value.categoryName = data.curSelData.text
  }

  const confirmUser = (data) => {
    formData.value.directorId = data.curSelUserId
    formData.value.directorName = data.curSelUser.name
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
