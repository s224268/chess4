import dataClasses.*
import gameLogic.*
import view.*


suspend fun main() {
    var board = getInitialBoard()
    var whiteNow = true

    startText()

    while (true){
        print("Current player is ")
        if (whiteNow) {
            println("white")
        } else {
            println("black")
        }

        if (whiteNow) {
            clearScreen()


            printBoard(board)

            var pieceToMove = Location(0, 0)
            var possibleMoves: List<Move> = emptyList()
            while (possibleMoves.isEmpty()) {
                pieceToMove = whichPiece()
                possibleMoves = getAllMovesFromPiece(board, pieceToMove)
            }

            val possibleDestinations = possibleMoves.map { it.end }
            printMoves(possibleDestinations)
            val node = Node(board = board)
            evalNode(node)

            val desiredMove = whichMove(possibleDestinations)
            move(board, pieceToMove, desiredMove)
            clearScreen()
            println("[AI is thinking]")
            printBoard(board)

        }
        else {
            board = evalBestMove(board = board.copyBoard(isItWhitesTurn = false), depth = 5, isWhite = false)
        }

        whiteNow = !whiteNow

    }
}