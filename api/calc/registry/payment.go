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

func v1PaymentInjection(
	fs *firestore.Firestore, cs *gcs.Storage, cm *messaging.Messaging, ac api.APIClient,
) v1.APIV1PaymentHandler {
	nc := notification.NewNotificationClient(cm)

	us := service.NewUserService(ac)

	gr := repository.NewGroupRepository(fs)

	pr := repository.NewPaymentRepository(fs)
	pdv := dv.NewPaymentDomainValidation(ac)
	pu := storage.NewPaymentUploader(cs)
	prv := rv.NewPaymentRequestValidation()
	ps := service.NewPaymentService(pdv, pr, pu, gr, ac, nc)
	pa := application.NewPaymentApplication(prv, us, ps)
	ph := v1.NewAPIV1PaymentHandler(pa)

	return ph
}
