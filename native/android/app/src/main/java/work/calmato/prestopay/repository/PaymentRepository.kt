package work.calmato.prestopay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.MainActivity
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.DatabaseGroup
import work.calmato.prestopay.database.asPaymentModel
import work.calmato.prestopay.network.*

class PaymentRepository(private val database: AppDatabase, groupId: String) {
  val payments: LiveData<List<PaymentPropertyGet>> =
    Transformations.map(database.paymentDao.getPayments(groupId)) {
      it.asPaymentModel()
    }
  val status: LiveData<DatabaseGroup> = database.groupDao.getLendingStatus(groupId)

  suspend fun refreshPayments(id: String, groupId: String) {
    withContext(Dispatchers.IO) {
      val getPayments = Api.retrofitService.getPayments(id, groupId, MainActivity.currency).await()
      val paymentsList = NetworkPaymentContainer(getPayments.payments)
      database.paymentDao.deleteAll(groupId)
      database.paymentDao.insertAll(*paymentsList.asDatabaseModel())
      database.groupDao.updateLendingStatus(getPayments.users.values.toList(), groupId)
    }
  }

  suspend fun deletePayment(paymentId: String) {
    withContext(Dispatchers.IO) {
      database.paymentDao.deletePayment(paymentId)
    }
  }
}
