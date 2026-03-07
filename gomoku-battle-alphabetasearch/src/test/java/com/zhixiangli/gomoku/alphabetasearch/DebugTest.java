package com.zhixiangli.gomoku.alphabetasearch;

import com.zhixiangli.gomoku.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.alphabetasearch.algorithm.AlphaBetaSearchProphet;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.Map;
import java.util.stream.Stream;

public class DebugTest {
    public static void main(String[] args) throws Exception {
        String sgf = "B[77];W[22];B[78];W[33];B[79];W[44];B[7a]";
        Chessboard chessboard = GomokuFormatter.toChessboard(sgf);
        ChessType chessType = ChessType.WHITE;
        
        System.out.println("Board state:");
        System.out.println(chessboard);
        
        AlphaBetaSearchAlgorithm algo = new AlphaBetaSearchAlgorithm(false);
        
        // Get candidate moves
        Point[] candidates = algo.nextMoves(chessboard);
        System.out.println("\nCandidate moves:");
        for (Point p : candidates) {
            double pointVal = AlphaBetaSearchProphet.evaluatePointValue(chessboard, p);
            System.out.println("  " + p + " -> pointValue=" + pointVal);
        }
        
        // Evaluate each candidate
        System.out.println("\nSearch values for each candidate:");
        for (Point point : candidates) {
            Chessboard newBoard = chessboard.clone();
            newBoard.setChess(point, chessType);
            algo.clearCache();
            double value = algo.search(SearchConst.MAX_DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE,
                    newBoard, point, chessType, chessType, StringUtils.EMPTY);
            newBoard.setChess(point, ChessType.EMPTY);
            System.out.println("  White at " + point + " -> searchValue=" + value);
        }
        
        // Also check pattern evaluations
        System.out.println("\nPattern evaluations at key positions:");
        for (Point p : new Point[]{new Point(7, 6), new Point(7, 11)}) {
            Map<PatternType, Integer> blackPatterns = GlobalAnalyser.getPatternStatistics(chessboard, p, ChessType.BLACK);
            Map<PatternType, Integer> whitePatterns = GlobalAnalyser.getPatternStatistics(chessboard, p, ChessType.WHITE);
            System.out.println("  " + p + " BLACK patterns: " + blackPatterns);
            System.out.println("  " + p + " WHITE patterns: " + whitePatterns);
        }
        
        // Board evaluation
        double selfValue = AlphaBetaSearchProphet.evaluateChessboardValueByChessType(chessboard, ChessType.WHITE);
        double oppValue = AlphaBetaSearchProphet.evaluateChessboardValueByChessType(chessboard, ChessType.BLACK);
        System.out.println("\nBoard evaluation:");
        System.out.println("  White value: " + selfValue);
        System.out.println("  Black value: " + oppValue);
        System.out.println("  evaluateChessboardValue(WHITE): " + AlphaBetaSearchProphet.evaluateChessboardValue(chessboard, ChessType.WHITE));
    }
}
