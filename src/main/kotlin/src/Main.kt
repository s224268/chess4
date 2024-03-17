import dataClasses.*
import gameLogic.*
import view.*

//TIP Press <shortcut raw="SHIFT"/> twice to open the Search Everywhere dialog and type <b>show whitespaces</b>,
// then press <shortcut raw="ENTER"/>. You can now see whitespace characters in your code.
suspend fun main() {
    var board = getInitialBoard()
    //val whitePlayer = createPlayer(isWhite = true, board = board)
    //val blackPlayer = createPlayer(isWhite = false, board = board)
    //val blackAI = true
    var whiteNow = true
    while (true){
        if (whiteNow) {
            clearScreen()

            /*val allPossibleDestinations = getAllPossibleMoves(board, board.whitesTurn).map { it.end }
            print("All ")
            printMoves(allPossibleDestinations)
            print("\n\n\n")*/

            print("Current player is ")
            if (whiteNow) {
                print("white")
            } else {
                print("black")
            }
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
        }
        else {
            //val isKingInCheck = isKingInCheck(board, Location(4,7), false)
            board = evalBestMove(board = board.copyBoard(isItWhitesTurn = false), depth = 5, isWhite = false)
        }

        whiteNow = !whiteNow

    }
}