package common

// RemoveString - スライスの中身削除
func RemoveString(strings []string, search string) []string {
	result := []string{}
	for _, v := range strings {
		if v != search {
			result = append(result, v)
		}
	}

	return result
}

// RemoveInt - スライスの中身削除
func RemoveInt(ints []int, search int) []int {
	result := []int{}
	for _, v := range ints {
		if v != search {
			result = append(result, v)
		}
	}

	return result
}
