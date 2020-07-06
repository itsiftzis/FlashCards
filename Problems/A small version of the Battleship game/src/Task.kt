import java.util.Scanner

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val firstShip = Pair(input.nextInt(), input.nextInt())
    val secondShip = Pair(input.nextInt(), input.nextInt())
    val thirdShip = Pair(input.nextInt(), input.nextInt())

    var rows = mutableListOf<Int>()
    var columns = mutableListOf<Int>()

    for (x in 1..5) {
        if (allowedRow(x, firstShip) && allowedRow(x, secondShip) && allowedRow(x, thirdShip)) {
            rows.add(x)
        }
    }
    for (y in 1..5) {
        if (allowedColumn(y, firstShip) && allowedColumn(y, secondShip) && allowedColumn(y, thirdShip)) {
            columns.add(y)
        }
    }

    println(rows.joinToString(" "))
    println(columns.joinToString(" "))

}

private fun allowedColumn(y: Int, first: Pair<Int, Int>) =
        y != first.second


private fun allowedRow(x: Int, firstShip: Pair<Int, Int>) =
        x != firstShip.first