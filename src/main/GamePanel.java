package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{

	public static final int HEIGHT = 800;
	public static final int WIDTH = 1100;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	
	//Pieces
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promoPieces = new ArrayList<>();
	Piece activeP;
	Piece checkingP;
	public static Piece castlingP;
	
	//Color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	
	//Booleans
	boolean canMove;
	boolean validSquare;
	boolean promo;
	boolean gameOver;
	boolean stalemate;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(mouse);   //movement
		addMouseListener(mouse); //action
		
		setPieces();		
		copyPieces(pieces, simPieces);
	}

	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void setPieces() {
		
		//White Team
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, 4, 7));
		
		//Black Team
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new King(BLACK, 4, 0));
		
	}
	
	public void testIllegal() {
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new King(WHITE, 3, 7));
		pieces.add(new Bishop(BLACK, 0, 3));
		pieces.add(new Queen(BLACK, 1, 4));
		pieces.add(new Rook(BLACK, 4, 5));
		pieces.add(new King(BLACK, 5, 7));
	}
	
	public void testPromo() {
		pieces.add(new Pawn(BLACK, 3, 0));
		pieces.add(new Pawn(WHITE, 4, 3));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//GAMELOOP
		double drawInterval = 100000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	
	private void update() {
		if(promo) {
			promoting();
		}
		
		else if(!gameOver && !stalemate){
			if(mouse.pressed) {
				
				if(activeP == null) {
					
					for(Piece piece : simPieces) {
						
						if(piece.color == currentColor &&
								piece.col == mouse.x/Board.SQUARE_SIZE &&		//Check if colorn matches turn color and checks if x y matches where mouse is
								piece.row == mouse.y/Board.SQUARE_SIZE) {
							
							activeP = piece;
						}
					}
				}
				
				else {
					simulate();			//If player is holding piece
				}
			}
				
				//Mouse bottom relased
			if(mouse.pressed == false) {
				if(activeP != null) {
					if(validSquare) {
						copyPieces(simPieces, pieces);	//Update pieces list in case one got taken out
						activeP.updatePiece();
						
						if(isKingInCheck() && isCheckMate()) {
							gameOver = true;
						}
						else if(isStaleMate() && !isCheckMate()) {
							stalemate = true;
						}
						else {
							if(canPromote()) {
								promo = true;
							}
							else {
								changeTurn();
							}
						}
						
						if(castlingP != null) {
							castlingP.updatePiece();
						}
						
					}
					else {
						copyPieces(pieces, simPieces);
						activeP.resetPositsion();
						activeP = null;
						}
				}
				}
		}
	}
	
	private boolean isStaleMate() {
		
		int count = 0;
		for(Piece p :simPieces) {
			if(p.color != currentColor) {
				count++;
			}
		}
		
		if(count == 1) {
			if(!kingCanMove(getKing(true))) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isKingInCheck() {
	    Piece king = getKing(true);

	    if (king == null) {
	        System.out.println("Opponent's king not found.");
	        return false; // Avoid NullPointerException
	    }

	    if (activeP.canMove(king.col, king.row)) {
	        checkingP = activeP;
	        return true;
	    }

	    return false;
	}
	
	private boolean opponentCanCaptureKing() {
		
		Piece king = getKing(false);
		
		for(Piece p : simPieces) {
			if(p.color != king.color && p.canMove(king.col, king.row)) {
				return true;
			}
		}
		
		return false;
	}

	
	private Piece getKing(boolean opponent) {
	    Piece king = null;

	    for (Piece p : simPieces) {
	        if (opponent) {
	            // Find the opponent's king
	            if (p.type == Type.KING && p.color != currentColor) {
	                king = p;
	                break; // Exit the loop once the king is found
	            }
	        } else {
	            // Find the current player's king
	            if (p.type == Type.KING && p.color == currentColor) {
	                king = p;
	                break; // Exit the loop once the king is found
	            }
	        }
	    }

	    if (king == null) {
	        System.out.println("King not found. Opponent: " + opponent);
	    }

	    return king;
	}

	private void promoting() {
		// TODO Auto-generated method stub
		if(mouse.pressed) {
			for(Piece p : promoPieces) {
				if(p.col == mouse.x/Board.SQUARE_SIZE && p.row == mouse.y/Board.SQUARE_SIZE) {
					switch(p.type) {
					case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
					break;
					case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
					break;
					case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
					break;
					case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
					break;
					default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promo = false;
					changeTurn();
				}
			}
		}
	}

	private void simulate() {
		// TODO Auto-generated method stub
		canMove = false;
		validSquare = false;
		//Reset pieces every loop
		copyPieces(pieces, simPieces);
		
		//Reset castlingP position 
		if(castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.preCol);
			castlingP = null;	
		}
		copyPieces(pieces, simPieces);
		activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;		// - Half size Makes mouse center to image and not top left
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		//Check if piece if hovering over moveable square
		if(activeP.canMove(activeP.col, activeP.row)) {
			canMove = true;
			
			//Check if hitting enemy piece
			if(activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			checkCastling();
			
			if(!isIllegal(activeP) && !opponentCanCaptureKing()) {
				validSquare = true;
			}
			else
			validSquare = false;
		}
	}
	
	private boolean isIllegal(Piece king) {
		if(king.type == Type.KING) {
			for(Piece p : simPieces) {
				if(p != king && p.color != king.color && p.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isCheckMate() {
	    Piece king = getKing(true); // Get opponent's king

	    if (kingCanMove(king)) {
	        return false; // If the king can move to a safe square, it's not checkmate
	    }

	    // Handle double check: The only option is for the king to move
	    int attackers = countAttackers(king);
	    if (attackers > 1) {
	        return true; // Double check, no way to block
	    }

	    // Find the attacking piece (checkingP should already be set if the king is in check)
	    if (attackers == 1) {
	        int colDiff = Math.abs(checkingP.col - king.col);
	        int rowDiff = Math.abs(checkingP.row - king.row);

	        // Vertical attack
	        if (colDiff == 0) {
	            int direction = (checkingP.row < king.row) ? 1 : -1;
	            for (int row = checkingP.row + direction; row != king.row; row += direction) {
	                if (canBlockOrCapture(checkingP.col, row)) {
	                    return false; // A piece can block the attack
	                }
	            }
	        }

	        // Horizontal attack
	        if (rowDiff == 0) {
	            int direction = (checkingP.col < king.col) ? 1 : -1;
	            for (int col = checkingP.col + direction; col != king.col; col += direction) {
	                if (canBlockOrCapture(col, checkingP.row)) {
	                    return false; // A piece can block the attack
	                }
	            }
	        }

	        // Diagonal attack
	        if (colDiff == rowDiff) {
	            int colDirection = (checkingP.col < king.col) ? 1 : -1;
	            int rowDirection = (checkingP.row < king.row) ? 1 : -1;
	            int col = checkingP.col + colDirection;
	            int row = checkingP.row + rowDirection;
	            while (col != king.col && row != king.row) {
	                if (canBlockOrCapture(col, row)) {
	                    return false; // A piece can block the attack
	                }
	                col += colDirection;
	                row += rowDirection;
	            }
	        }
	    }

	    return true; // If no piece can block or capture, it's checkmate
	}

	
	private boolean kingCanMove(Piece king) {
	    for (int colDiff = -1; colDiff <= 1; colDiff++) {
	        for (int rowDiff = -1; rowDiff <= 1; rowDiff++) {
	            if (colDiff == 0 && rowDiff == 0) {
	                continue; // Skip the current position
	            }
	            if (isValidMove(king, colDiff, rowDiff)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
	    int newCol = king.col + colPlus;
	    int newRow = king.row + rowPlus;

	    // Check bounds
	    if (newCol < 0 || newCol >= 8 || newRow < 0 || newRow >= 8) {
	        return false;
	    }

	    // Simulate the move and check if it is safe
	    int originalCol = king.col;
	    int originalRow = king.row;
	    king.col = newCol;
	    king.row = newRow;

	    boolean valid = !isIllegal(king);

	    // Reset king's position
	    king.col = originalCol;
	    king.row = originalRow;

	    return valid;
	}
	
	private int countAttackers(Piece king) {
	    int count = 0;
	    for (Piece p : simPieces) {
	        if (p.color != currentColor && p.canMove(king.col, king.row)) {
	            checkingP = p; // Save the last attacking piece as `checkingP`
	            count++;
	        }
	    }
	    return count;
	}
	
	private boolean canBlockOrCapture(int col, int row) {
	    for (Piece p : simPieces) {
	        if (p.color == currentColor && p != checkingP && p.canMove(col, row)) {
	            return true;
	        }
	    }
	    return false;
	}



	
	public void checkCastling() {
		if(castlingP != null) {
			if(castlingP.col == 0) {
				castlingP.col +=3;
			}
			else if(castlingP.col == 7) {
				castlingP.col -=2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	
	private boolean canPromote() {
		
		if(activeP.type == Type.PAWN) {

			if(activeP.color == WHITE && activeP.row == 0) {	
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor, 9, 2));
				promoPieces.add(new Knight(currentColor, 9, 3));
				promoPieces.add(new Bishop(currentColor, 9, 4));
				promoPieces.add(new Queen(currentColor, 9, 5));
				return true;
				
			}
			else if(activeP.color == BLACK && activeP.row == 7) {
				promoPieces.add(new Rook(currentColor, 9, 2));
				promoPieces.add(new Knight(currentColor, 9, 3));
				promoPieces.add(new Bishop(currentColor, 9, 4));
				promoPieces.add(new Queen(currentColor, 9, 5));
				return true;
			}
		}
		
		
		return false;
	}
	
	public void changeTurn() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			for(Piece p: simPieces) {
				if(p.color == BLACK && p.twoStepped==true) {
					p.twoStepped=false;
				}
			}
		}
		else {
			currentColor = WHITE;
			for(Piece p: simPieces) {
				if(p.color == WHITE && p.twoStepped==true) {
					p.twoStepped=false;
				}
			}
		}
		activeP = null;
	}




	public void paintComponent(Graphics g) {		//repaint
		super.paintComponent(g);
	
		Graphics2D g2 = (Graphics2D)g;
	
		board.draw(g2);
	
		for(Piece p : simPieces) {
			p.draw(g2);
			}
		
		//g2.dispose();
		
		if(activeP != null) {
			if(canMove) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,  Board.SQUARE_SIZE,  Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
				else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,  Board.SQUARE_SIZE,  Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}
			
			//draw piece so it wont be hidden behind square
			activeP.draw(g2);
		}
		
		//Turn message
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.WHITE);
		
		
		if(promo) {
			g2.drawString("Promote to:", 840, 150);
			for(Piece p : promoPieces) {
				g2.drawImage(p.image, p.getX(p.col), p.getY(p.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		
		else {
			if(currentColor == WHITE) {
				g2.drawString("White's Turn", 840, 550);
				if(checkingP != null && checkingP.color == BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 600);
					g2.drawString("is in check!", 840, 700);
				}
			}
			else {
				g2.drawString("Black's Turn", 840, 250);
				if(checkingP != null && checkingP.color == WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 100);
					g2.drawString("is in check!", 840, 150);
				}
			}
			
			if(gameOver) {
				String s = "";
				if(currentColor == WHITE) {
					s = "White Wins";
				}
				else {
					s = "Black Wins";
				}
				g2.setFont(new Font("Arial", Font.PLAIN, 90));
				g2.setColor(Color.green);
				g2.drawString(s, 200, 420);
			}
			if(stalemate) {
				g2.setFont(new Font("Arial", Font.PLAIN, 90));
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString("Stalemate", 200, 420);
			}
			
		}
		
	}
}
