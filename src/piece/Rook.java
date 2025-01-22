package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

	public Rook(int color, int col, int row) {
		super(color, col, row);
		type = Type.ROOK;
		// TODO Auto-generated constructor stub
		
		
		if (color == GamePanel.WHITE ) {
			image = getImage("/piece/w-rook");
		}
		
		else {
			image = getImage("/piece/b-rook");
		}
	
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow)) {
		
			if((Math.abs(targetCol - preCol) <= 7 ) && (Math.abs(targetRow - preRow)) == 0 && (Math.abs(targetCol - preCol) > 0 ) || ((Math.abs(targetCol - preCol) == 0 ) && (Math.abs(targetRow - preRow)) <= 7) && isSameSquare(targetCol, targetRow) == false ) {
				if(isValidSquare(targetCol, targetRow) && pieceIsInPath(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		
		return false;
		
	}

}
