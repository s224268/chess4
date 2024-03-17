package dataClasses

import AI.results
import gameLogic.getAllPossibleMoves
import gameLogic.getLegalMoves
import gameLogic.getWhiteAdvantage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import view.printBoard
import view.printMoves
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

data class Node(
    val board: Board,
    val children: MutableList<Node> = mutableListOf(),
    var whiteAdvantage: Int = 0,
    val move: Move? = null
) {
    /**
     * ExpandFrontier
     */
    fun setChildren(isWhite: Boolean) {
        val allPossibleMoves: MutableList<Move> = getLegalMoves(this.board, isWhite)

        for (move: Move in allPossibleMoves) {
            val newChild = Node(
                board = results(this.board.copyBoard(isItWhitesTurn = isWhite), move),
                children = mutableListOf()
            )
            children.add(newChild)
        }
    }
}

fun evalNode(node: Node) {
    node.whiteAdvantage = getWhiteAdvantage(node.board)
}

suspend fun evalBestMove(board: Board, depth: Int, isWhite: Boolean): Board {
    var favoriteChild = Node(board = board)
    favoriteChild.setChildren(isWhite)
    val jobs = mutableListOf<Job>()
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    var bestScore: Int = if (isWhite) Int.MIN_VALUE else Int.MAX_VALUE
    favoriteChild.children.forEach{child ->
        val job = coroutineScope.launch {

            if (isWhite) {
                val score = minimax(child, depth - 1, true, Int.MIN_VALUE, Int.MAX_VALUE)
                if (score > bestScore) {
                    favoriteChild = child
                    bestScore = score
                } else {
                    child.children.clear() //MASSIVELY saves ram and prevents memory overflow
                }

            } else {

                val score = minimax(child, depth - 1, false, Int.MIN_VALUE, Int.MAX_VALUE)
                if (score < bestScore) {
                    favoriteChild = child
                    bestScore = score
                } else {
                    child.children.clear()
                }
            }
        }
        jobs.add(job)
    }
    jobs.forEach { it.join() }

    println("Potenital best score scenario found: $bestScore")
    print("This move has a score of: " + favoriteChild.whiteAdvantage)
    return favoriteChild.board
}

fun minimax(node: Node, depth: Int, maximizing: Boolean, alpha: Int, beta: Int): Int {
    if (depth == 0) {
        evalNode(node)
        return node.whiteAdvantage
    }

    node.setChildren(maximizing)

    var a = alpha
    var b = beta


    if (maximizing) {
        var maxEval: Int = Int.MIN_VALUE
        for (child in node.children) {
            val eval = minimax(child, depth - 1, false, a, b)
            maxEval = max(maxEval, eval)
            if (maxEval >= b) {
                break
            }
            a = max(a, eval)
        }
        node.whiteAdvantage = maxEval
        return maxEval
    } else {
        var minEval: Int = Int.MAX_VALUE
        for (child in node.children) {
            val eval = minimax(child, depth - 1, true, a, b)
            minEval = min(minEval, eval)
            if (minEval <= a) {
                break
            }
            b = min(b, eval)
        }
        node.whiteAdvantage = minEval
        return minEval
    }

}