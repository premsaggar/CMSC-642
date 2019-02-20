package main.java.CMSC_642.CMSC_624;

/**
 * Hello world!
 *
 */
public class App 
{
    public App(){

    }

    public int form(int x1, int x2, int x3){
        int s = x1;
        for(int i = 0; i < x3; i++){
            s = s*x2;
        }
        return s;
    }


}
