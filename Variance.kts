import java.util.*
import java.util.concurrent.TimeUnit

interface FoodInspector {
    fun getHarvestDate(): Date
    fun getColor(): String
}
abstract class Fruit : FoodInspector{
    abstract val nameId: String
    abstract val harvestDate: Date
}
open class Apple(override val nameId: String, override val harvestDate: Date, val color: String) :
    Fruit() {

    override fun getColor(): String {
        return color
    }

    override fun getHarvestDate(): Date {
        return harvestDate
    }

}
class GrannySmith(nameId: String, harvestDate: Date) : Apple(nameId, harvestDate, "green")
class Orange(override val nameId: String, override val harvestDate: Date) : Fruit() {

    override fun getColor(): String {
        return "Orange"
    }

    override fun getHarvestDate(): Date {
        return harvestDate
    }
}
class Kale(val nameId: String, val harvestDate: Date) : FoodInspector {

    override fun getHarvestDate(): Date {
        return harvestDate
    }

    override fun getColor(): String {
        return "green"
    }

}

class InvariantCrate<T>(val items: MutableList<T> = mutableListOf()) {
    fun addFood(food: T) = items.add(food)
    fun getLastFoodItem(): T {
        return items.last()
    }
}

class CovariantFarm<out T>(val items: List<T>) { //changing this to a Mutable list throws error
    fun getProduce(): T {
        return items.last()
    }
}

class ContravariantConsumer<in T>{
    fun isProduceFresh(item: T): Unit{
        print("consumed ${item.toString()}")
    }
}

class MeijerProduce {

    private val weekAgo =
        Date(System.currentTimeMillis() - TimeUnit.DAYS.toDays(7))//LocalDate.parse("2019-5-13")
    private val threeDaysAgo =
        Date(System.currentTimeMillis() - TimeUnit.DAYS.toDays(3))//LocalDate.parse("2019-5-19")
    private val freshFromFarm =
        Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMinutes(90))//LocalDate.parse("2019-5-21")

    private val apples = listOf(
        Apple("fuji", weekAgo, "red"),
        Apple("goldendelicious", freshFromFarm, "gold")
    )
    private val grannySmithApples =
        listOf(GrannySmith("grannysmith1", freshFromFarm), GrannySmith("grannysmith2", weekAgo))
    private val oranges = listOf(Orange("halos", weekAgo), Orange("cuties", threeDaysAgo))

    fun sortByFood() {
        var appleCrate = InvariantCrate<Apple>()
        appleCrate.addFood(apples[0])
        appleCrate.addFood(apples[1])
        //appleCrate.addFood(oranges[0])

        var grannySmithCrate = InvariantCrate<GrannySmith>()
        grannySmithCrate.addFood(GrannySmith("grannysmith", freshFromFarm))

        var orangeCrate = InvariantCrate<Orange>()
        orangeCrate.addFood(oranges[0])
        orangeCrate.addFood(oranges[1])
        //orangeCrate.addFood(apples[0])

        var fruitCrate = InvariantCrate<Fruit>()
        fruitCrate.addFood(apples[0])
        fruitCrate.addFood(oranges[0])

        //Invariance of generic Type doesn't respect relation between type parameter
        fruitCrate = appleCrate
        appleCrate = grannySmithCrate
        fruitCrate = orangeCrate

        //Use Site Variance ( we are creating projection of the generic type)
        val projectedAppleCrate: InvariantCrate<in Apple> = fruitCrate
        var projectedFruitCrate: InvariantCrate<out Fruit> = appleCrate
        val projectedGrannySmithCrate: InvariantCrate<in GrannySmith> = appleCrate
        projectedFruitCrate = orangeCrate

    }

    fun takeFruitsFromFarm() {

        var appleCrate : CovariantFarm<Apple> = CovariantFarm(listOf(apples[0], apples[1]))
        var grannySmithCrate : CovariantFarm<GrannySmith> = CovariantFarm(listOf(grannySmithApples[0], grannySmithApples[1]))
        var orangeCrate : CovariantFarm<Orange> = CovariantFarm<Orange>(listOf(oranges[0], oranges[1]))
        var fruitCrate : CovariantFarm<Fruit> = CovariantFarm<Fruit>(listOf(oranges[0], apples[1]))
        val kaleCrate : CovariantFarm<Kale> = CovariantFarm<Kale>(listOf(Kale("kalegreens", threeDaysAgo)))


        orangeCrate = fruitCrate
        appleCrate = fruitCrate

        //Covariance of generic Type maintains subtype relation between type parameter
        fruitCrate = appleCrate
        //the above line works because anywhere we need a fruit an apple will suffice
        val fruit: Fruit = fruitCrate.getProduce()
        //and also compiler makes sure that out type parameter is not usable as a method parameter inside the generic type
        //if it allowed this type parameter you could add fruit which is not an apple that could cause problem when u are looking for apples

        grannySmithCrate = appleCrate
        appleCrate = grannySmithCrate
        fruitCrate = orangeCrate

    }

    fun addFruitsToShelf(){

        var appleCrate = ContravariantConsumer<Apple>()
        var grannySmithCrate = ContravariantConsumer<GrannySmith>()
        var orangeCrate = ContravariantConsumer<Orange>()
        var fruitCrate = ContravariantConsumer<Fruit>()
        var kaleCrate = ContravariantConsumer<Kale>()

        appleCrate = grannySmithCrate

        //Contravariance of generic Type inverts the subtype relation between type parameter
        fruitCrate = appleCrate
        grannySmithCrate = appleCrate
        appleCrate = fruitCrate
        //the above line works because anywhere it needs a fruit passing in an apple will suffice
        appleCrate.isProduceFresh(apples[0])
        //compiler makes sure that in type parameter is not usable as a return type inside the generic type
        //if it allowed this you will have problem when

        fruitCrate = orangeCrate
        orangeCrate = fruitCrate

    }


}
