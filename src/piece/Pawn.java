package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		
		type = Type.PAWN;
		// TODO Auto-generated constructor stub
		
		if (color == GamePanel.WHITE ) {
			image = getImage("/piece/w-pawn");
		}
		
		else {
			image = getImage("/piece/b-pawn");
		}	
		if (image == null) {
	        System.err.println("Failed to load image for color: " + color);
	    }
	
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			
			//Define move value based on color
			int moveValue;
			if(color == GamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			
			//Check hitting piece
			hittingP = getHittingP(targetCol, targetRow);
			
			
			
			//2 sqaure movement
			if(moved == false && targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && pieceIsInPath(targetCol, targetRow) == false ) {
				return true;
			}
				
				
			//1 square movement
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null ) {
				return true;
			}
			
			//capture piece
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color) {
				return true;
			}
			
			//En pas
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
	
				for(Piece p: GamePanel.simPieces) {
					if(p.col == targetCol && p.row == preRow && p.twoStepped ==true) {
						hittingP =p;
						return true;
					}
				}
			}
			
			
			
		}
		
		return false;
		}
	
	

}
