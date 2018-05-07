package callofmuty;

public class TestSQL {
    public static void main(String[] args){
        long dt = System.currentTimeMillis()%100;
        System.out.println("dt = "+dt);
        double multiplier = 2;
        System.out.println("mult = "+multiplier);
        System.out.println("raw mult = "+dt*multiplier);
        System.out.println("long mult = "+(long)(dt*multiplier));
    }
}