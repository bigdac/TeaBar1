/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库演示
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.3
 */
package teabar.ph.com.teabar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.view.chartview.DemoView;
import teabar.ph.com.teabar.view.chartview.IFormatterDoubleCallBack;
import teabar.ph.com.teabar.view.chartview.IFormatterTextCallBack;
import teabar.ph.com.teabar.view.chartview.RadarChart;
import teabar.ph.com.teabar.view.chartview.RadarData;
import teabar.ph.com.teabar.view.chartview.XEnum;
import teabar.ph.com.teabar.view.chartview.touch.PointPosition;


/**
 * @ClassName RadarChart02View
 * @Description  圆形雷达图的例子
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class RadarChart02View extends DemoView {

	private String TAG = "RadarChart02View";
	private RadarChart chart = new RadarChart();
	
	
	//标签集合
	private List<String> labels = new LinkedList<String>();
	private List<RadarData> chartData = new LinkedList<RadarData>();
	
	
	public RadarChart02View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		 initView();
	}
	
	public RadarChart02View(Context context, AttributeSet attrs){
        super(context, attrs);   
        initView();
	 }
	 
	 public RadarChart02View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
		 	chartLabels();
			chartDataSet();	
			chartRender();
			
			//綁定手势滑动事件
//			this.bindTouch(this,chart);
	 }
	 	 	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
       //图所占范围大小
        chart.setChartRange(w,h);
    }  	
	
	private void chartRender()
	{
		try{				
			//设置绘图区默认缩进px值
			int [] ltrb = getPieDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
			
//			chart.setTitle("圆形雷达图");
//			chart.addSubtitle("(XCL-Charts Demo)");

			//设定数据源
			chart.setCategories(labels);								
			chart.setDataSource(chartData);
			//圆形雷达图
			chart.setChartType(XEnum.RadarChartType.ROUND);
			//点击事件处理
			chart.ActiveListenItemClick();
			chart.extPointClickRange(30);
			
			
			//数据轴最大值
			chart.getDataAxis().setAxisMax(50);
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);
			//主轴标签偏移50，以便留出空间用于显示点和标签
			chart.getDataAxis().setTickLabelMargin(40);
			
//			chart.getLinePaint().setColor(Color.rgb(133, 194, 2)); //Color.parseColor("#BFE154"));
			chart.getLinePaint().setColor(Color.parseColor("#BFE154")); //Color.parseColor("#BFE154"));
			chart.getLabelPaint().setColor(Color.parseColor("#333333"));
			chart.getLabelPaint().setFakeBoldText(true);
			
			//定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					Double tmp = Double.parseDouble(value);
					DecimalFormat df=new DecimalFormat("#0");
//					String label = df.format(tmp).toString();
					String label ="";
					return (label);
				}

			});

			//定义数据点标签显示格式
//			chart.setDotLabelFormatter(new IFormatterDoubleCallBack() {
//				@Override
//				public String doubleFormatter(Double value) {
//					// TODO Auto-generated method stub
//					DecimalFormat df= new DecimalFormat("#0");
//					String label = "["+df.format(value).toString()+"]";
//					return label;
//				}});
//
			chart.enablePanMode();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
		
	}
	
	private void chartDataSet()
	{
		LinkedList<Double> dataSeriesA= new LinkedList<Double>();
		dataSeriesA.add(20d); 
		dataSeriesA.add(10d); 
		dataSeriesA.add(30d); 
		dataSeriesA.add(25d);
//		dataSeriesA.add(60d);
//		dataSeriesA.add(70d);
//		dataSeriesA.add(80d);
//		dataSeriesA.add(90d);
		
		RadarData lineData1 = new RadarData("",dataSeriesA,
					Color.rgb(234, 83, 71),XEnum.DataAreaStyle.FILL);
		lineData1.setLabelVisible(true);	
		lineData1.getPlotLine().getDotLabelPaint().setTextAlign(Align.LEFT);

		chartData.add(lineData1);

		
	}
    
	private void chartLabels()
	{
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");

//		labels.add(getResources().getText(R.string.my_power_s2).toString());
//		labels.add(getResources().getText(R.string.my_power_s3).toString());
//		labels.add(getResources().getText(R.string.my_power_s4).toString());

	}
	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(event.getAction() == MotionEvent.ACTION_UP)
		{			
//			triggerClick(event.getX(),event.getY());
		}
		return true;
	}
	
	//触发监听
	private void triggerClick(float x,float y)
	{
		PointPosition record = chart.getPositionRecord(x,y);
		if( null == record) return;
			
		if(record.getDataID() < chartData.size())
		{
			RadarData lData = chartData.get(record.getDataID());
			Double lValue = lData.getLinePoint().get(record.getDataChildID());
			
			Toast.makeText(this.getContext(),
					" Current Value:"+Double.toString(lValue) +
					" Point info:"+record.getPointInfo() , 
					Toast.LENGTH_SHORT).show();
		}
	}

}
