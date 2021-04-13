import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FinanceReports extends JFrame{

    private final Orders db;
    private final String start;
    private final String end;
    private String yAxisLabel;

    public FinanceReports(Orders database, String startDate, String endDate){
        db = database;
        start = startDate;
        end = endDate;
    }

    private void createGUI(CategoryDataset data){
        JFreeChart chart = createChart(data);
        ChartPanel graphic = new ChartPanel(chart);
        graphic.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        graphic.setBackground(Color.white);
        add(graphic);
        pack();
        setTitle("Finance Report");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private CategoryDataset getRevenueData(){

        int year = Integer.parseInt(start.substring(0,4));
        int month = Integer.parseInt(start.substring(5,7)) - 1;
        int day = Integer.parseInt(start.substring(8));
        GregorianCalendar firstDay = new GregorianCalendar(year,month,day);
        year = Integer.parseInt(end.substring(0,4));
        month = Integer.parseInt(end.substring(5,7)) - 1;
        day = Integer.parseInt(end.substring(8));
        GregorianCalendar lastDay = new GregorianCalendar(year,month,day);

        final String series = "USD";
        DefaultCategoryDataset weeks = new DefaultCategoryDataset();

        double totalRevenue = 0;
        //Data at the end of the span will be cut off if it doesn't make a full week.
        while(!firstDay.after(lastDay)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setCalendar(firstDay);
            String formattedDay = format.format(firstDay.getTime());
            firstDay.add(Calendar.DAY_OF_MONTH,6);
            if(firstDay.after(lastDay)){
                break;
            }
            String formattedDay2 = format.format(firstDay.getTime());
            Week span = new Week(formattedDay,formattedDay2);
            double revenue = span.getRevenueForWeek(db);
            weeks.addValue(revenue,series,formattedDay);
            totalRevenue = totalRevenue + revenue;
            firstDay.add(Calendar.DAY_OF_MONTH,1);
        }
        System.out.println("Total revenue for the span of time: " + totalRevenue);
        return weeks;
    }

    private CategoryDataset getOrderData(){
        int year = Integer.parseInt(start.substring(0,4));
        int month = Integer.parseInt(start.substring(5,7)) - 1;
        int day = Integer.parseInt(start.substring(8));
        GregorianCalendar firstDay = new GregorianCalendar(year,month,day);
        year = Integer.parseInt(end.substring(0,4));
        month = Integer.parseInt(end.substring(5,7)) - 1;
        day = Integer.parseInt(end.substring(8));
        GregorianCalendar lastDay = new GregorianCalendar(year,month,day);

        final String series = "Number of Orders";
        DefaultCategoryDataset weeks = new DefaultCategoryDataset();

        int totalNumOrders = 0;
        //Data at the end of the span will be cut off if it doesn't make a full week.
        while(!firstDay.after(lastDay)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setCalendar(firstDay);
            String formattedDay = format.format(firstDay.getTime());
            firstDay.add(Calendar.DAY_OF_MONTH,6);
            if(firstDay.after(lastDay)){
                break;
            }
            String formattedDay2 = format.format(firstDay.getTime());
            Week span = new Week(formattedDay,formattedDay2);
            int numOrders = span.getNumOrdersForWeek(db);
            totalNumOrders = totalNumOrders + numOrders;
            weeks.addValue(numOrders,series,formattedDay);
            firstDay.add(Calendar.DAY_OF_MONTH,1);
        }
        System.out.println("Total number of orders for the span of time: " + totalNumOrders);
        return weeks;
    }

    private JFreeChart createChart(CategoryDataset set){

        // create the chart...
        final JFreeChart chart = ChartFactory.createLineChart(
                "Finance report",       // chart title
                "Week",                    // domain axis label
                yAxisLabel,                   // range axis label
                set,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);

        // customise the renderer...
        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
//        renderer.setDrawShapes(true);

        renderer.setSeriesStroke(
                0, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {10.0f, 6.0f}, 0.0f
                )
        );

        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setSeriesItemLabelsVisible(0,true);

        return chart;
    }

    public void displayFinanceReports(){
        yAxisLabel = "Revenue (USD)";
        createGUI(getRevenueData());
        setVisible(true);
        toFront();
        setAlwaysOnTop(true);
    }

    public void displayOrderReports(){
        yAxisLabel = "Number of Customer Orders";
        createGUI(getOrderData());
        setVisible(true);
        toFront();
        setAlwaysOnTop(true);
    }

}
