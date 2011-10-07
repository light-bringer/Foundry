/*
 * File:                VectorThresholdInformationGainLearner.java
 * Authors:             Justin Basilico, Art Munson
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import java.util.ArrayList;
import java.util.Map;

/**
 * The {@code VectorThresholdInformationGainLearner} computes the best 
 * threshold over a dataset of vectors using information gain to determine the 
 * optimal index and threshold. This is an implementation of what is used in
 * the C4.5 decision tree algorithm.
 * <BR><BR>
 * Information gain for a given split (sets X and Y) for two categories (a and b):
 * <BR>     ig(X, Y) = entropy(X + Y)
 * <BR>              – (|X| / (|X| + |Y|)) entropy(X)
 * <BR>              – (|Y| / (|X| + |Y|)) entropy(Y)
 * <BR> with
 * <BR><BR>
 * <BR>    entropy(Z) = - (Za / |Z|) log2(Za / |Z|) – (Zb / |Z|) log2(Zb / |Z|)
 * <BR><BR>
 * <BR>where
 * <BR>     Za = number of a's in Z, and
 * <BR>     Zb = number of b's in Z.
 * <BR> In the multi-class case, the entropy is defined as the sum over all of the
 * categories (c) of -Zc / |Z| log2(Zc / |Z|).
 *
 * @param  <OutputType> The output type of the data.
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdInformationGainLearner<OutputType>
    extends AbstractVectorThresholdMaximumGainLearner<OutputType>
    implements PriorWeightedNodeLearner<OutputType>
{
    private static final double LOG2 = Math.log(2);

    private ArrayList<OutputType> klasses = null;
    private double[] klassPriors = null;
    private int[] klassCounts = null;

    // Following is scratch space used when computing weighted
    // entropy. It is declared here so it can be allocated once,
    // instead of during every entropy evaluation.
    private double[] klassProbs = null;

    /**
     * Creates a new instance of VectorDeciderLearner.
     */
    public VectorThresholdInformationGainLearner()
    {
        super();
    }
    
    @Override
    public double computeSplitGain(
        final DefaultDataDistribution<OutputType> baseCounts,
        final DefaultDataDistribution<OutputType> positiveCounts,
        final DefaultDataDistribution<OutputType> negativeCounts)
    {
        if (klassPriors == null) {
            // Support legacy code that does not configure class
            // priors.
            return legacyComputSplitGain(baseCounts,
                                         positiveCounts,
                                         negativeCounts);
        }

        Vector2 baseEntropy = weightedEntropy(baseCounts);
        Vector2 posEntropy = weightedEntropy(positiveCounts);
        Vector2 negEntropy = weightedEntropy(negativeCounts);

        double posWt = posEntropy.getSecond() / baseEntropy.getSecond();
        double negWt = negEntropy.getSecond() / baseEntropy.getSecond();
        double gain = baseEntropy.getFirst()
            - posWt*posEntropy.getFirst()
            - negWt*negEntropy.getFirst();
        return gain;
    }

    /**
     * Computes entropy of the counts, weighted by prior
     * probabilities.  This entropy calculation comes from Breiman et
     * al. (1984), "Classification and Regression Trees".
     * @return The pair of values (entropy, marginal node prob).
     */
    private Vector2 weightedEntropy(
        final DefaultDataDistribution<OutputType> counts)
    {
        // Variable p_t stores the marginal probability of a training
        // point reaching this tree node (node t).  It is defined as:
        //   p(t) = sum_j p(j, t)
        // where j indexes over classes.
        double p_t = 0;

        for (int j = 0; j < klassProbs.length; ++j) {
            // Compute joint probability of seeing class j and landing
            // in this tree node.  We estimate this as:
            //    p(j, t) = prior(j) * p(t | j)
            // where
            //    p(t | j) = (# class j at node t) / (# class j in training)
            klassProbs[j] = klassPriors[j] 
                * counts.get(klasses.get(j))
                / (double)(klassCounts[j]);
            p_t += klassProbs[j];
         }

        // The entropy of data at a node t equals
        //   - sum_j p(j | t) log p(j | t)
        double entropy = 0;
        for (int j = 0; j < klassProbs.length; ++j) {
            double condProb = klassProbs[j] / p_t;
            if (condProb > 0) {
                entropy -= condProb * lb(condProb);
            }
        }

        return new Vector2(entropy, p_t);
    }

    /**
     * Compute log_2(x).
     * @param x should be greater than 0.
     */
    private static double lb(double x)
    {
        return Math.log(x) / LOG2;
    }

    /**
     * Legacy implementation of gain computation.  This code does not
     * incorporate class priors.
     */
    private double legacyComputSplitGain(
        final DefaultDataDistribution<OutputType> baseCounts,
        final DefaultDataDistribution<OutputType> positiveCounts,
        final DefaultDataDistribution<OutputType> negativeCounts)
    {
        final double totalCount = baseCounts.getTotal();
        final double entropyBase = baseCounts.getEntropy();
        final double entropyPositive = positiveCounts.getEntropy();
        final double entropyNegative = negativeCounts.getEntropy();
        final double proportionPositive = positiveCounts.getTotal() / totalCount;
        final double proportionNegative = negativeCounts.getTotal() / totalCount;

        final double gain = entropyBase
            - proportionPositive * entropyPositive
            - proportionNegative * entropyNegative;

        return gain;
    }

    ///// Implementation of PriorWeightedNodeLearner /////

    public void configure(Map<OutputType,Double> priors, 
                          Map<OutputType,Integer> trainCounts)
    {
        klasses = new ArrayList<OutputType>(trainCounts.keySet());

        klassCounts = new int[klasses.size()];
        int total = 0;
        for (int j = 0; j < klasses.size(); ++j) {
            klassCounts[j] = trainCounts.get(klasses.get(j));
            total += klassCounts[j];
        }

        klassPriors = new double[klasses.size()];
        if (priors == null) {
            if (total > 0) {
                // Default to relative class frequencies.
                for (int j = 0; j < klasses.size(); ++j) {
                    klassPriors[j] = klassCounts[j] / ((double)total);
                }
            }
            else {
                // This is really unlikely . . .
                for (int j = 0; j < klasses.size(); ++j) {
                    klassPriors[j] = 1.0 / klasses.size();
                }
            }
        }
        else {
            for (int j = 0; j < klasses.size(); ++j) {
                klassPriors[j] = priors.get(klasses.get(j));
            }
        }

        klassProbs = new double[klasses.size()];
    }
}
