package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

	public Queen(int color, int col, int row) {
		super(color, col, row);
		type = Type.QUEEN;
		// TODO Auto-generated constructor stub
		
		
		if (color == GamePanel.WHITE ) {
			image = getImage("/piece/w-queen");
		}
		
		else {
			image = getImage("/piece/b-queen");
		}
	
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
		
			if((Math.abs(targetCol - preCol) <= 7 ) && (Math.abs(targetRow - preRow)) == 0 || ((Math.abs(targetCol - preCol) == 0 ) && (Math.abs(targetRow - preRow)) <= 7)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsInPath(targetCol, targetRow) == false) {
					return true;
				}
			}
			if(Math.abs(targetRow - preRow) == Math.abs(targetCol - preCol)) {
				if(isValidSquare(targetCol, targetRow) && pieceIsInPathDiagonal(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		
		return false;
		
	}

}