package teabar.ph.com.teabar.view;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;

public class BarChartManager {
    private BarChart mBarChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private  Context context;

    public BarChartManager(BarChart barChart,Context context) {
        this.mBarChart = barChart;
        leftAxis = mBarChart.getAxisLeft();
        rightAxis = mBarChart.getAxisRight();
        xAxis = mBarChart.getXAxis();
        this.context = context;
    }
    public class XFormattedValue extends ValueFormatter

    {


        private Context context;
        private List<String> mValues;
        public XFormattedValue(Context context,List<String> mValues){
            this.context=context;
        }
        /*
         * 重写该方法根据值返回自定义的数值
         * */
        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (value==1){
                return context.getText(R.string.weather_week_71).toString();
            }else if (value==2){
                return context.getText(R.string.weather_week_11).toString();
            }else if (value==3){
                return context.getText(R.string.weather_week_21).toString();
            }else if (value==4){
                return context.getText(R.string.weather_week_31).toString();
            }else if (value==5){
                return context.getText(R.string.weather_week_41).toString();
            }else if (value==6){
                return context.getText(R.string.weather_week_51).toString();
            }else if (value==7){
                return context.getText(R.string.weather_week_61).toString();
            }
            return "";
        }


    }
    /**
     * 初始化LineChart
     */
    private void initLineChart(int num) {


        //背景颜色
        mBarChart.setBackgroundColor(Color.WHITE);
        //网格
        mBarChart.setDrawGridBackground(false);
        //背景阴影
        mBarChart.setDrawBarShadow(false);
        
        mBarChart.setHighlightFullBarEnabled(false);

        //显示边界
        mBarChart.setDrawBorders(false);

        //设置动画效果
//        mBarChart.animateY(1000, Easing.Linear);
//        mBarChart.animateX(1000, Easing.Linear);

        //折线图例 标签 设置
        Legend legend = mBarChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        legend.setTextColor(Color.parseColor("#333333"));
        legend.setEnabled(false);


        mBarChart.setScaleXEnabled(false);/**禁止缩放x轴*/
        mBarChart.setScaleYEnabled(false);/**缩放y轴*/
        leftAxis.setDrawLabels(false);

        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //XY轴的设置
        //X轴设置显示位置在底部

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);

//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setAxisLineColor(Color.parseColor("#dedede"));
//        xAxis.setGridColor(Color.parseColor("#eeeeee"));
//        xAxis.setAxisLineColor(Color.parseColor("#eeeeee"));
//        xAxis.setAxisLineColor(Color.parseColor("#20e2ff"));
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setDrawGridLines(false);
//        leftAxis.setDrawAxisLine(false);
//        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineWidth(0f);
        rightAxis.setAxisMinimum(0f);
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisLineWidth(0f);
//        leftAxis.setTextColor(Color.parseColor("#ffffff"));
//        leftAxis.setEnabled(false);
        //手机屏幕上显示6剩下的滑动直方图然后显示

        //显示的时候是按照多大的比率缩放显示，1f表示不放大缩小



    }

    /**
     * 展示柱状图(一条)
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param label
     * @param color
     */
    String label1 = "";
    public void showBarChart(List<String> xAxisValues, List<Integer> yAxisValues, String label, int color) {
        initLineChart(xAxisValues.size());
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisValues.size(); i++) {
            entries.add(new BarEntry(i, yAxisValues.get(i)));
//            entries.add(new BarEntry(xAxisValues.get(i), yAxisValues.get(i)));
        }
        // 每一个BarDataSet代表一类柱状图

        BarDataSet barDataSet = new BarDataSet(entries, label);
        int color1[] ={Color.parseColor("#8D9DFC"),Color.parseColor("#C3A6FA")};
//        barDataSet.setColor(color1);

        barDataSet.setGradientColor(Color.parseColor("#8D9DFC"),Color.parseColor("#C3A6FA"));
        barDataSet.setValueTextSize(9f);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return new DecimalFormat("###,###,###,##0").format(v);
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);
        //设置X轴的刻度数

        xAxis.setLabelCount(xAxisValues.size()-1,false);
        if (xAxisValues.size()==7){
            List<String> data1=new ArrayList<>();
            data1.add(0,context.getText(R.string.weather_week_71).toString());
            data1.add(1,context.getText(R.string.weather_week_11).toString());
            data1.add(2,context.getText(R.string.weather_week_21).toString());
            data1.add(3,context.getText(R.string.weather_week_31).toString());
            data1.add(4,context.getText(R.string.weather_week_41).toString());
            data1.add(5,context.getText(R.string.weather_week_51).toString());
            data1.add(6,context.getText(R.string.weather_week_61).toString());
            xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
//                    return super.getFormattedValue(value);
                    return String.valueOf(data1.get((int) value));
                }
                //                @Override
//                public String getFormattedValue(float value, AxisBase axis) {
//                    return String.valueOf(data1.get((int) value));
//
//                }
//
//                @Override
//                public int getDecimalDigits() {
//                    return 0;
//                }
            });


        }else
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
//        xAxis.setAxisLineColor(Color.parseColor("#20e2ff"));
        if (xAxisValues.size()==7) {
            mBarChart.zoom(  1.0f,1f,0,0);
        }else {
            mBarChart.zoom(  2.0f,1f,0,0);
        }

        rightAxis.setEnabled(false);

//        mBarChart.resetZoom();
        mBarChart.setData(data);
    }



    /**
     * 展示柱状图(多条)
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param labels
     * @param colours
     */
    public void showBarChart(List<Float> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours) {
        initLineChart(xAxisValues.size());
        BarData data = new BarData();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {
                entries.add(new BarEntry(xAxisValues.get(j), yAxisValues.get(i).get(j)));
            }
            BarDataSet barDataSet = new BarDataSet(entries, labels.get(i));
            barDataSet.setColor(colours.get(i));
            barDataSet.setValueTextColor(colours.get(i));
            barDataSet.setValueTextSize(10f);
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            data.addDataSet(barDataSet);
        }
        int amount = yAxisValues.size();
        float groupSpace = 0.12f; //柱状图组之间的间距
        float barSpace = (float) ((1 - 0.12) / amount / 10); // x4 DataSet
        float barWidth = (float) ((1 - 0.12) / amount / 10 * 9); // x4 DataSet
        // (0.2 + 0.02) * 4 + 0.08 = 1.00 -> interval per "group"
        xAxis.setLabelCount(xAxisValues.size() - 1, false);

        data.setBarWidth(barWidth);


        data.groupBars(0, groupSpace, barSpace);
        mBarChart.setData(data);
    }


    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);
        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        mBarChart.invalidate();
    }
    /**
     * 设置X轴的值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setXAxis(float max, float min, int labelCount) {
        xAxis.setAxisMaximum(max);
        xAxis.setAxisMinimum(min);
        xAxis.setLabelCount(labelCount, false);
        mBarChart.invalidate();
    }
    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        leftAxis.removeAllLimitLines();
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(1f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        hightLimit.enableDashedLine(8f,5f,0);  //设置虚线
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }
    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(1f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }
    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mBarChart.setDescription(description);
        mBarChart.invalidate();
    }

}
