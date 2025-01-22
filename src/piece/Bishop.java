package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int color, int col, int row) {
		super(color, col, row);
		type = Type.BISHOP;
		// TODO Auto-generated constructor stub
		
		
		if (color == GamePanel.WHITE ) {
			image = getImage("/piece/w-bishop");
		}
		
		else {
			image = getImage("/piece/b-bishop");
		}
	
	}
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow)) {
		
			if((Math.abs(targetCol - preCol) == (Math.abs(targetRow - preRow)) && (Math.abs(targetCol - preCol)) > 0 && isSameSquare(targetCol, targetRow) == false )) {
				if(isValidSquare(targetCol, targetRow) && pieceIsInPathDiagonal(targetCol, targetRow) == false) {
					return true;
				}
			}
		}
		
		
		return false;
		
	}

}