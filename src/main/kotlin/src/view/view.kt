package view

import dataClasses.Board
import dataClasses.Location

fun printBoard(board: Board) {
    //clearScreen()

    println("\nPlease note that piece colors can look inverted in dark mode\n")
    print("\t")
    for (i in 0..7){
        print( 'A' + i + "\t")
    }
    /*
    println()
    print("\t")
    for (i in 0..7){
        print(i.toString() + "\t")
    }

     */
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

fun clearScreen(){
    println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
}