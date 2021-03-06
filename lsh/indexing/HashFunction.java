package indexing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This will simulate a hash function which consists of a vector and an offset
 *
 */
public class HashFunction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private int numberOfDimensions;
	private List<Double> hashFunctionCoefficients;
	private double offset, slotWidthW;
	
	public HashFunction(int numberOfDimensions, double slotWidthW, Random randomNumberGenerator) {
		
		this.numberOfDimensions = numberOfDimensions;
		this.hashFunctionCoefficients = new ArrayList<Double>(numberOfDimensions);
		
		for (int dimensionCounter = 0; dimensionCounter < numberOfDimensions; ++dimensionCounter) {
			this.hashFunctionCoefficients.add(Double.valueOf(randomNumberGenerator.nextGaussian()));
		}

		// TODO i believe the offset is incorrect
		//		the slotWidthW multiplication scales it and then is immediately countered during the calculation on
		//		line 66. essentially we are just multiplying by a very very small real between 0,1 which i think
		//		skews the results since our offsets are not large enough. the paper states offset is between [0,w]
		this.offset = randomNumberGenerator.nextDouble() * slotWidthW;
		this.slotWidthW = slotWidthW;
		
	}

	public HashFunction(int numberOfDimensions, double slotWidthW, double eigenVector[], Random randomNumberGenerator) {
		this.numberOfDimensions = numberOfDimensions;
		this.hashFunctionCoefficients = new ArrayList<Double>();

		for (int i = 0; i < numberOfDimensions; i++)
			hashFunctionCoefficients.add(eigenVector[i]);

		this.offset = randomNumberGenerator.nextDouble() * slotWidthW;
		this.slotWidthW = slotWidthW;
	}
	
	/**
	 * @param objectFeatures
	 * @return slot number for locality sensitive hashing
	 */
	public int getSlotNumber(List<Double> objectFeatures) {
		
		assert objectFeatures.size() == this.hashFunctionCoefficients.size() : 
			"Size mismatch between object to be hashed and hash function";
		
		double innerProduct = 0.0;
		for (int dimensionCounter = 0; dimensionCounter < numberOfDimensions; ++dimensionCounter) {
			innerProduct += objectFeatures.get(dimensionCounter) * this.hashFunctionCoefficients.get(dimensionCounter);
		}
	
		innerProduct += this.offset;
		
		return Double.valueOf(Math.floor(innerProduct / this.slotWidthW)).intValue();
		
	}

	public double getF(List<Double> objectFeatures)
	{
		assert objectFeatures.size() == this.hashFunctionCoefficients.size() :
				"Size mismatch between object to be hashed and hash function";

		double innerProduct = 0.0;
		for (int dimensionCounter = 0; dimensionCounter < numberOfDimensions; ++dimensionCounter) {
			innerProduct += objectFeatures.get(dimensionCounter) * this.hashFunctionCoefficients.get(dimensionCounter);
		}

		innerProduct += this.offset;
		return innerProduct;
	}
	
}
