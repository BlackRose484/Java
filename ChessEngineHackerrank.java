import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessEngineHackerrank {

        // Count number of piece in chessboard and pos of black and white queen
        static class Feature {
            public int Qi;
            public int Qj;
            public int qi;
            public int qj;
            public List<int[]> wpieces = new ArrayList<>();
            public List<int[]> bpieces = new ArrayList<>();
        }

        // Able move for piece
        static class Move {
            public int srcRow;
            public int srcCol;
            public char srcPiece;
            public int dstRow;
            public int dstCol;
            public char dstPiece;
        }

        private static boolean isWhite(char c) {
            return "QNBR".indexOf(c) >=0;
        }

        private static boolean isBlack(char c) {
            return "qnbr".indexOf(c) >=0;
        }

        private static boolean isEmpty(char c) {
            return c == 0;
        }

        private static boolean isTarget(char[][] chess, int [] piece, int row ,int col) {
            char p = (char) piece[0];
            // For Queen
            int[] x1 = {0, 0, 1, -1, 1, -1, 1, -1};
            int[] y1 = {1, -1, 0, 0, 1, -1, -1, 1};
            //For Bishop
            int[] x2 = {1, -1, 1, -1};
            int[] y2 = {1, -1, -1, 1};
            //For Rook
            int[] x3 = {0, 0, 1, -1};
            int[] y3 = {1, -1, 0, 0};
            //Init
            int[] x = x1;
            int[] y = y1;


            //Check Piece
            if(p == 'q' || p == 'Q') {
                x = x1;
                y = y1;
            } else if(p == 'n' || p == 'N') {
                // Check for Knight
                if(Math.abs(piece[1]-row) == 2 && Math.abs(piece[2]-col) == 1) {
                    return true;
                }
                if(Math.abs(piece[1]-row) == 1 && Math.abs(piece[2]-col) == 2) {
                    return true;
                }
                return false;
            } else if(p == 'b' || p == 'B') {
                x = x2;
                y = y2;
            } else if(p == 'r' || p == 'R') {
                x = x3;
                y = y3;
            }

            // Check Move
            for (int  d = 0; d < x.length ; d ++) {
                int i = piece[1] + x[d];
                int j = piece[2] + y[d];
                for (; i >= 0 && i < 4 && j >= 0 && j < 4;i+=x[d], j += y[d]) {
                    // If have another piece in way, cancel
                    if (i != row || j != col) {
                        if (! isEmpty(chess[i][j])) {
                            break;
                        }
                    }
                    // If can reach the dst, true
                    if (i == row && j == col) {
                        return true;
                    }
                }
            }
            return false;
        }

        // Can white win
        private static boolean captureBlack(char[][] chess, Feature f) {
            for (int[] p : f.wpieces) {
                if(isTarget(chess,p,f.qi,f.qj)) {
                    return true;
                }
            }
            return false;
        }

        // Can black win
        private static boolean captureWhite(char[][] chess, Feature f) {
            for (int[] p : f.bpieces) {
                if(isTarget(chess,p,f.Qi,f.Qj)) {
                    return true;
                }
            }
            return false;
        }

        // Find all of piece in chessboard
        private static Feature getFeature(char[][] chess) {
            Feature f = new Feature();
            for (int i = 0; i < 4; i ++) {
                for (int j = 0; j < 4; j++) {
                    char c = chess[i][j];
                    int[] item = new int [3];
                    item[0] = c;
                    item[1] = i;
                    item[2] = j;
                    if(isWhite(c)) {
                        f.wpieces.add(item);
                    }
                    if (isBlack(c)) {
                        f.bpieces.add(item);
                    }
                    if(c == 'Q') {
                        f.Qi = i;
                        f.Qj = j;
                    }
                    if (c == 'q') {
                        f.qi = i;
                        f.qj = j;
                    }
                }
            }
            return f;
        }

        // Find all of able move for black or white
        private static List<Move> getValidMove(char[][] chess, List<int[]> pieces) {
            List<Move> res = new ArrayList<>();
            if(pieces.isEmpty()) {
                return res;
            }
            boolean WhiteMove = isWhite((char)pieces.get(0)[0]);
            for (int i = 0; i < 4; i ++) {
                for (int j = 0; j < 4; j++) {
                    if(isWhite(chess[i][j]) && WhiteMove) {
                        continue;
                    }
                    if(isBlack(chess[i][j]) && !WhiteMove) {
                        continue;
                    }
                    for(int[] p : pieces) {
                        if(isTarget(chess,p,i,j)) {
                            Move m = new Move();
                            m.srcRow = p[1];
                            m.srcCol = p[2];
                            m.srcPiece = (char)(p[0]);
                            m.dstCol = j;
                            m.dstRow = i;
                            m.dstPiece = chess[i][j];
                            res.add(m);
                        }
                    }
                }
            }
            return res;
        }

        // move a chess
        private static void moveChess(char[][] chess, Move m) {
            chess[m.dstRow][m.dstCol] = m.srcPiece;
            chess[m.srcRow][m.srcCol] = 0;
        }

        private static void moveBack(char[][] chess, Move m) {
            chess[m.dstRow][m.dstCol] = m.dstPiece;
            chess[m.srcRow][m.srcCol] = m.srcPiece;
        }

        // Core of program
        private static boolean evaluate(char[][] chess, int m, int step) {
            Feature f = getFeature(chess);
            // White move, and if white can capture black and win
            if(step % 2 == 1) {
                if(captureBlack(chess,f)) {
                    return true;
                }
            }
            // Over move, game over
            if (step == m) return false;
            // White move
            if(step % 2 == 1) {
                for (Move move : getValidMove(chess,f.wpieces)) {
                    // White move
                    moveChess(chess,move);
                    // Check chessboard after white move
                    Feature f1 = getFeature(chess);
                    // if white can be loss, rollback
                    if(captureWhite(chess,f1)) {
                        moveBack(chess,move);
                        continue;
                    }
                    // if not, continue for black turn and if can be loss, rollback
                    if(evaluate(chess,m,step+1)) {
                        moveBack(chess,move);
                        return true;
                    }
                    // roll back to move another move
                    moveBack(chess,move);
                }
            } else {
                // Black move
                for (Move move : getValidMove(chess,f.bpieces)) {
                    moveChess(chess,move);
                    //if black can win, roll back
                    if(!evaluate(chess,m,step + 1)) {
                        moveBack(chess,move);
                        return false;
                    }
                    // roll back to move another move
                    moveBack(chess,move);
                }
                // white must win if black dont have any move to win
                return true;
            }
            // black win
            return false;
        }

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            int q = sc.nextInt();
            while (q-- >0) {
                char[][] chess = new char[4][4];
                int w = sc.nextInt();
                int b = sc.nextInt();
                int m = sc.nextInt();
                for (int i = 0; i < w ; i++) {
                    char piece = sc.next().charAt(0);
                    int col = sc.next().charAt(0) - 'A';
                    int row = 4 - (sc.next().charAt(0) - '0');
                    chess[row][col] = piece;
                }
                for (int i = 0; i < b ; i++) {
                    char piece = sc.next().charAt(0);
                    int col = sc.next().charAt(0) - 'A';
                    int row = 4 - (sc.next().charAt(0) - '0');
                    chess[row][col] = (char)(piece + ('a' - 'A'));
                }
                if(m > 1 && m % 2 == 0) {
                    m--;
                }
                System.out.println(evaluate(chess, m, 1) ? "YES" : "NO");
            }

        }
    }
