import com.lunchometer.avro.AddCardTransaction
import com.lunchometer.avro.DomainAggregate
import com.lunchometer.avro.RetrieveCardTransactions
import com.lunchometer.domain.commandhandlers.handleAddCardTransaction
import com.lunchometer.domain.commandhandlers.handleRetrieveCardTransactions
import org.apache.avro.specific.SpecificRecord

fun handle(events: List<Any>, command: SpecificRecord): DomainAggregate  =
    when(command.schema.name) {
        RetrieveCardTransactions::class.java.simpleName -> {
            val retrieveCardTransactions = command as RetrieveCardTransactions
            handleRetrieveCardTransactions(events, retrieveCardTransactions)
        }
        AddCardTransaction::class.java.simpleName -> {
            val addCardTransaction = command as AddCardTransaction
            handleAddCardTransaction(events, addCardTransaction)
        }
        else -> {
            throw Exception()
        }
    }