import net.andreinc.mapneat.dsl.json
import net.andreinc.mapneat.model.MapNeatSource.Companion.fromObject
import net.andreinc.mockneat.unit.objects.From.from
import java.util.*
import kotlin.collections.HashSet

fun main() {

    val users : Map<Long, User> = getUsers(100)
    val aRandomUser = from(users.values.toMutableList()).get()

    val theInitialUser = json(fromObject(aRandomUser)) {
        copySourceToTarget()
    }.getPrettyString()
    println(theInitialUser)

    val theInitialUserDto = json(fromObject(aRandomUser)) {
        "" *= "$"
        - "visits"
        - "creditCards"
        - "pwd"
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
                .map { (users[it]?.firstName + " " + users[it]?.lastName) }
                .toList()
        }
    }.getPrettyString()

    println(theInitialUserDto)
}