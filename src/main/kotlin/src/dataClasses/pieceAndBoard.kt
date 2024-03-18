package dataClasses

import gameLogic.*

data class Location(
    val x: Int,
    val y: Int
)
data class Piece (
    val type: Char,
    val isWhite: Boolean,
    val value: Int,
    val moveStrategy: (Board, Location) -> MutableList<Location>
)

data class Move(
    val start: Location,
    val end: Location
)

data class Player(
    val isWhite: Boolean,
    val pieces: MutableList<Piece> = mutableListOf()
)

data class Board(
    val board: Array<Array<Piece?>>,
    val whitesTurn: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        return board.contentDeepEquals(other.board)
    }

    override fun hashCode(): Int {
        return board.contentDeepHashCode()
    }

    fun copyBoard(isItWhitesTurn: Boolean): Board {
        val newBoard = Array(board.size) { i ->
            board[i].clone() // Clone the inner arrays
        }
        return Board(newBoard, isItWhitesTurn)
    }
}



fun getEmptyBoard(): Board {
    return Board(
        board = Array(8) { Array(8) { null } }, whitesTurn = true
    )
}


fun getInitialBoard(): Board {
    val board: Board = getEmptyBoard()
    board.board[0][0] = Piece(type = 'R', isWhite = true, value = 50, moveStrategy = ::getRookMoves)
    board.board[1][0] = Piece(type = 'N', isWhite = true, value = 30, moveStrategy = ::getKnightMoves)
    board.board[2][0] = Piece(type = 'B', isWhite = true, value = 30, moveStrategy = ::getBishopMoves)
    board.board[3][0] = Piece(type = 'Q', isWhite = true, value = 90, moveStrategy = ::getQueenMoves)
    board.board[4][0] = Piece(type = 'K', isWhite = true, value = 9000, moveStrategy = ::getKingMoves)   // Black king
    board.board[5][0] = Piece(type = 'B', isWhite = true, value = 30, moveStrategy = ::getBishopMoves) // Black bishop
    board.board[6][0] = Piece(type = 'N', isWhite = true, value = 30, moveStrategy = ::getKnightMoves) // Black knight
    board.board[7][0] = Piece(type = 'R', isWhite = true, value = 50, moveStrategy = ::getRookMoves)   // Black rook
    for (i in 0..7) {
        board.board[i][1] = Piece(type = 'P', isWhite = true, value = 10, moveStrategy = ::getPawnMoves)  // Black pawns
    }

// White pieces

    for (i in 0..7) {
        board.board[i][6] = Piece(type = 'P', isWhite = false, value = 10, moveStrategy = ::getPawnMoves)  // White pawns
    }
    board.board[0][7] = Piece(type = 'R', isWhite = false, value = 50, moveStrategy = ::getRookMoves)
    board.board[1][7] = Piece(type = 'N', isWhite = false, value = 30, moveStrategy = ::getKnightMoves)
    board.board[2][7] = Piece(type = 'B', isWhite = false, value = 30, moveStrategy = ::getBishopMoves)
    board.board[3][7] = Piece(type = 'Q', isWhite = false, value = 90, moveStrategy = ::getQueenMoves)
    board.board[4][7] = Piece(type = 'K', isWhite = false, value = 9000, moveStrategy = ::getKingMoves)    // White king
    board.board[5][7] = Piece(type = 'B', isWhite = false, value = 30, moveStrategy = ::getBishopMoves)  // White bishop
    board.board[6][7] = Piece(type = 'N', isWhite = false, value = 30, moveStrategy = ::getKnightMoves)  // White knight
    board.board[7][7] = Piece(type = 'R', isWhite = false, value = 50, moveStrategy = ::getRookMoves)    // White rook

    //board.board[6][5] = Piece(type = 'R', isWhite = true)

    return board
}

fun createPlayer(board: Board, isWhite: Boolean) : Player {
    val player = Player(isWhite)
    val start = if (isWhite) 0 else 6
    val end = if (isWhite) 1 else 7

    for (i in start..end) {
        for (j in 0..7) {
            val piece = board.board[i][j]
            if (piece != null) player.pieces.add(piece) else continue
        }
    }
    return player
}