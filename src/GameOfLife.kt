import java.security.SecureRandom

const val aliveCell = "▮"
const val deadCell = "▯"
const val boardHeight = 25
const val boardWidth = 80
const val millisecondsBetweenIterations = 1_000L

val random = SecureRandom()
var oldBoard = constructNewBoard()
var newBoard = constructNewBoard()

fun main() {
    playGame()
}

fun playGame() {
    setupBoard()
    gameLoop()
}

fun constructNewBoard(): Array<BooleanArray> {
    return Array(boardHeight) { BooleanArray(boardWidth) }
}

fun setupBoard() {
    // Sets approximately half of the cells as alive.
    repeat((boardWidth * boardHeight) / 2) {
        oldBoard[random.nextInt(boardHeight)][random.nextInt(boardWidth)] = true
    }
}

fun gameLoop() {
    while (true) {
        updateBoard()
        printBoard()
        prepareForNextIteration()
        Thread.sleep(millisecondsBetweenIterations)
    }
}

fun updateBoard() {
    oldBoard.forEachIndexed { outerArrayIndex, innerArray ->
        innerArray.forEachIndexed { innerArrayIndex, isAlive ->
            val numberOfAliveNeighbors = calculateNumberOfAliveNeighbors(outerArrayIndex, innerArrayIndex)
            newBoard[outerArrayIndex][innerArrayIndex] = decideFate(isAlive, numberOfAliveNeighbors)
        }
    }
}

fun calculateNumberOfAliveNeighbors(x: Int, y: Int): Int {
    var numberOfAliveNeighbors = 0
    for (i in (x - 1)..(x + 1)) {
        for (j in (y - 1)..(y + 1)) {
            if (i == x && j == y) {
                // Do not count self.
                continue
            }
            try {
                if (oldBoard[i][j]) {
                    numberOfAliveNeighbors++
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                // If we are out of bounds, that neighbor is not alive, therefore just continue.
            }
        }
    }
    return numberOfAliveNeighbors
}

fun decideFate(isAlive: Boolean, numberOfAliveNeighbors: Int): Boolean {
    return when {
        isAlive -> {
            when (numberOfAliveNeighbors) {
                2, 3 -> true
                else -> false
            }
        }
        else -> { // Is not alive
            when (numberOfAliveNeighbors) {
                3 -> true
                else -> false
            }
        }
    }
}

fun printBoard() {
    val stringBuilder = StringBuilder()
    newBoard.forEach { innerArray ->
        innerArray.forEach { cell ->
            stringBuilder.append(if (cell) aliveCell else deadCell)
        }
        stringBuilder.append("\n")
    }
    repeat(5) {
        stringBuilder.append("\n")
    }
    print(stringBuilder)
}

fun prepareForNextIteration() {
    oldBoard = newBoard
    newBoard = constructNewBoard()
}
