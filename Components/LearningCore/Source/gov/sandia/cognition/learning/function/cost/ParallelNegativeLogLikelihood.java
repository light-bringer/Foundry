/*
 * File:                ParallelNegativeLogLikelihood.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Parallel implementation of the NegativeLogLikleihood cost function
 * @param <DataType>
 * Type of data generated by the Distribution
 */
public class ParallelNegativeLogLikelihood<DataType>
    extends NegativeLogLikelihood<DataType>
    implements ParallelAlgorithm
{

    /**
     * Thread pool for executing the tasks.
     */
    protected transient ThreadPoolExecutor threadPool;

    /**
     * Tasks to compute partial log likelihoods
     */
    protected transient ArrayList<NegativeLogLikelihoodTask<DataType>> tasks;

    /**
     * Default constructor
     */
    public ParallelNegativeLogLikelihood()
    {
        this( null );
    }

    /**
     * Creates a new instance of ParallelNegativeLogLikelihood
     * @param costParameters
     * Data generated by the target distribution
     */
    public ParallelNegativeLogLikelihood(
        Collection<? extends DataType> costParameters)
    {
        super( costParameters );
    }

    @Override
    public Double evaluate(
        ComputableDistribution<DataType> target)
    {

        ProbabilityFunction<DataType> probabilityFunction =
            target.getProbabilityFunction();

        final int N = this.costParameters.size();
        final int numThreads = this.getNumThreads();
        if( (this.tasks == null) ||
            (this.tasks.size() != numThreads) )
        {
            ArrayList<? extends DataType> dataArray =
                CollectionUtil.asArrayList(this.costParameters);

            this.tasks = new ArrayList<NegativeLogLikelihoodTask<DataType>>( numThreads );
            int numPerTask = N/numThreads;
            int endIndex = 0;
            int beginIndex = 0;
            for( int i = 0; i < numThreads; i++ )
            {
                beginIndex = endIndex;
                endIndex += numPerTask;
                if( i == (numThreads-1) )
                {
                    endIndex = N;
                }
                this.tasks.add( new NegativeLogLikelihoodTask<DataType>(
                    dataArray.subList(beginIndex, endIndex) ) );
            }
        }

        for( int i = 0; i < numThreads; i++ )
        {
            this.tasks.get(i).probabilityFunction = probabilityFunction;
        }

        ArrayList<Double> results = null;
        try
        {
            results = ParallelUtil.executeInParallel(
                this.tasks, this.getThreadPool() );
        }
        catch (Exception ex)
        {
            throw new RuntimeException( ex );
        }

        return UnivariateStatisticsUtil.computeSum(results) / this.costParameters.size();

    }

    public ThreadPoolExecutor getThreadPool()
    {
        if( this.threadPool == null )
        {
            this.threadPool = ParallelUtil.createThreadPool();
        }
        return this.threadPool;
    }

    public void setThreadPool(
        ThreadPoolExecutor threadPool)
    {
        this.threadPool = threadPool;
    }

    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads(this);
    }

    /**
     * Task for computing partial log likelihoods
     * @param <DataType>
     * Type of data generated by the Distribution
     */
    protected static class NegativeLogLikelihoodTask<DataType>
        implements Callable<Double>
    {

        /**
         * Partial data
         */
        private Collection<? extends DataType> data;

        /**
         * Probability function to compute the log likelihood
         */
        protected ProbabilityFunction<DataType> probabilityFunction;

        /**
         * Creates a new instance of NegativeLogLikelihoodTask
         * @param data
         * Partial Data
         */
        public NegativeLogLikelihoodTask(
            Collection<? extends DataType> data )
        {
            this.data = data;
        }

        public Double call()
            throws Exception
        {
            return this.data.size() * NegativeLogLikelihood.evaluate(
                this.probabilityFunction, this.data );
        }

    }

}
