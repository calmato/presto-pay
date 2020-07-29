package repository

import "strings"

const (
	// GroupCollection - GroupCollection名
	GroupCollection = "groups"
	// PaymentCollection - PaymentCollection名
	PaymentCollection = "payments"
)

func getGroupCollection() string {
	return GroupCollection
}

func getPaymentCollection(groupID string) string {
	return strings.Join([]string{GroupCollection, groupID, PaymentCollection}, "/")
}
