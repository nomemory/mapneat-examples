import net.andreinc.mockneat.types.enums.CreditCardType
import net.andreinc.mockneat.unit.address.Countries.countries
import net.andreinc.mockneat.unit.financial.CVVS.cvvs
import net.andreinc.mockneat.unit.financial.CreditCards.creditCards
import net.andreinc.mockneat.unit.objects.Constructor.constructor
import net.andreinc.mockneat.unit.objects.Filler.filler
import net.andreinc.mockneat.unit.objects.From.from
import net.andreinc.mockneat.unit.seq.LongSeq.longSeq
import net.andreinc.mockneat.unit.time.LocalDates.localDates
import net.andreinc.mockneat.unit.types.Ints.ints
import net.andreinc.mockneat.unit.user.Emails.emails
import net.andreinc.mockneat.unit.user.Names.names
import net.andreinc.mockneat.unit.user.Passwords.passwords
import net.andreinc.mockneat.unit.user.Users.users
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE
import kotlin.collections.ArrayList

class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val friends: MutableList<Long>,
    val visits: Set<Visit>,
    val creditCards: Set<CreditCardInfo>,
    val pwd: String,
    val email: String,
    val userName: String
)

class Visit(
    val id: Long,
    val country: String,
    val enter: String
)

class CreditCardInfo(
    val number: String,
    val cvv: String,
    val expirationDate: String
)

fun getUsers(size: Int) : Map<Long, User> {

    val pId = longSeq().increment(10)
    val vId = longSeq().increment(100)

    val persons = constructor(User::class.java)
            .params(
                pId, // Long
                names().first(), // String
                names().last(), // String
                localDates().past(LocalDate.of(1900, 1, 1)).display(ISO_DATE), // Date
                filler { ArrayList<User>() }, // List<Person>
                constructor(Visit::class.java).params(
                    vId,
                    countries().names(),
                    localDates().past(LocalDate.of(1970, 1, 1)).display(ISO_DATE),
                ).set(ints().range(1, 10)), // Set<Visit>
                constructor(CreditCardInfo::class.java).params(
                    creditCards().types(CreditCardType.AMERICAN_EXPRESS, CreditCardType.VISA_16, CreditCardType.MASTERCARD),
                    cvvs(),
                    localDates().future(LocalDate.of(2040, 1, 1)).display(ISO_DATE)
                ).set(ints().range(1, 4)), // Set<CreditCardInfo>
                passwords().weak(),
                emails(),
                users()
            )
            .list(size)
            .get()

    val friendsGen = from(persons)
                        .map { it.id }
                        .list(ints().range(1, 5))

    persons.forEach { it.friends.addAll(friendsGen.get()) }

    return persons.associateBy({ it.id }, { it})
}