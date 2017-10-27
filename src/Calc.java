import java.util.*;

//Evaluates given infix expression
class Calc {
    static class CalcException extends Exception {
        CalcException(String msg) {
            super(msg);
        }
    }

    private static HashMap<String, Integer> operatorPrecedenceMap = new HashMap<>();

    static {
        operatorPrecedenceMap.put("x", 2);
        operatorPrecedenceMap.put("/", 2);
        operatorPrecedenceMap.put("-", 1);
        operatorPrecedenceMap.put("+", 1);
        operatorPrecedenceMap.put("(", 0);
    }

//    private static boolean isOperator(String symbol) {
//        return operatorPrecedenceMap.containsKey(symbol);
//    }

    private static Double performCalc(String operator, LinkedList<Object> argList) {
        Double result = null;
        switch (operator) {
            case "+":
                result = (Double) argList.pollFirst();
                for (Object argument : argList)
                    result += (Double) argument;
                break;
            case "-":
                result = (Double) argList.pollFirst();
                for (Object argument : argList)
                    result -= (Double) argument;
                break;
            case "x":
                result = (Double) argList.pollFirst();
                for (Object argument : argList)
                    result *= (Double) argument;
                break;
            case "/":
                result = (Double) argList.pollFirst();
                for (Object argument : argList)
                    result /= (Double) argument;
                break;
        }
        return result;
    }

    static Object eval(LinkedList<Object> exp) throws CalcException {
        Stack<Object> operands = new Stack<>();
        LinkedList<Object> argList = new LinkedList<>();
        for (Object symbol : exp) {
            if (symbol instanceof String) {
                try {
                    int n = (Integer) operands.pop();
                    for (int i = 0; i < n; ++i)
                        argList.addFirst(operands.pop());
                    operands.push(performCalc((String) symbol, argList));
                    argList.clear();
                } catch (EmptyStackException e) {
                    throw new CalcException("Empty Stack");
                } catch (ClassCastException e) {
                    throw new CalcException("All Operands must be Double");
                }
            } else
                operands.push(symbol);
        }
        if (operands.size() != 1)
            throw new CalcException("Invalid symbolList expression");
        return operands.pop();
    }

    private static boolean isOperandCh(char ch) {
        return Character.isDigit(ch) || ch == '.' || ch == 'e';
    }

    private static boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == ',';
    }

    private static boolean isOperatorCh(char ch) {
        return !isOperandCh(ch) && !isSpace(ch);
    }

    static LinkedList<Object> parseExpression(String exp) {
        LinkedList<Object> symbolList = new LinkedList<>();
        StringBuilder symbol = new StringBuilder();
        exp += ' ';
        char prev, curr = exp.charAt(0);
        boolean isDouble = false;
        for (int i = 0; i < exp.length(); ++i) {
            prev = curr;
            curr = exp.charAt(i);
            if (isSpace(prev) && isSpace(curr))
                continue;
            if ((isOperandCh(prev) && !isOperandCh(curr) || isOperatorCh(prev) && !isOperatorCh(curr)) && symbol.length() > 0) {
                if (!isOperandCh(prev))
                    symbolList.add(symbol.toString());
                else if (isDouble)
                    symbolList.add(Double.parseDouble(symbol.toString()));
                else
                    symbolList.add(Integer.parseInt(symbol.toString()));
                symbol.setLength(0);
                isDouble = false;
            }
            if (!isSpace(curr)) {
                symbol.append(curr);
                if (curr == '.' || curr == 'e')
                    isDouble = true;
            }
        }
        return symbolList;
    }

    static LinkedList<Object> toPostfix(LinkedList<Object> symbol_list) {
        LinkedList<Object> postfix = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        int precedence;
        symbol_list.addFirst("(");
        symbol_list.addLast(")");
        for (Object symbol : symbol_list) {
            if (symbol instanceof String) {
                if (symbol.equals("("))
                    operatorStack.push((String) symbol);
                else if (symbol.equals(")")) {
                    while (!operatorStack.peek().equals("(")) {
                        postfix.add(2);
                        postfix.add(operatorStack.pop());
                    }
                    operatorStack.pop();
                } else {
                    precedence = operatorPrecedenceMap.get(symbol);
                    while (operatorPrecedenceMap.get(operatorStack.peek()) >= precedence) {
                        postfix.add(2);
                        postfix.add(operatorStack.pop());
                    }
                    operatorStack.push((String) symbol);
                }
            } else
                postfix.add(symbol);
        }
        return postfix;
    }
}
