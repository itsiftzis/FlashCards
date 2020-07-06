package flashcards

import java.io.File
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess

var logStream = mutableListOf<String>()

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val cardsMap = mutableMapOf<String, String>()
    val errorMap = mutableMapOf<String, Int>()
    val commandMap = handleInputArguments(args)

    if (commandMap["-import"] != null && commandMap["-import"]?.isNotEmpty() == true)
        loadCards(cardsMap, errorMap, commandMap["-import"]?.get(0))

    while (true) {
        val message = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"
        println(message)
        logStream.add(message)
        when (scanner.nextLine()) {
            "add" -> {
                logStream.add("add")
                handleAdd(cardsMap, scanner)
            }
            "remove" -> {
                logStream.add("remove")
                handleRemove(cardsMap, scanner, errorMap)
            }
            "import" -> {
                logStream.add("import")
                handleImport(cardsMap, scanner, errorMap)
            }
            "export" -> {
                logStream.add("export")
                handleExport(cardsMap, scanner, errorMap)
                val exportMessage = "${cardsMap.size} cards have been saved."
                logStream.add(exportMessage)
                println(exportMessage)
            }
            "ask" -> {
                logStream.add("ask")
                handleAsk(cardsMap, scanner, errorMap)
            }
            "log" -> {
                logStream.add("log")
                handleLog(scanner)
            }
            "hardest card" -> {
                logStream.add("hardest card")
                handleHardestCard(errorMap)
            }
            "reset stats" -> {
                logStream.add("reset stats")
                errorMap.clear()
                val messageClear = "Card statistics has been reset."
                println(messageClear)
                logStream.add(messageClear)
            }
            "exit" -> {
                logStream.add("exit")
                handleExit(cardsMap, errorMap, commandMap["-export"]?.get(0))
            }
        }
    }
}

fun loadCards(cardsMap: MutableMap<String, String>, errorMap: MutableMap<String, Int>, fileName: String?) {
    val file = File(fileName)
    if (!file.exists()) {
        println("File not found.")
    } else {
        val map = mutableMapOf<String, String>()
        file.forEachLine {
            val (key, value, error) = it.split(":")

            map[key] = value
            if ("null" != error) {
                errorMap[key] = error.toInt()
            }
        }
        cardsMap.putAll(map)
        println("${cardsMap.keys.size} cards have been loaded")
    }

}

fun handleInputArguments(args: Array<String>): Map<String, List<String>> {
    return args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
        else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
    }.first
}

fun handleHardestCard(errorMap: MutableMap<String, Int>) {
    if (errorMap.isEmpty()) {
        val message = "There are no cards with errors."
        logStream.add(message)
        println(message)
    } else {
        val maxValue = errorMap.maxBy { (_, value) -> value }?.value
        val entries = errorMap.filter { (_, value) -> value == maxValue }
        val message: String
        message = if (entries.size == 1) {
            "The hardest card is \"${entries.keys.joinToString()}\". You have ${entries.values.joinToString()} errors answering them."
        } else {
            val keys = entries.keys.joinToString(", ", prefix = "[", postfix = "]", transform = { k -> "\"" + k + "\"" })
            val suffixMessage = "You have $maxValue error answering them."
            "The hardest cards are $keys. $suffixMessage"
        }

        logStream.add(message)
        println(message)
    }
}

fun handleLog(scanner: Scanner) {
    val message = "File name:"
    logStream.add(message)
    println(message)
    val fileName = scanner.nextLine()
    logStream.add(fileName)
    val file = File(fileName)
    file.writeText(logStream.joinToString("\n"))
    val messageEnd = "The log has been saved."
    println(messageEnd)
    logStream.add(messageEnd)
}

fun handleAsk(cardsMap: MutableMap<String, String>, scanner: Scanner, errorMap: MutableMap<String, Int>) {
    println("How many times to ask?")
    val questionsNumber = scanner.nextInt()
    scanner.nextLine()
    var questionsAsked = 0
    val cardsMapCopy = HashMap<String, String>(cardsMap)
    while (questionsAsked < questionsNumber) {
        val question = nextInt(0, cardsMapCopy.size)
        val entry = cardsMapCopy.entries.elementAt(question)
        println("Print the definition of \"${entry.key}\":")
        val userAnswer = scanner.nextLine()
        if (entry.value == userAnswer) {
            println("Correct answer.")
            questionsAsked++
        } else {
            var firstPart = "Wrong answer, The correct one is \"${entry.value}\""
            increaseErrorCount(entry.key, errorMap)
            if (cardsMapCopy.containsValue(userAnswer)) {
                val key = cardsMapCopy.filterValues { it == userAnswer }.keys.stream().findFirst().orElse("")
                firstPart += ", you've just written the definition of \"$key\""
            }
            println(firstPart)
//            println("The asked card was \"$entry\", the answer was \"$userAnswer\"")
            questionsAsked++
        }
        if (questionsAsked < cardsMapCopy.size) {
            cardsMapCopy.remove(entry.key)
        }
    }
}

fun increaseErrorCount(key: String, errorMap: MutableMap<String, Int>) {
    if (errorMap.contains(key)) {
        errorMap[key] = errorMap[key]!! + 1
    } else {
        errorMap[key] = 1
    }
}

fun handleExit(cardsMap: MutableMap<String, String>, errorMap: MutableMap<String, Int>, fileName: String?) {
    if (fileName != null) {
        println("FileName: $fileName")
        var text = ""
        cardsMap.forEach { (key, value) -> text += "$key:$value:${errorMap[key]}\n" }
        val file = File(fileName)
        file.writeText(text)
    }
    println("${cardsMap.keys.size} cards have been saved.")
    exitProcess(1)
}

fun handleExport(cardsMap: MutableMap<String, String>, scanner: Scanner, errorMap: MutableMap<String, Int>) {
    println("File name:")
    val fileName = scanner.nextLine()
    val file = File(fileName)
    var text = ""
    cardsMap.forEach { (key, value) -> text += "$key:$value:${errorMap[key]}\n" }
    file.writeText(text)
}

fun handleImport(cardsMap: MutableMap<String, String>, scanner: Scanner, errorMap: MutableMap<String, Int>) {
    val map = mutableMapOf<String, String>()
    println("File name:")
    val fileName = scanner.nextLine()
    val file = File(fileName)
    if (!file.exists()) {
        println("File not found.")
        return
    }
    file.forEachLine {
        val (key, value, error) = it.split(":")
        map[key] = value
        if ("null" != error) {
            errorMap[key] = error.toInt()
        }
    }
    cardsMap.putAll(map)
    println("${map.size} cards have been loaded.")
}

fun handleRemove(cardsMap: MutableMap<String, String>, scanner: Scanner, errorMap: MutableMap<String, Int>) {
    println("The cards:")
    val term = scanner.nextLine()
    if (cardsMap.remove(term) != null) {
        println("The card has been removed.")
        errorMap.remove(term)
    } else {
        println(" Can't remove \"${term}\": there is no such card.")
    }
}

fun handleAdd(cardsMap: MutableMap<String, String>, scanner: Scanner) {
    println("The card:")
    val term = scanner.nextLine()
    if (cardsMap.containsKey(term)) {
        println("The card \"$term\" already exists. Try again")
        return
    }
    println("The definition of the card:")
    val definition = scanner.nextLine()
    if (cardsMap.containsValue(definition)) {
        println("The definition \"$definition\" already exists. Try again")
        return
    }
    cardsMap[term] = definition
    println("The pair (\"$term\":\"$definition\") has been added.")
}