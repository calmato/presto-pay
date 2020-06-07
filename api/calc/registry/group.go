package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/application"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/service"
	v1 "github.com/calmato/presto-pay/api/calc/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	gcs "github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

func v1GroupInjection(fs *firestore.Firestore, cs *gcs.Storage, ac *api.Client) v1.APIV1GroupHandler {
	us := service.NewUserService(ac)
	ga := application.NewGroupApplication(us)
	gh := v1.NewAPIV1GroupHandler(ga)

	return gh
}
