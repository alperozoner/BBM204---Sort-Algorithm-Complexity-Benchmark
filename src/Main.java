import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.time.*;
import java.util.*;
import java.io. * ;
//-Xmx2048M
// compile: javac -cp *.jar *.java -d .
// run: java -cp '.;*' Main .\TrafficFlowDataset.csv
// ex: java -cp '.;*' -Xmx3072M Main .\TrafficFlowDataset.csv
class Main {

    public static final String delimiter = ",";

    public static int getCsvSize(String csvFile) {
        int size = 0;
        try {
            File file = new File(csvFile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] tempArr;
            while ((line = br.readLine()) != null) {
                size++;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return size-1;
    }

    public static Integer[] readCSV(String csvFile) {
        Integer[] flowDurationList = new Integer[]{};
        try {
            File file = new File(csvFile);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] tempArr;
            flowDurationList = new Integer[251281];
            br.readLine(); // skip the first line as it contains the column names
            int i = 0;
            while ((line = br.readLine()) != null) {
                tempArr = line.split(delimiter);
                flowDurationList[i] = Integer.valueOf(tempArr[7]);
                i++;
            }
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flowDurationList;
    }

    public static void main(String args[]) throws IOException {
        //String csvFile = "src/TrafficFlowDataset.csv";
        int experiment = 2; // 1: random 2: sorted 3: reverse sorted
        String csvFile = args[0];
        Integer[] flowDurationList = new Integer[]{};
        Integer[] sortedList = new Integer[]{};
        Integer[] reverseSortedList = new Integer[]{};

        flowDurationList = readCSV(csvFile);
        sortedList = flowDurationList.clone();
        int len = flowDurationList.length;
        int min = getMin(flowDurationList,len);
        int max = getMax(flowDurationList,len);

        CountingSort.sort(sortedList, len, min, max);
        reverseSortedList = reverse(sortedList, len);
        //flowDurationList = trimArray(flowDurationList);
        //printArray(flowDurationList);
        // X axis (input size)
        int[] inputAxis = {512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 251281};
        int xLen = inputAxis.length;
        // Y axis time elapsed while sorting
        double[][] yAxis = new double[4][xLen];

        for (Integer i = 0; i < 4; i++) { // loop for 4 different sorting algortihms

            for (int j = 0; j < xLen; j++) { // do the experiment for different input sizes [512,1024,...,251281]
                double totalTime = 0.0;
                int inputSize = inputAxis[j];
                Integer[] tempArr = new Integer[inputSize];
                
                if (experiment == 1) //random data
                    tempArr = copyArray(flowDurationList,inputSize);
                else if (experiment == 2) //sorted data
                    tempArr = copyArray(sortedList,inputSize);
                else if (experiment == 3) //reverse sorted data
                    tempArr = copyArray(reverseSortedList,inputSize);

                int tempMin = getMin(tempArr,inputSize);
                int tempMax = getMax(tempArr,inputSize);

                for (int n = 0; n < 10; n++) { // run each experiment 10 times
                    Instant start = Instant.now();
                    switch (i) {
                        case 0:
                            InsertionSort.sort(tempArr);
                            break;
                        case 1:
                            Integer[] aux = new Integer[inputSize];
                            MergeSort.sort(tempArr, aux, 0, inputSize - 1);
                            break;
                        case 2:
                            PigeonHole.sort(tempArr, inputSize, tempMin, tempMax);
                            break;
                        case 3:
                            CountingSort.sort(tempArr, inputSize, tempMin, tempMax);
                            break;
                    }
                    Instant finish = Instant.now();
                    double timeElapsed = Duration.between(start, finish).toMillis();
                    totalTime += timeElapsed;
                    //System.out.println("Breakpoint here!");
                }
                yAxis[i][j] = totalTime / 10; // store the average running time over 10 iterations for each case
            }
        }
        for (int i = 0; i < yAxis.length; i++) {
            for (int j = 0; j < yAxis[0].length; j++) {
                System.out.printf(String.valueOf(yAxis[i][j]) + ",");
            }
            System.out.println();
        }
        if (experiment == 1) //random data
        	showAndSaveChart("Experiment on Given Random Data", inputAxis, yAxis);
        else if (experiment == 2) //sorted data
        	showAndSaveChart("Experiment on Sorted Data", inputAxis, yAxis);
        else if (experiment == 3) //reverse sorted data
        	showAndSaveChart("Experiment on Reverse Sorted Data", inputAxis, yAxis);
        
    }



        // Create sample data for linear runtime
        //double[][] yAxis = new double[2][10];
        //yAxis[0] = new double[]{512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 251282};
        //yAxis[1] = new double[]{300, 800, 1800, 3000, 7000, 15000, 31000, 64000, 121000, 231000};

        // Save the char as .png and show it
        // showAndSaveChart("Sample Test", inputAxis, yAxis);

        // my code below:
        // create random list and print

        /*
        Integer[] arr = new Integer[10];
        Integer[] aux = new Integer[10];

        Random rd = new Random();
        for (int i = 0; i < 10; i++) {
            arr[i] = rd.nextInt(100);
        }
        */

        //printArrayList(arr);

        // sort and print
        //InsertionSort.sort(arr);
        //MergeSort.sort(arr, aux, 0, arr.length-1);
        //PigeonHole.sort(arr, arr.length);
        //CountingSort.sort(arr);

        //printArrayList(arr);
    public static Integer[] copyArray (Integer[] a, int len){
        Integer[] b = new Integer[len];
        for (int i = 0; i < len; i++) {
            b[i] = a[i];
        }
        return b;
    }

    public static int getMin (Integer[] a, int len){
        int min = a[0];
        for (int i = 0; i < len; i++) {
            if (a[i] < min)
                min = a[i];
        }
        return min;
    }

    public static int getMax (Integer[] a, int len){
        int max = a[0];
        for (int i = 0; i < len; i++) {
            if (a[i] > max)
                max = a[i];
        }
        return max;
    }
    public static Integer[] reverse(Integer[] a, int n) {
        Integer[] b = new Integer[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j--;
        }
        return b;
    }
    public static void showAndSaveChart(String title, int[] xAxis, double[][] yAxis) throws IOException {
        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title(title)
                .yAxisTitle("Time in Milliseconds").xAxisTitle("Input Size").build();

        // Convert x axis to double[]
        double[] doubleX = Arrays.stream(xAxis).asDoubleStream().toArray();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        // Add a plot for a sorting algorithm
        chart.addSeries("Insertion Sort", doubleX, yAxis[0]);
        chart.addSeries("Merge Sort", doubleX, yAxis[1]);
        chart.addSeries("Pigeonhole Sort", doubleX, yAxis[2]);
        chart.addSeries("Counting Sort", doubleX, yAxis[3]);
        //chart.addSeries("Sample Data 2", doubleX, yAxis[1]);

        // Save the chart as PNG
        BitmapEncoder.saveBitmap(chart, title + ".png", BitmapEncoder.BitmapFormat.PNG);

        // Show the chart
        new SwingWrapper(chart).displayChart();
    }

    public static void printArray(Integer[] arr) {
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            if (arr[i] != null) {
                if (i != len - 1)
                    System.out.printf(arr[i].toString() + ',');
                else
                    System.out.printf(arr[i].toString() + '\n');
            }
        }
    }

    public static Integer[] trimArray(Integer[] oldArray) {
        int count = 0;
        for (Integer i : oldArray) {
            if (i != null) {
                count++;
            }
        }

        Integer[] newArray = new Integer[count];

        int index = 0;
        for (Integer i : oldArray) {
            if (i != null) {
                newArray[index++] = i;
            }
        }
        return newArray;
    }
}
