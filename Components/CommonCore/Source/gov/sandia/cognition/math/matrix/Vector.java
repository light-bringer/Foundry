/*
 * File:                Vector.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import java.text.NumberFormat;

/**
 * The <code>Vector</code> interface defines the operations that are expected
 * on a mathematical vector. The <code>Vector</code> can be thought of as a
 * collection of doubles of fixed size (the dimensionality) that supports
 * array-like indexing into the <code>Vector</code>. The <code>Vector</code>
 * is defined as an interface because there is more than one way to implement
 * a Vector, in particular if you want to make use of sparseness, which occurs
 * when most of the elements of a <code>Vector</code> are zero so that they are
 * not represented.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments={
                "Minor changes to formatting.",
                "Otherwise, looks good."
            }
        ),    
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-17",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public interface Vector
    extends VectorSpace<Vector,VectorEntry>,
    Vectorizable
{
    
    @Override
    public Vector clone();

    /**
     * Returns the number of elements in the Vector
     *
     * @return number of elements in the Vector
     */
    public int getDimensionality();

    /**
     * Gets the zero-based indexed element from the Vector 
     *
     * @param index
     *          zero-based index
     * @return zero-based indexed element in the Vector
     */
    public double getElement(
        final int index );

    /**
     * Sets the zero-based indexed element in the Vector from the specified value 
     *
     * @param index
     *          zero-based index
     * @param value
     *          value to set the element in the Vector
     */
    public void setElement(
        final int index,
        final double value );

    /**
     * Computes the outer matrix product between the two vectors 
     *
     * @param other
     *          post-multiplied Vector with which to compute the outer product
     * @return Outer matrix product, which will have dimensions
     *         (this.getDimensionality x other.getDimensionality) and WILL BE 
     *         singular
     */
    public Matrix outerProduct(
        final Vector other );

    /**
     * Premultiplies the matrix by the vector "this"
     * @param matrix
     * Matrix to premultiply by "this", must have the same number of rows as
     * the dimensionality of "this"
     * @return
     * Vector of dimension equal to the number of columns of "matrix"
     */
    public Vector times(
        final Matrix matrix );
    /**
     * Determines if <code>this</code> and <code>other</code> have the same
     * number of dimensions (size)
     *
     * @param other
     *          vector to compare to
     * @return true iff this.getDimensionality() == other.getDimensionality(),
     *          false otherwise
     */
    public boolean checkSameDimensionality(
        final Vector other );
    
    /**
     * Asserts that this vector has the same dimensionality as the given 
     * vector. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   other The other vector to compare to.
     */
    public void assertSameDimensionality(
        final Vector other);
    
    /**
     * Asserts that the dimensionality of this vector equals the given 
     * dimensionality. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   otherDimensionality The dimensionality of the.
     */
    public void assertDimensionalityEquals(
        final int otherDimensionality);

    /**
     * Stacks "other" below "this" and returns the stacked Vector 
     *
     * @param other
     *          Vector to stack below "this"
     * @return stacked Vector
     */
    public Vector stack(
        Vector other );

    /**
     * Gets a subvector of "this", specified by the inclusive indices
     * @param minIndex minimum index to get (inclusive)
     * @param maxIndex maximum index to get (inclusive)
     * @return vector of dimension (maxIndex-minIndex+1)
     */
    public Vector subVector(
        final int minIndex,
        final int maxIndex );
    
    @Override
    public String toString();
    
    /**
     * Converts the vector to a {@code String}, using the given formatter.
     * 
     * @param   format The number format to use.
     * @return  The string representation of the vector.
     */
    public String toString(
        final NumberFormat format);
    
    /**
     * Converts the vector to a {@code String}, using the given formatter and
     * delimiter.
     * 
     * @param   format The number format to use.
     * @param   delimiter The delimiter to use.
     * @return  The string representation of the vector.
     */
    public String toString(
        final NumberFormat format,
        final String delimiter);

}
