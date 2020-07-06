import java.util.Scanner

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    var letter = input.nextLine()

    var c = 'a'
    val charLetter = letter.toCharArray()[0]
    while (c <= 'z') {
        if (c == charLetter) {
            break
        } else {
            print(c)
            c++
        }
    }
}