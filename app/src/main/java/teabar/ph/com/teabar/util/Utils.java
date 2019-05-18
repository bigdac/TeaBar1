package teabar.ph.com.teabar.util;

import java.util.List;

public class Utils{
        /**
         * 得到某一天的星期几

         * @param week
         * @return
         */
        public static int getWeek(int week){
            int mWeek=0;
            switch (week) {
                case (1):
                    mWeek=7;
                    break;
                case (2):
                    mWeek=1;
                    break;
                case 3:
                    mWeek=2;
                    break;
                case 4:
                    mWeek=3;
                    break;
                case 5:
                    mWeek=4;
                    break;
                case 6:
                    mWeek=5;
                    break;
                case 7:
                    mWeek=6;
                    break;
            }
            return mWeek;
        }

        /*list轉String*/
    public static String listToString(List<String> list){

        if(list==null){
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        //第一个前面不拼接","
        for(String string :list) {
            if(first) {
                first=false;
            }else{
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    private static final int MIN_DELAY_TIME= 1000;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

}
