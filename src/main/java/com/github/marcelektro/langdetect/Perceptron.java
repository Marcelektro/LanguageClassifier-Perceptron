package com.github.marcelektro.langdetect;

/**
 * Discrete unipolar perceptron
 * <p>
 * Taken from my <a href="https://github.com/Marcelektro/Iris-Perceptron/blob/662b70dd982132ca171d2a5d35ba64d0b21a90d9/src/main/java/com/github/marcelektro/iris/Perceptron.java">other project</a>.
 */
public class Perceptron {

    private double[] weights;
    private double threshold;
    private double alpha;
    private double beta;

    private double netValue;


    // task requirement: constructor with weights, vector len, threshold and alpha val
    public Perceptron(
            int weightsLen,
            double threshold,
            double alpha
    ) {
        this.weights = new double[weightsLen];
        this.threshold = threshold;
        this.alpha = alpha;
        this.beta = alpha; // for now, good enough (as per task requirement)
    }



    public int compute(double[] input) {
        if (input.length != this.weights.length)
            throw new IllegalArgumentException("input arr length must be equal to weight arr length");

        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            sum += (input[i] * this.weights[i]);
        }

        this.netValue = sum;

        return sum >= 0 ? 1 : 0;
    }

    public void learn(double[] input, double answer) {
        if (input.length != this.weights.length)
            throw new IllegalArgumentException("input and answer array lengths must equal weight array length");

        // delta rule learning with threshold modification

        final double diff = answer - compute(input);
        for (int i = 0; i < this.weights.length; i++) {
            this.weights[i] = this.weights[i] + diff * this.alpha * input[i];
        }

        this.threshold = this.threshold + diff * -this.beta;
    }



    public double[] getWeights() {
        return weights;
    }

    public double getThreshold() {
        return threshold;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getNetValue() {
        return netValue;
    }

}
