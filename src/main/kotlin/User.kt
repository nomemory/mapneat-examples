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

/**
 * Returns a set of Visit objects
 * @param lowerBound The minimum possible size of the Set
 * @param upperBound The maximum possible size of the Set
 * @return A Set of Visit objects
 */
fun getVisits(lowerBound: Int, upperBound: Int) : MutableSet<Visit> {
    val vId = longSeq().increment(100)
    return constructor(Visit::class.java).params(
        vId,
        countries().names(),
        localDates().past(LocalDate.of(1970, 1, 1)).display(ISO_DATE),
    ).set(ints().range(lowerBound, upperBound)).get()
}

/**
 * Returns a set of CreditCardInof objects
 * @param lowerBound The minim possible size of the Set
 * @param upperBound The maximum possible size of the Set
 * @return A Set of CreditCardInfo objects
 */
fun getCreditCardInfos(lowerBound: Int, upperBound: Int) : MutableSet<CreditCardInfo> {
    return constructor(CreditCardInfo::class.java).params(
        creditCards().types(CreditCardType.AMERICAN_EXPRESS, CreditCardType.VISA_16, CreditCardType.MASTERCARD),
        cvvs(),
        localDates().future(LocalDate.of(2040, 1, 1)).display(ISO_DATE)
    ).set(ints().range(lowerBound, upperBound)).get()
}

/**
 * Returns a Map of (arbitrary) users. The key represents the id of the user, the value is the user itself.
 * @param size the number of users in the Map
 * @return A map of users.
 */
fun getUsers(size: Int) : Map<Long, User> {

    val pId = longSeq().increment(10)

    val persons = constructor(User::class.java)
            .params(
                pId, // Long
                names().first(), // String
                names().last(), // String
                localDates().past(LocalDate.of(1900, 1, 1)).display(ISO_DATE),
                filler { ArrayList<User>() },
                getVisits(1, 10),
                getCreditCardInfos(1, 4),
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