import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;


public class Proiect2 {

    private JFrame windowFrame;
    private JButton buttonTest;
    private JLabel windowTitle;
    private JLabel inputFieldImage;
    private ImageIcon imageIcon = new ImageIcon(new ImageIcon("images/function_image.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH));
    private JTextField inputField;
    private JLabel inputFieldAnnotation;

    private JTextField dataFieldX;
    private JTextField dataFieldY;

    private JLabel Xlabel;
    private JLabel Ylabel;
    private JTextField predictField;
    private JLabel predictFieldAnnotation;
    private JButton predictButton;
    private JTextField predictFieldOutput;
    private JLabel predictFieldOutAnnotation;
    private JPanel perceptronSettings;
    private DrawingPanel perceptronDisplay;
    private JComboBox<String> selectActivation;
    private JLabel selectActivationAnnotation;

    private JComboBox<String> selectWeightInitializer;
    private JLabel selectWeightInitAnnotation;

    private JComboBox<String> selectLossFunction;
    private JLabel selectLossAnnotation;

    private JTextField inputNumberOfEpochs;
    private JLabel inputEpochsAnnotation;

    private JComboBox<String> selectWeightRegularization;
    private JLabel selectRegAnnotation;

    private JButton initializePerceptron;
    private JButton resetPerceptron;

    private JButton trainPerceptron;
    private JButton stopTraining;

    private JTextField inputLR;
    private JLabel inputLRAnnotation;

    private JLabel errorLoggingLabel;
    private JLabel inputSleepRateAnnotation;
    private JTextField inputSleepRate;
    private static Perceptron perceptron=null;
    private ArrayList<Double> xValues;
    private ArrayList<Double> yValues;

    private void setPerceptron(Perceptron perceptron){
        Proiect2.perceptron =perceptron;
        perceptronDisplay.setPaintTarget(Proiect2.perceptron);
    }
    private void initPerceptron() throws AssertionError{
        String activation= (String) this.selectActivation.getSelectedItem();
        String loss=(String)this.selectLossFunction.getSelectedItem();
        String weight_initializer=(String) this.selectWeightInitializer.getSelectedItem();
        String regularization=(String) this.selectWeightRegularization.getSelectedItem();
        Perceptron temp=new Perceptron(1,activation,weight_initializer,regularization,loss,perceptronDisplay);
        setPerceptron(temp);
    }

    private void generateTrainingExamples() {
        String expression = inputField.getText();
        boolean valid = Pattern.matches("[0-9+\\-*/x ]+", expression);
        if (!valid) {

        } else {
            this.xValues = new ArrayList<>();
            this.yValues = new ArrayList<>();

            Random rand = new Random();
            for (int i = 0; i < 40; i++) {
                double x = rand.nextDouble() * 10;
                double y = evaluateExpression(expression, x);
                this.xValues.add(x);
                this.yValues.add(y);
            }

            dataFieldX.setText(xValues.toString());
            dataFieldY.setText(yValues.toString());
        }
    }
    private void startTraining() throws InterruptedException {
        int epochs=Integer.parseInt(inputNumberOfEpochs.getText());
        if(perceptron==null){
          System.out.println("Error null perceptron");
        }else{
            double[][] batchedX = new double[xValues.size()][1];
            for (int i = 0; i < xValues.size(); i++) {
                batchedX[i][0] = xValues.get(i);
            }
            double[] batchedY=new double[yValues.size()];
            for (int i = 0; i < yValues.size(); i++) {
                batchedY[i] = yValues.get(i);
            }
            try{
                double learning_rate=Double.parseDouble(inputLR.getText());
                int sleep_rate=Integer.parseInt(inputSleepRate.getText());
                perceptron.setLearning_rate(learning_rate);
                perceptron.setEpochSleepInterval(sleep_rate);
            }catch(Exception e){

            }
            perceptron.trainPerceptron(batchedX,batchedY,epochs);
        }
    }
    private void runPredict() {
        String result = predictField.getText();
        try{
            double[] X= new double[1];
            X[0]=Double.parseDouble(result);
            double prediction=perceptron.predict(X);
            predictFieldOutput.setText(String.format("%.2f",prediction));

        }catch (Exception e){

        }
    }
    private double evaluateExpression(String expression, double xValue) {
        String preparedExpression = expression.replaceAll("x", String.valueOf(xValue));
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < preparedExpression.length()) ? preparedExpression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < preparedExpression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(preparedExpression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }

    Proiect2(){
        windowFrame = new JFrame("My First GUI");
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowFrame.setSize(1080,720);
        windowFrame.setLayout(null);

        windowTitle= new JLabel("Perceptron");
        windowTitle.setFont(new Font("Arial",Font.BOLD,24));
        windowTitle.setBounds(460,0,160,80);
        windowFrame.getContentPane().add(windowTitle);

        inputFieldImage=new JLabel(this.imageIcon);
        inputFieldImage.setBounds(10,100,40,30);
        windowFrame.getContentPane().add(inputFieldImage);

        inputField=new JTextField();
        inputField.setBounds(60,100,200,30);
        windowFrame.getContentPane().add(inputField);

        inputFieldAnnotation= new JLabel("Input a function (e.x 2x+3):");
        inputFieldAnnotation.setFont(new Font("Arial",Font.BOLD,14));
        inputFieldAnnotation.setBounds(10,80,260,20);
        windowFrame.getContentPane().add(inputFieldAnnotation);

        predictField=new JTextField();
        predictField.setBounds(500,100,100,30);
        windowFrame.getContentPane().add(predictField);

        predictFieldAnnotation= new JLabel("Input x value:");
        predictFieldAnnotation.setFont(new Font("Arial",Font.BOLD,14));
        predictFieldAnnotation.setBounds(500,80,260,20);
        windowFrame.getContentPane().add(predictFieldAnnotation);

        predictFieldOutput=new JTextField();
        predictFieldOutput.setBounds(720,100,100,30);
        windowFrame.getContentPane().add(predictFieldOutput);

        predictButton=new JButton("Predict");
        predictButton.setBounds(610,100,100,30);
        predictButton.addActionListener(e -> {
            runPredict();
        });
        windowFrame.getContentPane().add(predictButton);

        predictFieldOutAnnotation= new JLabel("Result:");
        predictFieldOutAnnotation.setFont(new Font("Arial",Font.BOLD,14));
        predictFieldOutAnnotation.setBounds(720,80,260,20);
        windowFrame.getContentPane().add(predictFieldOutAnnotation);

        buttonTest = new JButton("Generate");
        buttonTest.setBounds(270, 100, 110, 30);
        windowFrame.getContentPane().add(buttonTest);
        buttonTest.addActionListener(e -> {
            generateTrainingExamples();
        });
        windowFrame.setVisible(true);

        dataFieldX=new JTextField();
        dataFieldX.setBounds(25,150,430,15);
        windowFrame.getContentPane().add(dataFieldX);

        dataFieldY=new JTextField();
        dataFieldY.setBounds(25,170,430,15);
        windowFrame.getContentPane().add(dataFieldY);

        Xlabel=new JLabel("X:");
        Xlabel.setBounds(10,150,20,15);
        windowFrame.getContentPane().add(Xlabel);

        Ylabel=new JLabel("Y:");
        Ylabel.setBounds(10,170,20,15);
        windowFrame.getContentPane().add(Ylabel);

        perceptronDisplay=new DrawingPanel();
        perceptronDisplay.setBounds(0,200,620,515);
        perceptronDisplay.setBorder(BorderFactory.createTitledBorder("Visualization: "));
        perceptronDisplay.setLayout(null);
        windowFrame.getContentPane().add(perceptronDisplay);

        perceptronSettings=new JPanel();
        perceptronSettings.setBounds(679,200,400,515);
        perceptronSettings.setBorder(BorderFactory.createTitledBorder("Perceptron Settings: "));
        perceptronSettings.setLayout(null);
        windowFrame.getContentPane().add(perceptronSettings);

        selectActivationAnnotation= new JLabel("Select Activation: ");
        selectActivationAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        selectActivationAnnotation.setBounds(5,40,180,20);
        perceptronSettings.add(selectActivationAnnotation);

        String[] items = {"Linear", "Sigmoid", "Hyperbolic Tangent", "Rectified Linear Unit"};
        selectActivation = new JComboBox<>(items);
        selectActivation.setBounds(190,40,160,20);
        perceptronSettings.add(selectActivation);

        selectWeightInitAnnotation= new JLabel("Select Weight Initializer: ");
        selectWeightInitAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        selectWeightInitAnnotation.setBounds(5,70,180,20);
        perceptronSettings.add(selectWeightInitAnnotation);

        String[] items2 = {"zeros","random"};
        selectWeightInitializer = new JComboBox<>(items2);
        selectWeightInitializer.setBounds(190,70,160,20);
        perceptronSettings.add(selectWeightInitializer);

        selectLossAnnotation= new JLabel("Select Loss Function: ");
        selectLossAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        selectLossAnnotation.setBounds(5,100,180,20);
        perceptronSettings.add(selectLossAnnotation);

        String[] items3 = {"MeanSquaredError","MeanAbsoluteError"};
        selectLossFunction = new JComboBox<>(items3);
        selectLossFunction.setBounds(190,100,160,20);
        perceptronSettings.add(selectLossFunction);

        selectRegAnnotation = new JLabel("Select Regularization: ");
        selectRegAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        selectRegAnnotation.setBounds(5,130,180,20);
        perceptronSettings.add(selectRegAnnotation);

        String[] items4 = {"L1","L2"};
        selectWeightRegularization = new JComboBox<>(items4);
        selectWeightRegularization.setBounds(190,130,160,20);
        perceptronSettings.add(selectWeightRegularization);

        initializePerceptron = new JButton("Create");
        initializePerceptron.setBounds(210, 180, 90, 25);
        initializePerceptron.addActionListener(e -> initPerceptron());
        perceptronSettings.add(initializePerceptron);

        resetPerceptron = new JButton("Reset");
        resetPerceptron.setBounds(90, 180, 90, 25);
        resetPerceptron.addActionListener(e -> setPerceptron(null));
        perceptronSettings.add(resetPerceptron);


        inputEpochsAnnotation= new JLabel("Input Number of Epochs: ");
        inputEpochsAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        inputEpochsAnnotation.setBounds(5,255,180,20);
        perceptronSettings.add(inputEpochsAnnotation);

        inputNumberOfEpochs= new JTextField();
        inputNumberOfEpochs.setBounds(190,255,160,20);
        perceptronSettings.add(inputNumberOfEpochs);

        inputLRAnnotation= new JLabel("Input Learning Rate: ");
        inputLRAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        inputLRAnnotation.setBounds(5,285,180,20);
        perceptronSettings.add(inputLRAnnotation);

        inputLR= new JTextField();
        inputLR.setBounds(190,285,160,20);
        perceptronSettings.add(inputLR);

        inputSleepRateAnnotation= new JLabel("Input Sleep time (ms)");
        inputSleepRateAnnotation.setFont(new Font("Arial",Font.BOLD,12));
        inputSleepRateAnnotation.setBounds(5,310,180,20);
        perceptronSettings.add(inputSleepRateAnnotation);

        inputSleepRate= new JTextField();
        inputSleepRate.setBounds(190,310,160,20);
        perceptronSettings.add(inputSleepRate);

        trainPerceptron = new JButton("Train");
        trainPerceptron.setBounds(210, 360, 90, 25);
        perceptronSettings.add(trainPerceptron);
        trainPerceptron.addActionListener(e -> {
            try {
                startTraining();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        stopTraining = new JButton("Stop");
        stopTraining.setBounds(90, 360, 90, 25);
        stopTraining.addActionListener(e -> {
            synchronized (perceptron) {
                perceptron.setStopTrainingFlag(true);
            }
        });
        perceptronSettings.add(stopTraining);

        errorLoggingLabel=new JLabel("");
        errorLoggingLabel.setFont(new Font("Arial",Font.BOLD,14));
        errorLoggingLabel.setBounds(5,450,400,20);
        perceptronSettings.add(errorLoggingLabel);

        perceptronSettings.revalidate();
        perceptronSettings.repaint();


    }
}
