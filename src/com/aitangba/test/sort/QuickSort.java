package com.aitangba.test.sort;

/**
 * Created by fhf11991 on 2017/4/18.
 */
public class QuickSort implements BaseSort {

    @Override
    public void sort(int[] numbers) {
        if (numbers.length > 0) {    //查看数组是否为空
            quickSort(numbers, 0, numbers.length - 1);
        }
    }

    private int getMiddle(int[] list, int low, int high) {
        int tmp = list[low];    //数组的第一个作为中轴
        while (low < high) {
            while (low < high && list[high] >= tmp) {
                high--;
            }
            list[low] = list[high];   //比中轴小的记录移到低端
            while (low < high && list[low] <= tmp) {
                low++;
            }
            list[high] = list[low];   //比中轴大的记录移到高端
        }
        list[low] = tmp;              //中轴记录到尾
        return low;                   //返回中轴的位置
    }

    private void quickSort(int[] list, int low, int high) {
        if (low < high) {
            int middle = getMiddle(list, low, high);  //将list数组进行一分为二
            quickSort(list, low, middle - 1);        //对低字表进行递归排序
            quickSort(list, middle + 1, high);       //对高字表进行递归排序
        }
    }

}
