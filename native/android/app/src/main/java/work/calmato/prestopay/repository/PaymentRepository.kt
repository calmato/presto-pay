package work.calmato.prestopay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import work.calmato.prestopay.database.AppDatabase
import work.calmato.prestopay.database.asPaymentModel
import work.calmato.prestopay.network.Api
import work.calmato.prestopay.network.PaymentPropertyGet
import work.calmato.prestopay.network.asDatabaseModel

class PaymentRepository(private val database:AppDatabase,groupId: String) {
  val payments:LiveData<List<PaymentPropertyGet>> =
    Transformations.map(database.paymentDao.getPayments(groupId)){
      it.asPaymentModel()
    }

  suspend fun refreshPayments(id:String,groupId:String){
    withContext(Dispatchers.IO){
      val paymentsList = Api.retrofitService.getPayments(id,groupId).await()
      database.paymentDao.deleteAll(groupId)
      database.paymentDao.insertAll(*paymentsList.asDatabaseModel())
    }
  }

  suspend fun deletePayment(paymentId: String) {
    withContext(Dispatchers.IO) {
      database.paymentDao.deletePayment(paymentId)
    }
  }
}
