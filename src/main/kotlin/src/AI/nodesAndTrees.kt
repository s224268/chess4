package dataClasses

import AI.results
import gameLogic.getLegalMoves
import gameLogic.getWhiteAdvantage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import view.printBoard
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

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

/**
 * I made some lovely concurrency here. Not totally optimal, but really simple way to split it up. For very simple
 * positions, it won't use all cores and for complicated positions it may try to run more coroutines (read: Threads)
 * than what is available on the computer. However, this shouldn't be a significant slowdown.
 */
suspend fun evalBestMove(board: Board, depth: Int, isWhite: Boolean): Board {
    var favoriteChild = Node(board = board)
    favoriteChild.setChildren(isWhite)
    val favoriteChildren = mutableMapOf<Int,Node>()
    val jobs = mutableListOf<Job>()
    val mutex = Mutex()
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    favoriteChild.children.forEach{child ->
        val job = coroutineScope.launch {

            val bestScore: AtomicInteger = if (isWhite) AtomicInteger(Int.MIN_VALUE) else AtomicInteger(Int.MAX_VALUE)
            var localFavoriteChild = favoriteChild
            if (isWhite) {
                val score = minimax(child, depth - 1, false, Int.MIN_VALUE, Int.MAX_VALUE)
                child.children.clear() // saves massive amount of memory. A true keeper <3
                if (score > bestScore.get()) {
                    localFavoriteChild = child
                    bestScore.set(score)
                }

            } else {

                val score = minimax(child, depth - 1, true, Int.MIN_VALUE, Int.MAX_VALUE)
                child.children.clear()
                if (score < bestScore.get()) {
                    localFavoriteChild = child
                    bestScore.set(score)
                }
            }
            mutex.withLock {
                favoriteChildren[bestScore.get()] = localFavoriteChild
            }
        }
        jobs.add(job)
    }
    jobs.forEach { it.join() }
    if (isWhite){
        val highestkey = favoriteChildren.maxBy { entry -> entry.key }
        favoriteChild = favoriteChildren[highestkey.key]!!
        //println("Here the key is: " + highestkey.key)
        favoriteChild.whiteAdvantage = highestkey.key
    } else {
        val minimumKey = favoriteChildren.minBy { entry -> entry.key }
        favoriteChild = favoriteChildren[minimumKey.key]!!
        //println("Here the key is: " + minimumKey.key)
        favoriteChild.whiteAdvantage = minimumKey.key
    }


    //println("Potential best score scenario found: $bestScore")
    print("This move has a score of: " + favoriteChild.whiteAdvantage)
    return favoriteChild.board
}

fun minimax(node: Node, depth: Int, maximizing: Boolean, alpha: Int, beta: Int): Int {
    if (depth == 0) {
        evalNode(node)
        return node.whiteAdvantage
    }

    node.setChildren(maximizing)
    if(node.children.isEmpty()){
        evalNode(node)
        return node.whiteAdvantage
    }

    var a = alpha
    var b = beta


    if (maximizing) {
        var maxEval: Int = -1000069
        if (node.children.size == 0){
            printBoard(node.board)
            println("Gooner alarm 4")
        }
        for (child in node.children) {
            val eval = minimax(child, depth - 1, false, a, b)

            //println("EVAL IS: " + eval)
            maxEval = max(maxEval, eval)
            if (maxEval == -1000069){
                println("Gooner alarm 1")
            }
            //println("MAXEVAL IS: " + maxEval)
            if (maxEval >= b) {
                break
            }
            a = max(a, maxEval)
        }
        if (maxEval == -1000069){
            println("Gooner alarm 2")
        }
        node.whiteAdvantage = maxEval
        if (node.whiteAdvantage == -1000069){
            println("Gooner alarm 3")
        }
        return maxEval
    } else {
        var minEval: Int = Int.MAX_VALUE
        for (child in node.children) {
            val eval = minimax(child, depth - 1, true, a, b)
            minEval = min(minEval, eval)
            if (minEval <= a) {
                break
            }
            b = min(b, minEval)
        }
        node.whiteAdvantage = minEval
        return minEval
    }

}