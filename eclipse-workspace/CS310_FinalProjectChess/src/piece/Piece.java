package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;

public class Piece {
    
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    
    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }
    
    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
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
    
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return 0; 
    }
    
    public void updatePosition() {
        preCol = col;     
        preRow = row;
        x = getX(col);
        y = getY(row);
    }
    
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
    
    public boolean isSameSquare(int targetCol, int targetRow) {
        return targetCol == preCol && targetRow == preRow;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) return true;
        if (hittingP.color != this.color) return true;
        // same color piece blocks
        hittingP = null;
        return false;
    }

    public boolean pieceOnDiagonal(int targetCol, int targetRow) {
        int dCol = targetCol - preCol;
        int dRow = targetRow - preRow;

        if (Math.abs(dCol) != Math.abs(dRow) || dCol == 0) {
            return false; 
        }

        int colStep = Integer.compare(dCol, 0); 
        int rowStep = Integer.compare(dRow, 0); 

        int c = preCol + colStep;
        int r = preRow + rowStep;

        while (c != targetCol && r != targetRow) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
            c += colStep;
            r += rowStep;
        }

        hittingP = null;
        return false;
    }

    public boolean pieceOnStraight(int targetCol, int targetRow) {
  
        if (preRow == targetRow && preCol != targetCol) {
            int step = Integer.compare(targetCol, preCol); 
            int c = preCol + step;
            while (c != targetCol) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow) {
                        hittingP = piece;
                        return true;
                    }
                }
                c += step;
            }
            hittingP = null;
            return false;
        }

        if (preCol == targetCol && preRow != targetRow) {
            int step = Integer.compare(targetRow, preRow); 
            int r = preRow + step;
            while (r != targetRow) {
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == preCol && piece.row == r) {
                        hittingP = piece;
                        return true;
                    }
                }
                r += step;
            }
            hittingP = null;
            return false;
        }

        return false;
    }

    public static boolean isKingInCheck(int kingColor) {
        // Find the king
        Piece king = null;
        for (Piece p : GamePanel.simPieces) {
            if (p instanceof King && p.color == kingColor) {
                king = p;
                break;
            }
        }
        
        if (king == null) return false;

        for (Piece p : GamePanel.simPieces) {
            if (p.color != kingColor) {
                if (p.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    public static boolean isCheckmate(int kingColor) {
        if (!isKingInCheck(kingColor)) {
            return false; 
        }

        java.util.ArrayList<Piece> piecesCopy = new java.util.ArrayList<>(GamePanel.simPieces);

        for (Piece p : piecesCopy) {
            if (p.color == kingColor) {
                int originalCol = p.col;
                int originalRow = p.row;

                for (int c = 0; c < 8; c++) {
                    for (int r = 0; r < 8; r++) {
                        if (p.canMove(c, r)) {
                            Piece capturedPiece = p.hittingP;
                            
                            p.col = c;
                            p.row = r;

                            if (capturedPiece != null) {
                                int capturedIndex = GamePanel.simPieces.indexOf(capturedPiece);
                                if (capturedIndex >= 0) {
                                    GamePanel.simPieces.remove(capturedIndex);
                                }
                            }
    
                            boolean stillInCheck = isKingInCheck(kingColor);

                            p.col = originalCol;
                            p.row = originalRow;

                            if (capturedPiece != null) {
                                GamePanel.simPieces.add(capturedPiece);
                            }

                            if (!stillInCheck) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
 
        return true;
    }
    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}

