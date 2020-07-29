package validation

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
)

func userIDExists(ctx context.Context, ac api.APIClient, userID string) bool {
	exists, err := ac.UserExists(ctx, userID)
	if err != nil {
		return false
	}

	return exists
}
