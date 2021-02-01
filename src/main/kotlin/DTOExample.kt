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

        // Copy everything from the object (source) to the target
        "" *= "$"

        // Removes the visits node
        - "visits"

        // Removes the creditCards node
        - "creditCards"

        // Removes the pwd node
        - "pwd"

        // Creates a new "visited" node containing all the country names that were visited
        // To avoid country duplication we keep the results in a Set
        "visited" *= {
            expression = "$.visits[*].country"
            processor = {
                val result = HashSet<String>()
                result.addAll(it as LinkedList<String>)
                result
            }
        }

        // We modify the lastName field and make it uppercase
        "lastName" /= {
            targetCtx().read<String>("$.lastName").toUpperCase()
        }

        // We modify the friends field to contain actually names and first names
        // instead of users ids
        "friends" /= {
            targetCtx()
                .read<ArrayList<Long>>("$.friends")
                .map { (users[it]?.firstName + " " + users[it]?.lastName) }
                .toList()
        }
    }.getPrettyString()

    println(theInitialUserDto)
}