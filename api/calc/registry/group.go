package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/application"
	rv "github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/repository"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/service"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/storage"
	dv "github.com/calmato/presto-pay/api/calc/internal/infrastructure/validation"
	v1 "github.com/calmato/presto-pay/api/calc/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	gcs "github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

func v1GroupInjection(fs *firestore.Firestore, cs *gcs.Storage, ac *api.Client) v1.APIV1GroupHandler {
	us := service.NewUserService(ac)

	gr := repository.NewGroupRepository(fs)
	gdv := dv.NewGroupDomainValidation(gr, ac)
	gu := storage.NewGroupUploader(cs)
	grv := rv.NewGroupRequestValidation()
	gs := service.NewGroupService(gdv, gr, gu)
	ga := application.NewGroupApplication(grv, us, gs)
	gh := v1.NewAPIV1GroupHandler(ga)

	return gh
}
