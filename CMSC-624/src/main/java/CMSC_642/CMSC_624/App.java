package CMSC_642.CMSC_624;

/**
 * Hello world!
 *
 */
public class App 
{
    private int[] computeNums(int[] arr) {
        int[] res = new int[arr[2]];
        res[0] = arr[0] * arr[1];

        for (int i = 1; i < arr[2]; i++) res[i] = res[i-1] * arr[1];
        for (int i = 0; i < arr[2]; i++) System.out.print(res[i] + ",");

        System.out.println();

        return res;
    }

    private double onlyLastNum(int[] arr) {
        return arr[0] * Math.pow(arr[1], arr[2]);
    }

    public static void main(String[] args) {

        System.out.println( "Hello World!\n" );

        App obj = new App();

        System.out.println("print entire sequence:");
        obj.computeNums(new int[] {3,2,3});
        obj.computeNums(new int[] {4,3,2});
        obj.computeNums(new int[] {3,4,4});
        obj.computeNums(new int[] {2,3,3});
        obj.computeNums(new int[] {4,2,2});
        obj.computeNums(new int[] {3,4,3});

        System.out.println("\nprint only the last num:");
        System.out.println(obj.onlyLastNum(new int[] {3,2,3}));
        System.out.println(obj.onlyLastNum(new int[] {4,3,2}));
        System.out.println(obj.onlyLastNum(new int[] {3,4,4}));
        System.out.println(obj.onlyLastNum(new int[] {2,3,3}));
        System.out.println(obj.onlyLastNum(new int[] {4,2,2}));
        System.out.println(obj.onlyLastNum(new int[] {3,4,3}));


    }
}
