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
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 2.1
 */

package teabar.ph.com.teabar.view.chartview;


/**
 * @ClassName PointD
 * @Description 点位置类
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 *  
 */

public class PointD {
	
	public double x = 0d;
	public double y = 0d;
	
	public PointD()
	{	
	}

	public PointD(double x, double y)
	{	
		this.x = x;
		this.y = y;
	}
		
}
