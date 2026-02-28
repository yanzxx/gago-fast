export const toFarmTreeSelectData = (nodes = []) => {
	return (nodes || []).map((node) => ({
		title: node?.name || node?.title || '',
		value: node?.id || node?.value || '',
		key: node?.id || node?.value || '',
		children: toFarmTreeSelectData(node?.children || [])
	}))
}

export const flattenFarmTreeOptions = (tree = [], collector = []) => {
	(tree || []).forEach((node) => {
		if (!node?.value) return
		collector.push({ label: node?.title || node?.label || '', value: node.value })
		flattenFarmTreeOptions(node?.children || [], collector)
	})
	return collector
}

export const findFarmTreeNode = (tree = [], value) => {
	for (const node of tree || []) {
		if (node?.value === value) return node
		const found = findFarmTreeNode(node?.children || [], value)
		if (found) return found
	}
	return null
}

