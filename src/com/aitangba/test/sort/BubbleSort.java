package com.aitangba.test.sort;

/**
 * Created by fhf11991 on 2017/4/18.
 */
public class BubbleSort implements BaseSort {

    @Override
    public void sort(int[] numbers) {
        for (int i = 0; i < numbers.length -1; i++){    //最多做n-1趟排序
            for(int j = 0 ;j < numbers.length - i - 1; j++){    //对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
                if(numbers[j] < numbers[j + 1]){    //把小的值交换到后面
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
    }
}
