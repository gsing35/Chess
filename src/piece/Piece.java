package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {
	
	public Type type;
	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public Piece hittingP;
	public boolean moved = false;
	public boolean twoStepped;
	
	public Piece(int color, int col, int row) {
		
		this.color = color;
		this.row = row;
		this.col = col;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}
	public BufferedImage getImage(String imagePatch) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePatch + ".png"));
			if (image == null) {
	            System.err.println("Image not found: " + imagePatch + ".png");
	        }
		} catch(IOException e) {
			 System.err.println("Error loading image: " + imagePatch + ".png");
			e.printStackTrace();
		}
		return image;
	}
	
	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	
	public int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image, x , y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	
	public int getCol(int x) {
		return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public void updatePiece() {
	    // Update grid-based positions
	    col = getCol(x);
	    row = getRow(y);
	    
	  //Check En Pas
	    if(type == Type.PAWN) {
	    	if(Math.abs(row - preRow) ==2) {
	    		twoStepped = true;
	    	}
	    }

	    // Update pixel-based positions based on grid
	    x = getX(col);
	    y = getY(row);

	    // Update previous positions
	    preCol = col;
	    preRow = row;
	    moved = true;
	    
	  
	}
	
	public boolean canMove(int targetRow, int targetCol) {
		return false;
	}
	
	public boolean isWithinBoard(int targetCol, int targetRow) {
		if(targetRow >= 0 && targetRow <= 7 && targetCol >= 0 && targetCol <= 7) {
			return true;
		}
		return false;
		
	}
	
	public void resetPositsion() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}
	
	public Piece getHittingP(int targetCol, int targetRow) {
		for(Piece piece : GamePanel.simPieces) {
			if(piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public boolean isValidSquare(int targetCol, int targetRow) {
		hittingP = getHittingP(targetCol, targetRow);
		//Square not Occupied
		if(hittingP == null) {
			return true;
		}
		else {	//occupied square
			if(hittingP.color != this.color) { 	//Same color capture piece
			return true;
			}
			else {
				hittingP = null;
				return false;
			}
			
		}
	}
	
	public boolean isSameSquare(int targetCol, int targetRow) {
		if(targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	
	public boolean pieceIsInPath(int targetCol, int targetRow) {
		//-1/+1 skips current positon of the piece
			//Move left
		for(int c = preCol -1; c> targetCol; c--) {
			for(Piece piece : GamePanel.simPieces) {
				if(piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		//Move Right
		for(int c = preCol + 1; c< targetCol; c++) {
			for(Piece piece : GamePanel.simPieces) {
				if(piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		//Move Up
		for(int r = preRow -1; r> targetRow; r--) {
			for(Piece piece : GamePanel.simPieces) {
				if(piece.row == r && piece.col == targetCol) {
					hittingP = piece;
					return true;
				}
			}
		}
		//Move down
		for(int r = preRow +1; r< targetRow; r++) {
			for(Piece piece : GamePanel.simPieces) {
				if(piece.row == r && piece.col == targetCol) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean pieceIsInPathDiagonal(int targetCol, int targetRow) {
		//-1/+1 skips current positon of the piece
			//Move up left
		if(targetRow < preRow) {
			
			//Move up left
			for(int c = preCol -1; c> targetCol; c--) {
				int diff = Math.abs(c - preCol);	//Used to calculate the row postion and c calculates the column position
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			
		}
		
		//Move up Right
		for(int c = preCol +1; c< targetCol; c++) {
			int diff = Math.abs(c - preCol);
			for(Piece piece : GamePanel.simPieces) {
				if(piece.col == c && piece.row == preRow - diff) {
					hittingP = piece;
					return true;
				}
			}
		}
		
	
		if(targetRow > preRow) {
			//Move down left
			for(int c = preCol -1; c> targetCol; c--) {
				int diff = Math.abs(c -  preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			for(int c = preCol +1; c< targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == c && piece.row == preRow + diff) {
						hittingP = piece;
						return true;
					}
				}
			
		}
		}

		return false;
	}
	public int getIndex() {
		for(int i = 0; i < GamePanel.simPieces.size(); i++) {
			if(GamePanel.simPieces.get(i) == this) {
				return i;
			}
		}
		return 0;
	}
	

	
	

	
	



}
