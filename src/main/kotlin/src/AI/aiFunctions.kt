package AI

import dataClasses.Board
import dataClasses.Location
import dataClasses.Move
import dataClasses.Piece
import gameLogic.getAllMovesFromPiece
import gameLogic.move

//These functions have largely been deprecated and moved into nodesAndTrees as part of the code for performance reasons
fun actions(board: Board /* Formally ACTIONS(S)*/): List<Move> {

    var piece: Piece?
    val moves: MutableList<Move> = mutableListOf()
    for (i in 0..7) {
        for (j in 0..7) {
            piece = board.board[i][j]
            if (piece != null) {
                moves.addAll(getAllMovesFromPiece(board, Location(x = i, y = j)))
            }
        }
    }
    return moves
}

/**
 * returns the board for a given move
 */
fun results(board: Board, move: Move): Board {
    val tempBoard: Board = board.copy()
    move(tempBoard, move.start, move.end)

    return tempBoard
}

/**
 * @return null if no-one has won, true if white has won, false if black has won
 * This may be the single most disgusting thing I have ever done. Should be redone.
 */
fun terminalTest(board: Board): Boolean?{

    var piece: Piece?
    for (i in 0..7) {
        for (j in 0..7) {
            piece = board.board[i][j]
            if (piece?.type == 'K'){
                return(piece.isWhite)
            }
        }
    }
    return null
}


/**
 * IDK why you'd use this, but it's in the task, so it's here
 */
fun players(board: Board): Boolean{
    return board.whitesTurn
}
