import net.andreinc.mapneat.dsl.json
import net.andreinc.mapneat.model.MapNeatSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

val xml = """
<customer>
    <firstname>Mike</firstname>
    <lastname>Smith</lastname>
    <visits count="3">
        <visit>
            <country>France</country>
            <date>2010-01-22</date>
        </visit>
        <visit>
            <country>Italy</country>
            <date>1983-01-22</date>
        </visit>
        <visit>
            <country>Romania</country>
            <date>2010-01-22</date>
        </visit>
        <visit>
            <country>Bulgaria</country>
            <date>2010-01-25</date>
        </visit>        
    </visits>
    <email type="business">mail@bsi.com</email>
    <email type="personal">mail@pers.com</email>
    <age>67</age>
</customer>
""".trimIndent()

fun main() {

    json(MapNeatSource.fromXml(xml)) {
        copySourceToTarget()
        println(this)
    }

    val df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    val out = json(MapNeatSource.fromXml(xml)) {
        "person" /= json {
            "firstName" *= "$.customer.firstname"
            "lastName" *= "$.customer.lastname"
            "personalEmails" *= "$.customer.email[?(@.type == 'personal')].content"
            "businessEmails" *= "$.customer.email[?(@.type == 'business')].content"
            if (sourceCtx().read<MutableList<String>>("$.customer.visits.visit[*].country").contains("Romania")) {
                "hasVisitedRomania" /= "true"
            }
            else {
                "hasVisitedRomania" /= "false"
            }
        }
        "visits" /= json {
            "yearsActive" *= {
                expression = "$.customer.visits.visit[*].date"
                processor = {
                    (it as MutableList<String>)
                        .map { ds -> LocalDate.parse(ds, df).year.toString() }
                        .toSet()
                }
            }
            "countries" *= "$.customer.visits.visit[*].country"
        }
    }.getPrettyString();

    println(out)
}