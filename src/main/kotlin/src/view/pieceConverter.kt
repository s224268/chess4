package view

import dataClasses.Location
import dataClasses.Piece

fun pieceToUniCode(piece: Piece) : Char{
    if (piece.isWhite){
        when (piece.type){
            'K' -> return '♔'
            'Q' -> return '♕'
            'R' -> return '♖'
            'B' -> return '♗'
            'N' -> return '♘'
            'P' -> return '♙'
        }
    }
    else {
        when (piece.type){
            'K' -> return '♚'
            'Q' -> return '♛'
            'R' -> return '♜'
            'B' -> return '♝'
            'N' -> return '♞'
            'P' -> return '♟'

        }
    }
    return 'E'
}