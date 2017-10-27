import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        String exp = "10x5";
        LinkedList<Object> list = Calc.parseExpression(exp);
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
