package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Search Tree for Monte Carlo Tree Search
 *
 * @author
 */
public final class MonteCarloSearchTree<GM extends GameMove,
                                        GR extends GameResult,
                                        GS extends GameState<GM,GR>,
                                        NR extends NodeResults<GM,GR>> {

    private volatile SearchTreeNode rootNode;
    private final NodeResultsFactory<GM,GR,GS,NR> nodeResultsFactory;
    private volatile int MAX_NODE_DEPTH = 10;
    private volatile int NODE_EXPAND_THRESHOLD = 10;
    private volatile float EXPLORATION_CONSTANT = 1.0f;

    public MonteCarloSearchTree(NodeResultsFactory<GM,GR,GS,NR> nRFactory) {
        nodeResultsFactory = nRFactory;
        rootNode = null;
    }

    public void initialize(GS initialGameState) {
        rootNode = new SearchTreeNode(null, 1, null, initialGameState);
    }

    public void setExplorationConstant(float val) {
        EXPLORATION_CONSTANT = val;
    }

    public void setMaxNodeDepth(int val) {
        MAX_NODE_DEPTH = val;
    }

    public void setNodeExpandThreshold(int val) {
        NODE_EXPAND_THRESHOLD = val;
    }

    public void applyMove(GM move, GS resultingGameState) {
        pruneTreeAndAdvanceRoot(move, resultingGameState);
    }

    private void pruneTreeAndAdvanceRoot(GM move, GS resultingGameState) {
        SearchTreeNode newRoot = rootNode.getNodeWithMove(move);
        if (newRoot == null)
            newRoot = new SearchTreeNode(move, 1, null, resultingGameState);
        else
            newRoot.decrementTreeDepth();
        rootNode = newRoot;
    }

    public <PP extends PlayoutPolicy<GM,GR,GS>> void playSimulationGame(GS playoutGameState, PP playoutPolicy) {
        ensureExpandedRootNode(playoutGameState);
        SearchTreeNode leafNode = advanceGameStateToLeafNode(playoutGameState);
        leafNode.maybeExpandNode(playoutGameState);
        playoutGameState.setIsPlayout();
        while (!playoutGameState.isPlayoutOver()) {
            GM playoutMove = playoutPolicy.getPlayoutMove(playoutGameState);
            playoutGameState.applyMove(playoutMove);
        }
        GR gameResult = playoutGameState.getGameResult();
        leafNode.propagateGameResult(gameResult);
    }

    private void ensureExpandedRootNode(GS gameState) {
        if (rootNode.childNodes.isEmpty())
            rootNode.expandNode(gameState);
    }

    private SearchTreeNode advanceGameStateToLeafNode(GS gameState) {
        //This special function is run to avoid applying the rootNode's move
        SearchTreeNode explorationNode = rootNode.getNextNodeToExplore();
        if (explorationNode == null)
            return rootNode;
        return explorationNode.advanceGameStateToLeafNode(gameState);
    }

    public GM suggestMove() {
        return rootNode.getMostSimulatedChildMove();
    }

    private final class SearchTreeNode {
        private final GM appliedMove;
        private volatile int nodeDepth;
        private volatile NodeResults<GM,GR> nodeResults;
        private volatile SearchTreeNode parentNode;
        private volatile List<SearchTreeNode> childNodes;
        private volatile boolean expanded;

        public SearchTreeNode(GM m, int depth, SearchTreeNode parent, GS previousGameState) {
            appliedMove = m;
            nodeDepth = depth;
            parentNode = parent;
            nodeResults = nodeResultsFactory.getNewNodeResults(m, previousGameState);
            childNodes = Lists.newArrayList();
            expanded = false;
        }

        public void maybeExpandNode(GS gameState) {
            if ((nodeResults.getNumSimulations() >= NODE_EXPAND_THRESHOLD) && (nodeDepth < MAX_NODE_DEPTH))
                expandNode(gameState);
        }

        private synchronized void expandNode(GS gameState) {
            if (gameState.isGameOver() || expanded)
                return;
            for (GM move : gameState.getAllValidMoves())
                childNodes.add(new SearchTreeNode(move, nodeDepth+1, this, gameState));
            expanded = true;
        }

        public GM getMostSimulatedChildMove() {
            if (!expanded)
                return null;
            int mostSims = -Integer.MAX_VALUE;
            GM mostSimulatedMove = null;
            for (SearchTreeNode childNode : childNodes) {
                if (childNode.getNodeResults().getNumSimulations() > mostSims) {
                    mostSimulatedMove = childNode.appliedMove;
                    mostSims = childNode.getNodeResults().getNumSimulations();
                }
            }
            return mostSimulatedMove;
        }

        public SearchTreeNode getNodeWithMove(GM move) {
            if (!expanded)
                return null;
            for (SearchTreeNode childNode : childNodes) {
                if (childNode.appliedMove.equals(move))
                    return childNode;
            }
            return null;
        }

        public void decrementTreeDepth() {
            --nodeDepth;
            if (!expanded)
                return;
            for (SearchTreeNode childNode : childNodes)
                childNode.decrementTreeDepth();
        }

        public SearchTreeNode advanceGameStateToLeafNode(GS gameState) {
            if (appliedMove != null)
                gameState.applyMove(appliedMove);
            if (!expanded)
                return this;
            return getNextNodeToExplore().advanceGameStateToLeafNode(gameState);
        }

        private SearchTreeNode getNextNodeToExplore() {
            if (!expanded)
                return null;
            SearchTreeNode bestNode = null;
            float bestNodeValue = Float.NEGATIVE_INFINITY;
            for (SearchTreeNode childNode : childNodes) {
                float currentNodeValue = childNode.getNodeResults().getValue(childNode.appliedMove) + childNode.getNodeExplorationValue();
                if (currentNodeValue > bestNodeValue) {
                    bestNode = childNode;
                    bestNodeValue = currentNodeValue;
                }
            }
            return bestNode;
        }

        public NodeResults<GM,GR> getNodeResults() {
            return nodeResults;
        }

        private float getNodeExplorationValue() {
            if (parentNode == null)
                return 0.0f;
            int parentSimulations = parentNode.getNodeResults().getNumSimulations();
            return EXPLORATION_CONSTANT*(float)Math.sqrt(Math.log(parentSimulations+1)/(nodeResults.getNumSimulations()+1));
        }

        public void propagateGameResult(GR gameResult) {
            nodeResults.applyGameResultWithMove(gameResult, appliedMove);
            if (parentNode != null)
                parentNode.propagateGameResult(gameResult);
        }

    }
}
