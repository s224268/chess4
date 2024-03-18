package view

import dataClasses.Board
import dataClasses.Location

fun printBoard(board: Board) {
    //clearScreen()

    print("\t")
    for (i in 0..7){
        print( 'A' + i + "\t")
    }

    println()
    for (y in 0..7){
        print((y+1).toString() + "\t")
        for(x in 0..7){
            val piece = board.board[x][y]
            if (piece == null){
                print(".\t")
            } else {
                print(pieceToUniCode(piece) + "\t")  // Print "." for empty spaces
            }
        }
        println()
    }
    println()
}

fun printMoves(list: List<Location>){
    println("Possible moves:")
    list.forEachIndexed{ index, location ->
        println((index+1).toString() + ": " + (location.x + 'A'.code).toChar() + (location.y + 1).toString()) //TODO: This function exists already

    }
}

private fun chessNotationToLocation(input: String): Location{
    //Some error checking here maybe?
    val input = input.lowercase()
    return Location(x = input[0].code - 'a'.code, y = input[1].digitToInt()-1)
}

fun locationToChessNotation(location: Location): String{
    return ((location.y+1).toString() + (location.x+'a'.code).toChar())
}

fun whichPiece(): Location{
    println("Which piece do you want to move?")
    return (chessNotationToLocation(readln()))
}

fun whichMove(possibleMoves : List<Location>): Location {
    println("Which move do you want to make")
    val input = readln()
    if (input[0].isLetter()){ //We allow notation or number
        return chessNotationToLocation(input)
    }

    return possibleMoves[input.toInt()-1]
}

fun startText(){
    println("You are playing chess, but with slightly simplified rules. There is no en pessant or Castling, and all pawn promotions are to queens.")
    println("You may be able to make illegal moves. It is up to you to follow the rules of chess and make sure not to leave your king in check")
    println("When selecting a piece to move, write using chess notation, such as 'B2' \nThen, when selecting where to move the piece, write either the number from the list (e.g. '3'), or the location you want to move to, e.g. 'B4'")

    println("Please note that colors may look inverted in dark mode. This is a white queen: ♔")
    println("Press enter to continue")
    readln()
    clearScreen()
}

fun clearScreen(){
    println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
}