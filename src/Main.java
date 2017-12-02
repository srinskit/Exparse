import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        String exp = "1x2.0x3.0/4.0xAVG(10.0,20,0,50.0)";
        LinkedList<String> list = Calc.parseExpression(exp);
        System.out.println(list);
        list = Calc.toPostfix(list);
        System.out.println(list);
        try {
            System.out.println(Calc.eval(list));
        } catch (Calc.CalcException e) {
            System.out.println(e.getMessage());
        }
    }
}
