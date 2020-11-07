package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/application"
	rv "github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/notification"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/repository"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/service"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/storage"
	dv "github.com/calmato/presto-pay/api/calc/internal/infrastructure/validation"
	v1 "github.com/calmato/presto-pay/api/calc/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/messaging"
	gcs "github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

func v1GroupInjection(
	fs *firestore.Firestore, cs *gcs.Storage,
	cm *messaging.Messaging, ac api.APIClient,
) v1.APIV1GroupHandler {
	nc := notification.NewNotificationClient(cm)

	us := service.NewUserService(ac)

	gr := repository.NewGroupRepository(fs)
	gdv := dv.NewGroupDomainValidation(gr, ac)
	gu := storage.NewGroupUploader(cs)
	grv := rv.NewGroupRequestValidation()
	gs := service.NewGroupService(gdv, gr, gu, ac, nc)
	ga := application.NewGroupApplication(grv, us, gs)
	gh := v1.NewAPIV1GroupHandler(ga)

	return gh
}
