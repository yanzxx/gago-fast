import { showToast, closeToast, showDialog, closeDialog, showConfirmDialog, showLoadingToast  } from 'vant';
export default {
  // 消息提示
  msg(content) {
    showToast({
      title: content,
      icon: 'none'
    })
  },
  // 错误消息
  msgError(content) {
    showToast({
      title: content,
      icon: 'error',
    })
  },
  // 成功消息
  msgSuccess(content) {
    showToast({
      title: content,
      icon: 'success'
    })
  },
  // 隐藏消息
  hideMsg() {
    closeToast()
  },
  // 弹出提示
  alert(content, title) {
    showDialog({
      title: title || '警告提示',
      message: content,
    }).then(() => {
      closeDialog()
    });
  },
  // 确认窗体
  confirm(content) {
    return new Promise((resolve) => {
      showConfirmDialog({
        title: '系统提示',
        message: content,
      }) .then((res) => {
        resolve(res.confirm)
        closeDialog()
      }).catch(() => {
        closeDialog()
      });
    })
  },
  // 提示信息
  showToast(option) {
    if (typeof option === "object") {
      showToast(option)
    } else {
      showToast({
        title: option,
        icon: "none",
        duration: 2500
      })
    }
  },
  // 打开遮罩层
  loading(content) {
    showLoadingToast({
      message: content,
      forbidClick: true,
      loadingType: 'spinner',
    });
  },
  // 关闭遮罩层
  closeLoading() {
    closeToast()
  }
}
