package registry

import (
	"github.com/calmato/presto-pay/api/user/internal/application"
	rv "github.com/calmato/presto-pay/api/user/internal/application/validation"
	"github.com/calmato/presto-pay/api/user/internal/infrastructure/repository"
	"github.com/calmato/presto-pay/api/user/internal/infrastructure/service"
	"github.com/calmato/presto-pay/api/user/internal/infrastructure/storage"
	dv "github.com/calmato/presto-pay/api/user/internal/infrastructure/validation"
	v1 "github.com/calmato/presto-pay/api/user/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	gcs "github.com/calmato/presto-pay/api/user/lib/firebase/storage"
)

func v1UserInjection(fa *authentication.Auth, fs *firestore.Firestore, cs *gcs.Storage) v1.APIV1UserHandler {
	ur := repository.NewUserRepository(fa, fs)
	udv := dv.NewUserDomainValidation(ur)
	uu := storage.NewUserUploader(cs)
	urv := rv.NewUserRequestValidation()
	us := service.NewUserService(udv, ur, uu)
	ua := application.NewUserApplication(urv, us)
	uh := v1.NewAPIV1UserHandler(ua)

	return uh
}
