package de.lmu.ifi.dbs.elki.preprocessing;

import de.lmu.ifi.dbs.elki.data.RealVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.distance.DoubleDistance;
import de.lmu.ifi.dbs.elki.utilities.QueryResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.PatternParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.UnusedParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GlobalDistanceFunctionPatternConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GlobalParameterConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the HiCO correlation dimension of objects of a certain database. The
 * PCA is based on epsilon range queries.
 *
 * @author Elke Achtert
 */
public class RangeQueryBasedHiCOPreprocessor<V extends RealVector<V, ?>> extends HiCOPreprocessor<V> {

    /**
     * Parameter for epsilon.
     */
    public static final String EPSILON_P = "preprocessorEpsilon";

    /**
     * Description for parameter epsilon.
     */
    public static final String EPSILON_D = "an epsilon value suitable to the specified distance function";

    /**
     * Epsilon.
     */
    protected String epsilon;

    /**
     * Provides a new Preprocessor that computes the correlation dimension of
     * objects of a certain database based on a range query.
     */
    public RangeQueryBasedHiCOPreprocessor() {
        super();
        PatternParameter eps = new PatternParameter(EPSILON_P, EPSILON_D);
        optionHandler.put(eps);

        // global parameter constraint
        try {
            GlobalParameterConstraint gpc = new GlobalDistanceFunctionPatternConstraint(eps, (ClassParameter) optionHandler.getOption(PCA_DISTANCE_FUNCTION_P));
            optionHandler.setGlobalParameterConstraint(gpc);
        }
        catch (UnusedParameterException e) {
            verbose("Could not instantiate global parameter constraint concerning parameters " + EPSILON_P + " and parameter " + PCA_DISTANCE_FUNCTION_P + " because parameter " + PCA_DISTANCE_FUNCTION_P + " is not specified! " + e.getMessage());
        }
    }

    protected List<Integer> objectIDsForPCA(Integer id, Database<V> database, boolean verbose, boolean time) {
        pcaDistanceFunction.setDatabase(database, verbose, time);

        List<QueryResult<DoubleDistance>> knns = database.rangeQuery(id, epsilon, pcaDistanceFunction);

        List<Integer> ids = new ArrayList<Integer>(knns.size());
        for (QueryResult<DoubleDistance> knn : knns) {
            ids.add(knn.getID());
        }

        return ids;
    }

    protected List<QueryResult<DoubleDistance>> resultsForPCA(Integer id, Database<V> database, boolean verbose, boolean time) {
        pcaDistanceFunction.setDatabase(database, verbose, time);

        return database.rangeQuery(id, epsilon, pcaDistanceFunction);
    }

    /**
     * Sets the value for the required parameter k.
     */
    public String[] setParameters(String[] args) throws ParameterException {
        String[] remainingParameters = super.setParameters(args);

        epsilon = (String) optionHandler.getOptionValue(EPSILON_P);

        setParameters(args, remainingParameters);
        return remainingParameters;
    }

    /**
     * Returns a description of the class and the required parameters. <p/> This
     * description should be suitable for a usage description.
     *
     * @return String a description of the class and the required parameters
     */
    public String parameterDescription() {
        StringBuffer description = new StringBuffer();
        description.append(RangeQueryBasedHiCOPreprocessor.class.getName());
        description.append(" computes the correlation dimension of objects of a certain database.\n");
        description.append("The PCA is based on epsilon range queries.\n");
        description.append(optionHandler.usage("", false));
        return description.toString();
    }
}
