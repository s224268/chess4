package gameLogic

import AI.results
import dataClasses.*
import view.printMoves
import java.util.EnumSet.range
import javax.swing.text.Position
import kotlin.math.max
import kotlin.system.exitProcess

/**
 * Made this function, then realized it's not useful for anything.
 * Probably to be considered deprecated, but up to you
 */
fun getAllMovesFromPiece(board: Board, location: Location): List<Move> {
    val moves: MutableList<Move> = mutableListOf()
    val piece = board.board[location.x][location.y]
    if (piece != null) {
        for (move in piece.moveStrategy(board, location)){
            moves.add(Move(start = location, end = move))
        }
    }
    return moves
}

/**
 * Doesnt check for promotion. Assumes that player will always promote, so there is never a pawn
 * on the last rank
 */
fun getPawnMoves(board: Board, location: Location): MutableList<Location> {
    val piece = board.board[location.x][location.y]!!
    val possibleMoves = mutableListOf<Location>()

    if (piece.isWhite) {
        // Single forward move
        if (location.y + 1 <= 7 && board.board[location.x][location.y + 1] == null) {
            possibleMoves.add(Location(x = location.x, y = location.y + 1))
            // Double forward
            if (location.y == 1 && board.board[location.x][location.y + 2] == null) {
                possibleMoves.add(Location(x = location.x, y = location.y + 2))
            }
        }

        // Sideways capture moves
        if (location.x + 1 <= 7 && location.y + 1 <= 7 && board.board[location.x + 1][location.y + 1]?.isWhite == false) {
            possibleMoves.add(Location(x = location.x + 1, y = location.y + 1))
        }
        if (location.x - 1 >= 0 && location.y + 1 <= 7 && board.board[location.x - 1][location.y + 1]?.isWhite == false) {
            possibleMoves.add(Location(x = location.x - 1, y = location.y + 1))
        }
    } else { // Piece is black
        // Single forward move
        if (location.y - 1 >= 0 && board.board[location.x][location.y - 1] == null) {
            possibleMoves.add(Location(x = location.x, y = location.y - 1))
            // Double forward
            if (location.y == 6 && board.board[location.x][location.y - 2] == null) {
                possibleMoves.add(Location(x = location.x, y = location.y - 2))
            }
        }
        // Sideways capture moves
        if (location.x + 1 <= 7 && location.y - 1 >= 0 && board.board[location.x + 1][location.y - 1]?.isWhite == true) {
            possibleMoves.add(Location(x = location.x + 1, y = location.y - 1))
        }
        if (location.x - 1 >= 0 && location.y - 1 >= 0 && board.board[location.x - 1][location.y - 1]?.isWhite == true) {
            possibleMoves.add(Location(x = location.x - 1, y = location.y - 1))
        }
    }
    return possibleMoves
}

fun getQueenMoves(board: Board, location: Location): MutableList<Location> {
    val list = getRookMoves(board, location)
    list.addAll((getBishopMoves(board, location)))
    return list
}

fun getRookMoves(board: Board, location: Location): MutableList<Location> {

    val piece = board.board[location.x][location.y]
    val possibleMoves = mutableListOf<Location>()
    //North
    for (i in 1..8) { //TODO: 7?
        if (location.y - i !in 0..7) { //TODO: This could be optimized. y-i<0
            break
        }
        if (board.board[location.x][location.y - i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x, y = location.y - i))
        }
        if (board.board[location.x][location.y - i] != null) {
            break;
        }
    }

    //East
    for (i in 1..8) { //TODO: 7?
        if (location.x + i !in 0..7) { //TODO: This could be optimized. y-i<0
            break
        }
        if (board.board[location.x + i][location.y]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + i, y = location.y))
        }
        if (board.board[location.x + i][location.y] != null) {
            break;
        }
    }

    //South
    for (i in 1..8) { //TODO: 7?
        if (location.y + i !in 0..7) { //TODO: This could be optimized. y-i<0
            break
        }
        if (board.board[location.x][location.y + i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x, y = location.y + i))
        }
        if (board.board[location.x][location.y + i] != null) {
            break;
        }
    }

    //West
    for (i in 1..8) { //TODO: 7?
        if (location.x - i !in 0..7) { //TODO: This could be optimized. y-i<0
            break
        }
        if (board.board[location.x - i][location.y]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - i, y = location.y))
        }
        if (board.board[location.x - i][location.y] != null) {
            break;
        }
    }
    return possibleMoves
}
fun getBishopMoves(board: Board, location: Location): MutableList<Location> {
    val possibleMoves = mutableListOf<Location>()
    val piece = board.board[location.x][location.y]

    // First we move northwest (towards (0,0))
    for (i in 1..7) {
        if (location.x - i !in 0..7 || location.y - i !in 0..7) {
            break
        }
        if (board.board[location.x - i][location.y - i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - i, y = location.y - i))
        }
        if (board.board[location.x - i][location.y - i] != null) {
            break
        }
    }

    // Now we move southeast
    for (i in 1..7) {
        if (location.x + i !in 0..7 || location.y + i !in 0..7) {
            break
        }
        if (board.board[location.x + i][location.y + i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + i, y = location.y + i))
        }
        if (board.board[location.x + i][location.y + i] != null) {
            break
        }
    }

    // Northeast
    for (i in 1..7) {
        if (location.x + i !in 0..7 || location.y - i !in 0..7) {
            break
        }
        if (board.board[location.x + i][location.y - i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + i, y = location.y - i))
        }
        if (board.board[location.x + i][location.y - i] != null) {
            break
        }
    }

    // Southwest
    for (i in 1..7) {
        if (location.x - i !in 0..7 || location.y + i !in 0..7) {
            break
        }
        if (board.board[location.x - i][location.y + i]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - i, y = location.y + i))
        }
        if (board.board[location.x - i][location.y + i] != null) {
            break
        }
    }
    return possibleMoves
}

fun getKingMoves(board: Board, location: Location): MutableList<Location> {
    val piece = board.board[location.x][location.y]!!
    val possibleMoves = mutableListOf<Location>()

    //SOUTHEAST
    if ((maxOf(location.x + 1, location.y + 1) < 8)) {
        if (board.board[location.x + 1][location.y + 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + 1, y = location.y + 1))
        }
    }

    //SOUTH
    if ((location.y + 1) < 8) {
        if (board.board[location.x][location.y + 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x, y = location.y + 1))
        }
    }

    //SOUTHWEST
    if (location.x - 1 >= 0 && location.y + 1 < 8) {
        if (board.board[location.x - 1][location.y + 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - 1, y = location.y + 1))
        }
    }

    //WEST
    if ((location.x - 1) >= 0) {
        if (board.board[location.x - 1][location.y]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - 1, y = location.y))
        }
    }

    //NORTHWEST
    if ((minOf(location.x - 1, location.y - 1) >= 0)) {
        if (board.board[location.x - 1][location.y - 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x - 1, y = location.y - 1))
        }
    }

    //NORTH
    if ((location.y - 1) >= 0) {
        if (board.board[location.x][location.y - 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x, y = location.y - 1))
        }
    }

    //NORTHEAST
    if ((location.x + 1 < 8 && location.y - 1 > -1)) {
        if (board.board[location.x + 1][location.y - 1]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + 1, y = location.y - 1))
        }
    }

    //EAST
    if ((location.x + 1) < 8) { //TODO: FIX
        if (board.board[location.x + 1][location.y]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(x = location.x + 1, y = location.y))
        }
    }
    return possibleMoves
}

fun getKnightMoves(board: Board, location: Location): MutableList<Location> {
    val piece = board.board[location.x][location.y]!!
    val possibleMoves = mutableListOf<Location>()
    //val isOtherPlayer = piece.isWhite.not() // Not used, but isn't this a nice thing to use generally?

    val lMoves: List<Location> = listOf(
        Location(-2, -1), Location(-2, 1),  // Moves up
        Location(2, -1), Location(2, 1),    // Moves down
        Location(-1, -2), Location(1, -2),  // Moves left
        Location(-1, 2), Location(1, 2)     // Moves right
    )

    for (move in lMoves) {
        val desX = location.x + move.x
        val desY = location.y + move.y
        if (desX < 0 || desY < 0 || desX > 7 || desY > 7) {
            continue
        }
        val destination = board.board[desX][desY]
        if (board.board[desX][desY]?.isWhite != piece?.isWhite) {
            possibleMoves.add(Location(desX, desY))
        }
    }
    return possibleMoves
}

fun move(board: Board, previousLocation: Location, nextLocation: Location) {
    //print("$previousLocation, $nextLocation")
    val piece = board.board[previousLocation.x][previousLocation.y]
    if (piece != null) {
        if (previousLocation.y == 0 || previousLocation.y == 7) {
            if (piece.type == 'P') {
                board.board[nextLocation.x][nextLocation.y] = Piece(type = 'Q', isWhite = piece.isWhite, value = 90, moveStrategy = ::getQueenMoves)
            }
            board.board[nextLocation.x][nextLocation.y] = board.board[previousLocation.x][previousLocation.y]
        }
        else {
            board.board[nextLocation.x][nextLocation.y] = board.board[previousLocation.x][previousLocation.y]
        }
        board.board[previousLocation.x][previousLocation.y] = null
    }
}

/**
 * Calculates the piece advantage of white
 * Might be optimized by just giving each player a list of pieces,
 * so we don't have to search the whole matrix every time we calculate the advantage
 */
fun getWhiteAdvantage(board: Board): Int{
    var sum = 0
    var piece: Piece? = null

    for (i in 0..7){
        for (j in 0..7){
            piece = board.board[i][j]
            if (piece == null) {
                continue
            }
            if (piece.isWhite){ //This should be reduceable in some way
                if (piece.type == 'K') {
                    sum += evaluateKingSafety(board, Location(i, j), true)
                }
                else {
                    sum += piece.value
                    if (piece.type == 'P') {
                        sum += checkNumberOfPawnFriends(board = board, Location(i, j), isWhite = true)
                    }
                }

                /*
                if (!piece.hasMoved) {
                    sum -= 1
                }

                 */
            } else {
                if (piece.type == 'K') {
                    sum -= evaluateKingSafety(board, Location(i, j), false)
                }
                else {
                    sum -= piece.value
                    if (piece.type == 'P') {
                        sum -= checkNumberOfPawnFriends(board = board, Location(i, j), isWhite = false)
                    }
                }

                /*
                if (!piece.hasMoved) {
                    sum += 1
                }

                 */
            }
        }
    }
    return sum
}

// Deprecated
fun getPieceValue(piece: Piece?): Int {
    if (piece == null) return 0
    when (piece.type) {
        'K' -> return 12 //TODO: I'm not sure if setting this makes sense. Maybe goal-check should be separate?
        'Q' -> return 9
        'R' -> return 5
        'B' -> return 3
        'N' -> return 3
        'P' -> return 1
    }
    return 0
}

fun getAllPossibleMoves(board: Board, isWhite: Boolean): MutableList<Move> {
    val allPossibleMoves = mutableListOf<Move>()
    for (i in 0 .. 7) {
        for (j in 0 .. 7) {
            val currentLocation = Location(i, j)
            val piece = board.board[i][j]
            if (piece != null && piece.isWhite == isWhite) {
                val potentialMoves = piece.moveStrategy(board, currentLocation)
                for (endLocation in potentialMoves) {
                    if (endLocation.y in 0 .. 7 && endLocation.x in 0 .. 7) {
                        allPossibleMoves.add(Move(start = currentLocation, end = endLocation))
                    }
                }
            }
        }
    }
    return allPossibleMoves
}

fun evaluateKingSafety(board: Board, kingLocation: Location, isWhite: Boolean): Int {
    var score = 0

    score -= checkPenalty(board, kingLocation, isWhite)
    score -= checkmatePenalty(board, kingLocation)
    score -= surroundedByFriendsScore(board, kingLocation)
    //println("surroundedByFriendsScore " + surroundedByFriendsScore(board, kingLocation))

    return score
}

fun checkNumberOfPawnFriends(board: Board, pawnLocation: Location, isWhite: Boolean): Int {
    var score = 0

    val possibleFriendLocations: List<Pair<Int, Int>> = listOf(
        Pair(-1,-1), Pair(-1,1),
        Pair(1,-1), Pair(1,1),
    )

    for (offset in possibleFriendLocations) {
        val possibleFriendLocation = Location(pawnLocation.x + offset.first, pawnLocation.y + offset.second)
        if (possibleFriendLocation.x in 0..7 && possibleFriendLocation.y in 0..7) {
            if (board.board[possibleFriendLocation.x][possibleFriendLocation.y]?.isWhite == isWhite &&
                board.board[possibleFriendLocation.x][possibleFriendLocation.y]?.type == 'P' ||
                board.board[possibleFriendLocation.x][possibleFriendLocation.y]?.type == 'B'
            ) {
                score += 2
            }
        }
    }
    return score
}

fun surroundedByFriendsScore(board: Board, kingLocation: Location): Int {
    var score = 0
    val isWhite: Boolean = board.board[kingLocation.x][kingLocation.y]?.isWhite ?: return 0
    val possibleFriendLocations: List<Pair<Int, Int>> = listOf(
        Pair(-1,-1), Pair(-1,0), Pair(-1,1),
        Pair(0,-1), Pair(0,1),
        Pair(1,-1), Pair(1,0), Pair(1,1),
    )

    for (offset in possibleFriendLocations) {
        val possibleFriendLocation = Location(kingLocation.x + offset.first, kingLocation.y + offset.second)
        if (possibleFriendLocation.x in 0..7 && possibleFriendLocation.y in 0..7) {
            val possibleFriend = board.board[possibleFriendLocation.x][possibleFriendLocation.y]
            if (possibleFriend != null) {
                if (possibleFriend.isWhite == isWhite) {
                    score += 10
                }
            }
        }
    }

    if (score > 60) {
        score -= 30
    }

    return score
}

fun isKingInCheck(board: Board, kingLocation: Location, isWhite: Boolean): Boolean {
    val otherPlayerNextMoves = getAllPossibleMoves(board = board, isWhite = !isWhite).map { it.end }
    return otherPlayerNextMoves.contains(kingLocation)
}

fun checkPenalty(board: Board, kingLocation: Location, isWhite: Boolean): Int {
    return if (isKingInCheck(board, kingLocation, isWhite)) {
        15
    }
    else 0

}

fun checkmatePenalty(board: Board, kingLocation: Location): Int {
    val piece = board.board[kingLocation.x][kingLocation.y]
    if (piece == null) {
        print("No king found at given location. ")
        exitProcess(1)
    }
    val isWhite = piece!!.isWhite
    if (isKingInCheck(board, kingLocation, isWhite) && !hasLegalMoves(board, isWhite)) {
        return -50000 // Large penalty
    }
    return 0
}

fun hasLegalMoves(board: Board, isWhite: Boolean): Boolean {
    for (i in 0 .. 7) {
        for (j in 0 .. 7) {
            val piece = board.board[i][j]
            if (piece != null && piece.isWhite == isWhite) {
                val currentLocation = Location(i, j)
                val possibleMoves = piece.moveStrategy(board, currentLocation)
                for (move in possibleMoves) {
                    val simulatedBoard = board.copyBoard(isWhite)
                    move(simulatedBoard, currentLocation, move)
                    val kingLocation = findPieceLocation(simulatedBoard, isWhite, 'K')
                    if (kingLocation != null && !isKingInCheck(simulatedBoard, kingLocation, isWhite)) {
                        return true  // The player has a legal move or more
                    }
                }
            }
        }
    }
    return false // No legal moves
}

// If you want to know where a given piece is
fun findPieceLocation(board: Board, isWhite: Boolean, pieceType: Char): Location? {
    for (i in 0 .. 7) {
        for (j in 0 .. 7) {
            val piece = board.board[i][j]
            if (piece != null && piece.type == pieceType && piece.isWhite == isWhite) {
                return Location(i, j)
            }
        }
    }
    return null
}

fun getLegalMoves(board: Board, isWhite: Boolean): MutableList<Move> {
    val legalMoves = mutableListOf<Move>()
    val allPossibleMoves = getAllPossibleMoves(board, isWhite)

    for (move in allPossibleMoves) {
        val simulatedBoard = board.copyBoard(isWhite)
        move(simulatedBoard, move.start, move.end)
        val kingLocation = findPieceLocation(simulatedBoard, isWhite, 'K') ?: continue

        if (!isKingInCheck(simulatedBoard, kingLocation, isWhite)) {
            legalMoves.add(move)  // Should not leave the king in check
        }
    }
    return legalMoves
}
