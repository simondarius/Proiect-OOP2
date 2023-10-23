import java.util.Arrays;
import java.util.Random;
public class Perceptron {
    private int dim_input;
    private double learning_rate;
    private final double E=2.71828183;
    private double[] lastInput;
    private double[] weight_vector;
    private double output=Double.NaN;
    private double[] gradient_vector;
    private double DerivedLoss;
    private double DerivedActivation;
    private String activation_function;
    private String LossFunction;
    private String regularization;
    public double LossNumerical=Double.NaN;

    public void setEpochSleepInterval(int epochSleepInterval) {
        this.epochSleepInterval = epochSleepInterval;
    }

    private boolean stopTrainingFlag=false;
    private int epochSleepInterval=500;
    public DrawingPanel visualization_panel;

    public void setStopTrainingFlag(boolean stopTrainingFlag) {
        this.stopTrainingFlag = stopTrainingFlag;
    }

    public double[] getLastInput() {
        return lastInput;
    }

    public Perceptron(int dim_input, String activation_function, String weight_initializer, String regularization, String LossFunction, DrawingPanel visualization_panel) {
        this.dim_input = dim_input;
        this.activation_function = activation_function;
        this.learning_rate=0.001;
        this.weight_vector=new double[dim_input];
        this.visualization_panel=visualization_panel;
        for(int i=0;i< this.weight_vector.length;i++){
            switch (weight_initializer){
                case "zeros":
                    this.weight_vector[i]=0;
                    break;
                case "random":
                    this.weight_vector[i] = new Random().nextDouble();
                    break;
                default:
                    break;
            }
        }
        this.gradient_vector=new double[dim_input];
        this.output=Double.NaN;
        this.LossFunction=LossFunction;
        this.regularization=regularization;

    }
    public void trainPerceptron(double[][] train_data,double[] labels,int epochs) {
        new Thread(() -> {
            try {

                this.train(train_data, labels, epochs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void train(double[][] train_data,double[] labels,int epochs) throws InterruptedException {
        for(int i=1;i<=epochs;i++){
           visualization_panel.repaintWithInfo("Epoch nr "+i+" started");
           for(int j=0;j<train_data.length;j++) {
               //System.out.println("Input is : "+ Arrays.toString(train_data[j]));
               computeOutput(train_data[j]);
               //System.out.println("Pre-Activation output: " + this.output);
               this.output=calculateActivation();
               //System.out.println("Post-Activation output: " + this.output);
               calculateLoss(labels[j],getOutput());
               computeGradients();
               //System.out.println("The gradients are: " + Arrays.toString(this.gradient_vector));
               //System.out.println("The new weights are: "+Arrays.toString(this.weight_vector));
               applyGradients();
               visualization_panel.repaintWithInfo("Epoch nr "+i+" started");
               Thread.sleep(epochSleepInterval/5);
           }
           visualization_panel.repaintWithInfo("Epoch nr "+i+" finished");
           if(stopTrainingFlag)break;
           Thread.sleep(epochSleepInterval);
        }
    }
    public double predict(double[] predictionTarget){
        computeOutput(predictionTarget);
        this.output=calculateActivation();
        return this.output;
    }

    private double computeOutput(double[] input){
        assert (input.length == this.dim_input);
        double temp_output=0;

        for(int i=0;i<this.dim_input;i++){
            temp_output+=this.weight_vector[i]*input[i];
        }
        this.output=temp_output;
        this.lastInput=input;
        return this.output;
    }

    private void computeGradients(){
      for(int i=0;i< this.dim_input;i++){
          this.gradient_vector[i]=DerivedLoss*DerivedActivation*lastInput[i];
      }
    }
    private void applyGradients(){
        for(int i=0;i< this.dim_input;i++){
            this.weight_vector[i]-=learning_rate*this.gradient_vector[i];
        }
    }

    private double calculateLoss(double y_true,double y_pred){
        /*double reg_factor=0;
        if(this.regularization.equals("L1")){
            for(int i=0;i<dim_input;i++){
                reg_factor+=Math.abs(this.weight_vector[i]);
            }
        }
        if(this.regularization.equals("L2")){
            for(int i=0;i<dim_input;i++){
                reg_factor+=Math.pow(this.weight_vector[i],2);
            }
        }*/
        if(this.LossFunction.equals("MeanSquaredError")) {
            this.DerivedLoss=-2*(y_true-y_pred);
            this.LossNumerical=Math.pow((y_true - y_pred), 2);
            return Math.pow((y_true - y_pred), 2);
        }
        if(this.LossFunction.equals("MeanAbsoluteError")) {
            this.DerivedLoss=-2*(y_true-y_pred);
            this.LossNumerical=Math.pow((y_true - y_pred), 2);
            return Math.abs((y_true-y_pred));
        }
        this.DerivedLoss=Double.NaN;
        this.LossNumerical=Double.NaN;
        return Double.NaN;
    }
    private double calculateActivation(){
        switch (this.activation_function){
            case "Sigmoid":
                this.DerivedActivation=1/(1+Math.pow(this.E,-this.output))*(1-1/(1+Math.pow(this.E,-this.output)));
                return 1/(1+Math.pow(this.E,-this.output));
            case "Rectified Linear Unit":
                if(this.output>0)this.DerivedActivation=1;
                else this.DerivedActivation=0;
                return Math.max(0,this.output);
            case "Linear":
                this.DerivedActivation=1;
                return this.output;
            case "Hyperbolic Tangent":
                this.DerivedActivation=1-Math.pow((Math.pow(this.E,2*this.output)-1/Math.pow(this.E,2*this.output)+1),2);
                return Math.pow(this.E,2*this.output)-1/Math.pow(this.E,2*this.output)+1;
            default:
                return Double.NaN;

        }
    }
    public int getDim_input() {
        return dim_input;
    }

    public double[] getWeight_vector() {
        return weight_vector;
    }

    public double getOutput() {
        return output;
    }

    public double[] getGradient_vector() {
        return gradient_vector;
    }

    public String getActivation_function() {
        return activation_function;
    }

    public void setLearning_rate(double learning_rate) {
        this.learning_rate = learning_rate;
    }
}
