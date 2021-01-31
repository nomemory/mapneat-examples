import net.andreinc.mapneat.dsl.json
import net.andreinc.mapneat.model.MapNeatSource.Companion.fromObject
import net.andreinc.mockneat.unit.objects.From.from
import java.util.*
import kotlin.collections.HashSet

fun main() {

    val persons = getPerson(100)
    val aRandomPerson = from(persons.values.toMutableList()).get()

    val personInitial = json(fromObject(aRandomPerson)) {
        copySourceToTarget()
    }.getPrettyString()
    println(personInitial)

    val personDto = json(fromObject(aRandomPerson)) {
        copySourceToTarget()

        - "visits"
        - "creditCards"

        "visited" *= {
            expression = "$.visits[*].country"
            processor = {
                val result = HashSet<String>()
                result.addAll(it as LinkedList<String>)
                result
            }
        }

        "lastName" /= {
            targetCtx().read<String>("$.lastName").toUpperCase()
        }

        "friends" /= {
            targetCtx()
                .read<ArrayList<Long>>("$.friends")
                .map { (persons[it]?.firstName + " " + persons[it]?.lastName) }
                .toList()
        }
    }.getPrettyString()
    println(personDto)
}