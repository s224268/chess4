import dataClasses.*
import gameLogic.*
import view.*


suspend fun main() {
    var board = getInitialBoard()
    var whiteNow = true

    val depth = startText()

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
            printBoard(board)

        }
        else {
            println("[AI is thinking]")
            board = evalBestMove(board = board.copyBoard(), depth = depth, isWhite = false)
        }

        whiteNow = !whiteNow

    }
}