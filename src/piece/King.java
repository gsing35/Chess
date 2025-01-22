package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece{

	public King(int color, int col, int row) {
		super(color, col, row);
		type = Type.KING;
		// TODO Auto-generated constructor stub
		
		
		if (color == GamePanel.WHITE ) {
			image = getImage("/piece/w-king");
		}
		
		else {
			image = getImage("/piece/b-king");
		}
	
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow)) {
			//Covers up/down & left/right												//Covers diagonal movement
			if((Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1) || ((Math.abs(targetRow - preRow) ==1) && (Math.abs(targetCol - preCol)) ==1)) {
				if(isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
			
			if(moved == false) {
				//right castling
				if(targetCol == preCol+2 && targetRow == preRow && pieceIsInPath(targetCol, targetRow) == false) {
					for(Piece p : GamePanel.simPieces) {
						if(p.col == preCol + 3 && p.row == preRow && p.moved == false) {
							GamePanel.castlingP = p;
							return true;
						}
					}
				}
				
				//left
				if(targetCol == preCol-2 && targetRow == preRow && pieceIsInPath(targetCol, targetRow) == false) {
					Piece pie[] = new Piece [2];
					for(Piece p : GamePanel.simPieces) {
						if(p.col == preCol - 3 && p.row == targetRow) {
							pie[0] = p;
						}
						if(p.col == preCol - 4 && p.row == targetRow) {
							pie[1] = p;
						}
						
						if(pie[0] == null && pie[1] != null && pie[1].moved == false) {
							GamePanel.castlingP = pie[1];
							return true;
						
						}
					}
				}
			}
		}
		
		
		return false;
		
	}

}