package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.deliclu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.lmu.ifi.dbs.elki.index.tree.BreadthFirstEnumeration;
import de.lmu.ifi.dbs.elki.index.tree.Entry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.BulkSplit.Strategy;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.NonFlatRStarTree;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.persistent.PageFile;

/**
 * DeLiCluTree is a spatial index structure based on an R-TRee. DeLiCluTree is
 * designed for the DeLiClu algorithm, having in each node a boolean array which
 * indicates whether the child nodes are already handled by the DeLiClu
 * algorithm.
 * 
 * @author Elke Achtert
 * 
 * @apiviz.has DeLiCluNode oneway - - contains
 */
public class DeLiCluTree extends NonFlatRStarTree<DeLiCluNode, DeLiCluEntry> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(DeLiCluTree.class);

  /**
   * Holds the ids of the expanded nodes.
   */
  private HashMap<Integer, HashSet<Integer>> expanded = new HashMap<Integer, HashSet<Integer>>();

  /**
   * Constructor.
   * 
   * @param pagefile Page file
   * @param bulk bulk flag
   * @param bulkLoadStrategy bulk load strategy
   * @param insertionCandidates insertion candidate set size
   */
  public DeLiCluTree(PageFile<DeLiCluNode> pagefile, boolean bulk, Strategy bulkLoadStrategy, int insertionCandidates) {
    super(pagefile, bulk, bulkLoadStrategy, insertionCandidates);
  }

  /**
   * Marks the nodes with the specified ids as expanded.
   * 
   * @param entry1 the first node
   * @param entry2 the second node
   */
  public void setExpanded(SpatialEntry entry1, SpatialEntry entry2) {
    HashSet<Integer> exp1 = expanded.get(entry1.getEntryID());
    if(exp1 == null) {
      exp1 = new HashSet<Integer>();
      expanded.put(entry1.getEntryID(), exp1);
    }
    exp1.add(entry2.getEntryID());
  }

  /**
   * Returns the nodes which are already expanded with the specified node.
   * 
   * @param entry the id of the node for which the expansions should be returned
   * @return the nodes which are already expanded with the specified node
   */
  public Set<Integer> getExpanded(SpatialEntry entry) {
    HashSet<Integer> exp = expanded.get(entry.getEntryID());
    if(exp != null) {
      return exp;
    }
    return new HashSet<Integer>();
  }

  /**
   * Returns the nodes which are already expanded with the specified node.
   * 
   * @param entry the id of the node for which the expansions should be returned
   * @return the nodes which are already expanded with the specified node
   */
  public Set<Integer> getExpanded(DeLiCluNode entry) {
    HashSet<Integer> exp = expanded.get(entry.getPageID());
    if(exp != null) {
      return exp;
    }
    return new HashSet<Integer>();
  }

  /**
   * Determines and returns the number of nodes in this index.
   * 
   * @return the number of nodes in this index
   */
  public int numNodes() {
    int numNodes = 0;

    BreadthFirstEnumeration<DeLiCluNode, DeLiCluEntry> bfs = new BreadthFirstEnumeration<DeLiCluNode, DeLiCluEntry>(this, getRootPath());
    while(bfs.hasMoreElements()) {
      Entry entry = bfs.nextElement().getLastPathComponent().getEntry();
      if(!entry.isLeafEntry()) {
        numNodes++;
      }
    }

    return numNodes;
  }

  /**
   * Creates a new leaf node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  @Override
  protected DeLiCluNode createNewLeafNode(int capacity) {
    return new DeLiCluNode(file, capacity, true);
  }

  /**
   * Creates a new directory node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  @Override
  protected DeLiCluNode createNewDirectoryNode(int capacity) {
    return new DeLiCluNode(file, capacity, false);
  }

  /**
   * Creates a new directory entry representing the specified node.
   * 
   * @param node the node to be represented by the new entry
   */
  @Override
  protected DeLiCluEntry createNewDirectoryEntry(DeLiCluNode node) {
    return new DeLiCluDirectoryEntry(node.getPageID(), node.computeMBR(), node.hasHandled(), node.hasUnhandled());
  }

  /**
   * Creates an entry representing the root node.
   * 
   * @return an entry representing the root node
   */
  @Override
  protected DeLiCluEntry createRootEntry() {
    return new DeLiCluDirectoryEntry(0, null, false, true);
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}