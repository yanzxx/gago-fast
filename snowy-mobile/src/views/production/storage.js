const RECORD_KEY = 'production_collar_records_v1'
const EXPORT_KEY = 'production_export_tasks_v1'

const nowText = () => {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${hh}:${mm}:${ss}`
}

const id = () => `${Date.now()}${Math.floor(Math.random() * 1000)}`

const load = (key) => {
  try {
    const text = localStorage.getItem(key)
    return text ? JSON.parse(text) : []
  } catch (e) {
    return []
  }
}

const save = (key, data) => localStorage.setItem(key, JSON.stringify(data || []))

export const listRecords = () => load(RECORD_KEY)

export const saveRecord = (record) => {
  const all = listRecords()
  all.unshift({ ...record, id: record.id || id(), createTime: record.createTime || nowText() })
  save(RECORD_KEY, all)
}

export const saveBatch = (records = []) => {
  if (!records.length) return
  const all = listRecords()
  const mapped = records.map(item => ({ ...item, id: id(), createTime: nowText() }))
  save(RECORD_KEY, [...mapped, ...all])
}

export const existsCode = (code) => {
  if (!code) return false
  const all = listRecords()
  return all.some(item => String(item.collarCode || '').trim() === String(code).trim())
}

export const getRecord = (recordId) => listRecords().find(item => String(item.id) === String(recordId))

export const listExportTasks = () => load(EXPORT_KEY)

export const createExportTask = (payload = {}) => {
  const tasks = listExportTasks()
  const fileName = payload.fileName || `${payload.name || `项圈登记导出_${Date.now()}`}.csv`
  const task = {
    id: id(),
    name: payload.name || `项圈登记导出_${Date.now()}`,
    status: 'DONE',
    createTime: nowText(),
    filter: payload.filter || {},
    downloadUrl: payload.downloadUrl || '#',
    fileName,
    content: payload.content || '',
    total: payload.total || 0
  }
  tasks.unshift(task)
  save(EXPORT_KEY, tasks)
  return task
}

export const parseBatchText = (text) => {
  const rows = String(text || '').split('\n').map(v => v.trim()).filter(Boolean)
  return rows.map((line, idx) => {
    const parts = line.replace(/，/g, ',').split(',').map(v => v.trim())
    return {
      rowNo: idx + 1,
      collarCode: parts[0] || '',
      camelTag: parts[1] || '',
      orgName: parts[2] || '',
      registerDate: parts[3] || ''
    }
  })
}

export const buildCodeByRule = (prefix = 'XC') => {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const rand = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  return `${prefix}${y}${m}${day}${rand}`
}

const escapeCsv = (value) => {
  const text = String(value ?? '')
  if (text.includes('"') || text.includes(',') || text.includes('\n')) {
    return `"${text.replace(/"/g, '""')}"`
  }
  return text
}

export const buildRecordCsv = (records = []) => {
  const header = ['项圈编号', '骆驼标识', '养殖场', '登记日期', '状态', '备注', '创建时间']
  const lines = records.map((item) => {
    return [
      escapeCsv(item.collarCode || item.collarNo || ''),
      escapeCsv(item.camelTag || item.speciesName || ''),
      escapeCsv(item.orgName || ''),
      escapeCsv(item.registerDate || ''),
      escapeCsv(item.status || ''),
      escapeCsv(item.remark || ''),
      escapeCsv(item.createTime || '')
    ].join(',')
  })
  return [header.join(','), ...lines].join('\n')
}

export const downloadExportTask = (taskId) => {
  const task = listExportTasks().find((item) => String(item.id) === String(taskId))
  if (!task || !task.content) return false
  const blob = new Blob(['\ufeff' + task.content], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = task.fileName || `${task.name || '导出数据'}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  return true
}
