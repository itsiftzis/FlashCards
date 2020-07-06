import java.util.Scanner

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val word = input.nextLine()

    var c = 'a'
    while (c <= 'z') {
        if (word.contains(c)) {
            c++
            continue
        } else {
            print(c)
            c++
        }
    }
}