package de.lmu.ifi.dbs.elki.database.query.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.query.GenericDistanceResultPair;
import de.lmu.ifi.dbs.elki.database.query.LinearScanQuery;
import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;

/**
 * Default linear scan range query class.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.landmark
 * @apiviz.has DistanceQuery
 * 
 * @param <O> Database object type
 * @param <D> Distance type
 */
public class LinearScanRangeQuery<O, D extends Distance<D>> extends AbstractDistanceRangeQuery<O, D> implements LinearScanQuery {
  /**
   * Constructor.
   * 
   * @param distanceQuery Distance function to use
   */
  public LinearScanRangeQuery(DistanceQuery<O, D> distanceQuery) {
    super(distanceQuery);
  }

  @Override
  public List<DistanceResultPair<D>> getRangeForDBID(DBID id, D range) {
    List<DistanceResultPair<D>> result = new ArrayList<DistanceResultPair<D>>();
    for(DBID currentID : relation.iterDBIDs()) {
      D currentDistance = distanceQuery.distance(id, currentID);
      if(currentDistance.compareTo(range) <= 0) {
        result.add(new GenericDistanceResultPair<D>(currentDistance, currentID));
      }
    }
    Collections.sort(result);
    return result;
  }

  @Override
  public List<DistanceResultPair<D>> getRangeForObject(O obj, D range) {
    List<DistanceResultPair<D>> result = new ArrayList<DistanceResultPair<D>>();
    for(DBID currentID : relation.iterDBIDs()) {
      D currentDistance = distanceQuery.distance(currentID, obj);
      if(currentDistance.compareTo(range) <= 0) {
        result.add(new GenericDistanceResultPair<D>(currentDistance, currentID));
      }
    }
    Collections.sort(result);
    return result;
  }
}