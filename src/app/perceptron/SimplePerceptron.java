/**
 * Copyright (c) 2014 Saúl Piña <sauljabin@gmail.com>.
 * 
 * This file is part of SimplePerceptron.
 * 
 * SimplePerceptron is licensed under The MIT License.
 * For full copyright and license information please see the LICENSE file.
 */

package app.perceptron;

import java.util.Vector;

public class SimplePerceptron {

	private double[] weight;
	private int periods;
	private int maxPeriods;
	private double learningFactor;
	private Vector<Integer> errors;

	public SimplePerceptron(double learningFactor, int maxPeriods) {
		this.maxPeriods = maxPeriods;
		this.learningFactor = learningFactor;
	}

	public int getMaxPeriods() {
		return maxPeriods;
	}

	public void setMaxPeriods(int maxPeriods) {
		this.maxPeriods = maxPeriods;
	}

	public double[] getWeight() {
		return weight;
	}

	public void setWeight(double[] weight) {
		this.weight = weight;
	}

	public int getPeriods() {
		return periods;
	}

	public void setPeriods(int periods) {
		this.periods = periods;
	}

	public double getLearningFactor() {
		return learningFactor;
	}

	public void setLearningFactor(double learningFactor) {
		this.learningFactor = learningFactor;
	}

	public Vector<Integer> getErrors() {
		return errors;
	}

	public void setErrors(Vector<Integer> errors) {
		this.errors = errors;
	}

	public int activationFunction(double activation) {
		return activation > 0 ? 1 : -1;
	}

	public double activation(int[] input) {
		double value = 0;
		for (int i = 0; i < input.length; i++) {
			value += weight[i] * input[i];
		}
		return value;
	}

	public void weightUpdate(int[] input, int desiredOutput, int output) {
		for (int i = 0; i < weight.length; i++) {
			weight[i] += learningFactor * input[i] * (desiredOutput - output);
		}
	}

	public int output(int[] input) {
		return activationFunction(activation(input));
	}

	public void training(int[][] trainingValues, int[] desiredOutput) {

		errors = new Vector<Integer>();

		// START THE WEIGHTS RANDOMLY
		weight = new double[trainingValues[0].length];
		for (int i = 0; i < weight.length; i++) {
			weight[i] = Math.random();
		}

		// START PERIODS
		periods = 0;

		// ITERATE UNTIL IT LEARNS
		boolean learns = false;
		while (!learns && periods < maxPeriods) {

			// ADAPT THE WEIGHTS
			for (int j = 0; j < trainingValues.length; j++) {
				weightUpdate(trainingValues[j], desiredOutput[j], output(trainingValues[j]));
			}

			// CHECK IF THE PERCEPTRON LEARNED
			int errores = 0;
			for (int j = 0; j < trainingValues.length; j++) {
				if (desiredOutput[j] != output(trainingValues[j])) {
					errores++;
				}
			}

			errors.add(errores);

			learns = errores > 0 ? false : true;

			// INCREASE PERIODS
			periods++;
		}
	}

}
