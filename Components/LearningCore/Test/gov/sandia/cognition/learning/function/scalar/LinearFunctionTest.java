/*
 * File:                LinearFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

/**
 * Unit tests for class LinearFunction.
 *
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 */
public class LinearFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{

    /**
     * Entry point for JUnit tests for class LinearFunctionTest
     * @param testName name of this test
     */
    public LinearFunctionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public LinearFunction createInstance()
    {
        return new LinearFunction(RANDOM.nextGaussian(), RANDOM.nextGaussian());
    }

    /**
     * Test of constants of class LinearFunction.
     */
    public void testConstants()
    {
        // The default values need to be this.
        assertEquals(1.0, LinearFunction.DEFAULT_SLOPE, 0.0);
        assertEquals(0.0, LinearFunction.DEFAULT_OFFSET, 0.0);
    }

    @Override
    public void testConstructors()
    {
        double slope = LinearFunction.DEFAULT_SLOPE;
        double offset = LinearFunction.DEFAULT_OFFSET;
        LinearFunction instance = new LinearFunction();
        assertEquals(slope, instance.getSlope(), 0.0);
        assertEquals(offset, instance.getOffset(), 0.0);

        slope = RANDOM.nextGaussian();
        slope = RANDOM.nextGaussian();
        instance = new LinearFunction(slope, offset);
        assertEquals(slope, instance.getSlope(), 0.0);
        assertEquals(offset, instance.getOffset(), 0.0);

        instance = new LinearFunction(instance);
        assertEquals(slope, instance.getSlope(), 0.0);
        assertEquals(offset, instance.getOffset(), 0.0);
    }

    /**
     * Test of clone method, of class LinearFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        LinearFunction instance = new LinearFunction();
        LinearFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(clone, clone.clone());
        assertEquals(instance.getSlope(), clone.getSlope(), 0.0);
        assertEquals(instance.getOffset(), clone.getOffset(), 0.0);

        instance = this.createInstance();
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(clone, clone.clone());
        assertEquals(instance.getSlope(), clone.getSlope(), 0.0);
        assertEquals(instance.getOffset(), clone.getOffset(), 0.0);
    }

    /**
     * Test of evaluate method, of class LinearFunction.
     */
    public void testEvaluate()
    {
        // Test how it performs by default: f(x) = x.
        LinearFunction instance = new LinearFunction();
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            double input = RANDOM.nextDouble();
            assertEquals(input, instance.evaluate(input), 0.0);
        }

        instance = new LinearFunction(-1.2, 0.5);
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            double input = RANDOM.nextDouble();
            assertEquals(-1.2 * input + 0.5, instance.evaluate(input), 0.0);
        }
    }

    /**
     * Test of differentiate method, of class LinearFunction.
     */
    public void testDifferentiate()
    {
        // Test how it is for the default: f(x) = x
        LinearFunction instance = new LinearFunction();
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            double input = RANDOM.nextDouble();
            assertEquals(1.0, instance.differentiate(input), 0.0);
        }

        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            double slope = RANDOM.nextGaussian();
            instance.setSlope(slope);
            instance.setOffset(RANDOM.nextGaussian());
            double input = RANDOM.nextDouble();

            assertEquals(slope, instance.differentiate(input), 0.0);
        }

        instance.setSlope(0.0);
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            double input = RANDOM.nextDouble();
            assertEquals(0.0, instance.differentiate(input), 0.0);
        }
    }

    /**
     * Test of getSlope method, of class LinearFunction.
     */
    public void testGetSlope()
    {
        this.testSetSlope();
    }

    /**
     * Test of setSlope method, of class LinearFunction.
     */
    public void testSetSlope()
    {
        double slope = LinearFunction.DEFAULT_SLOPE;
        LinearFunction instance = new LinearFunction();
        assertEquals(slope, instance.getSlope(), 0.0);

        double[] goodValues =
        {
            0.0, 1.0, -2.0, RANDOM.nextDouble(), -RANDOM.nextDouble(),
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN
        };
        for (double goodValue : goodValues)
        {
            slope = goodValue;
            instance.setSlope(slope);
            assertEquals(slope, instance.getSlope(), 0.0);
        }
    }

    /**
     * Test of getOffset method, of class LinearFunction.
     */
    public void testGetOffset()
    {
        this.testSetOffset();
    }

    /**
     * Test of setOffset method, of class LinearFunction.
     */
    public void testSetOffset()
    {
        double offset = LinearFunction.DEFAULT_OFFSET;
        LinearFunction instance = new LinearFunction();
        assertEquals(offset, instance.getOffset(), 0.0);

        double[] goodValues =
        {
            0.0, 1.0, -2.0, RANDOM.nextDouble(), -RANDOM.nextDouble(),
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN
        };
        for (double goodValue : goodValues)
        {
            offset = goodValue;
            instance.setOffset(offset);
            assertEquals(offset, instance.getOffset(), 0.0);
        }
    }

}
