import com.example.racunko.model.Transaction
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class TransactionTest {
    @Test
    fun `transaction should have correct default values`() {
        val transaction = Transaction(
            amount = 100.0,
            description = "Test",
            category = "Food"
        )
        assertEquals(0, transaction.id)
        assertTrue(transaction.isExpense)
        assertTrue(transaction.date <= Date())
    }

    @Test
    fun `equals and hashCode should work as expected`() {
        val date = Date()
        val transaction1 = Transaction(
            id = 1,
            amount = 100.0,
            description = "Test",
            category = "Food",
            date = date
        )
        val transaction2 = Transaction(
            id = 1,
            amount = 100.0,
            description = "Test",
            category = "Food",
            date = date
        )
        assertEquals(transaction1, transaction2)
        assertEquals(transaction1.hashCode(), transaction2.hashCode())
    }
}
