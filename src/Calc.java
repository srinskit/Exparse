import java.util.*;

class Calc {
    static class CalcException extends Exception {
        CalcException(String msg) {
            super(msg);
        }
    }

    interface Operation {
        short BASIC = 0, FUNCTION = 1, PAREN = 2;
    }

    private static HashMap<String, Integer> operatorPrecedenceMap = new HashMap<>();
    private static HashMap<String, Short> operationType = new HashMap<>();

    static {
        operationType.put("x", Operation.BASIC);
        operationType.put("/", Operation.BASIC);
        operationType.put("-", Operation.BASIC);
        operationType.put("+", Operation.BASIC);
        operationType.put("MAX(", Operation.FUNCTION);
        operationType.put("MIN(", Operation.FUNCTION);
        operationType.put("AVG(", Operation.FUNCTION);
        operationType.put("(", Operation.PAREN);
        operationType.put(")", Operation.PAREN);

        operatorPrecedenceMap.put("x", 2);
        operatorPrecedenceMap.put("/", 2);
        operatorPrecedenceMap.put("-", 1);
        operatorPrecedenceMap.put("+", 1);
        operatorPrecedenceMap.put("(", 0);
        operatorPrecedenceMap.put("MAX(", -1);
        operatorPrecedenceMap.put("MIN(", -1);
        operatorPrecedenceMap.put("AVG(", -1);
    }

    //Applies operation on argument list
    private static Double performCalc(String operation, LinkedList<Double> argList) {
        Double result = null;
        switch (operation) {
            case "+":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result += argument;
                break;
            case "-":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result -= argument;
                break;
            case "x":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result *= argument;
                break;
            case "/":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result /= argument;
                break;
            case "MAX(":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result = argument > result ? argument : result;
                break;
            case "MIN(":
                result = argList.pollFirst();
                for (Double argument : argList)
                    result = argument < result ? argument : result;
                break;
            case "AVG(":
                result = 0.0;
                for (Double argument : argList)
                    result += argument;
                if (argList.size() > 0) {
                    result /= argList.size();
                }
                break;
        }
        return result;
    }

    //Evaluates postfix ll
    static double eval(LinkedList<String> exp) throws CalcException {
        Stack<Double> operands = new Stack<>();
        LinkedList<Double> argList = new LinkedList<>();
        for (String symbol : exp) {
            if (isOperand(symbol)) {
                operands.push(Double.parseDouble(symbol));
            } else {
                try {
                    int n = operands.pop().intValue();
                    for (int i = 0; i < n; ++i)
                        argList.addFirst(operands.pop());
                    operands.push(performCalc(symbol, argList));
                    argList.clear();
                } catch (EmptyStackException e) {
                    throw new CalcException("Empty Stack");
                } catch (ClassCastException e) {
                    throw new CalcException("All Operands must be Double");
                }
            }
        }
        if (operands.size() != 1)
            throw new CalcException("Invalid symbolList expression");
        return operands.pop();
    }

    private static boolean isOperandCh(char ch) {
        return Character.isDigit(ch) || ch == '.' || ch == 'e';
    }

    private static boolean isSpaceCh(char ch) {
        return ch == ' ' || ch == '\t' || ch == ',';
    }

    private static boolean isOperatorCh(char ch) {
        return !isOperandCh(ch) && !isSpaceCh(ch);
    }

    //Parses expression to ll
    static LinkedList<String> parseExpression(String exp) {
        LinkedList<String> symbolList = new LinkedList<>();
        StringBuilder symbol = new StringBuilder();
        exp += ' ';
        char prev, curr = exp.charAt(0);
        for (int i = 0; i < exp.length(); ++i) {
            prev = curr;
            curr = exp.charAt(i);
            if (isSpaceCh(prev) && isSpaceCh(curr))
                continue;
            if ((isOperandCh(prev) && !isOperandCh(curr) || isOperatorCh(prev) && !isOperatorCh(curr) ||
                    Character.isLowerCase(prev) && Character.isUpperCase(curr)) && symbol.length() > 0) {
                symbolList.addLast(symbol.toString());
                symbol.setLength(0);
            }
            if (!isSpaceCh(curr)) {
                symbol.append(curr);
            }
        }
        return symbolList;
    }


    private static boolean isFunction(String symbol) {
        return symbol.length() >= 3;
    }

    private static boolean isOperand(String symbol) {
        return !operationType.containsKey(symbol);
    }
//    private static boolean canMergeOperation(String previous, String current) {
//        return (previous == null || previous.equals(current)) && !current.equals("(") && !current.equals(")");
//    }

    //Converts infix to postfix
    static LinkedList<String> toPostfix(LinkedList<String> symbol_list) {
        LinkedList<String> postfix = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        int precedence, count = 0;
        symbol_list.addFirst("(");
        symbol_list.addLast(")");
        for (String symbol : symbol_list) {
            if (isOperand(symbol)) {
                postfix.addLast(symbol);
                count++;
            } else {
                if (symbol.equals("(")) {
                    operatorStack.push(symbol);
                } else if (isFunction(symbol)) {
                    operatorStack.push(symbol);
                    count = 0;
                } else if (symbol.equals(")")) {
                    if (isFunction(operatorStack.peek())) {
                        postfix.addLast(Integer.toString(count));
                        postfix.addLast(operatorStack.pop());
                    } else {
                        while (!operatorStack.peek().equals("(")) {
                            postfix.addLast(Integer.toString(2));
                            postfix.addLast(operatorStack.pop());
                        }
                        operatorStack.pop();
                    }
                } else {
                    precedence = operatorPrecedenceMap.get(symbol);
                    while (operatorPrecedenceMap.get(operatorStack.peek()) >= precedence) {
                        postfix.addLast(Integer.toString(2));
                        postfix.addLast(operatorStack.pop());
                    }
                    operatorStack.push(symbol);
                }
            }
        }
        return postfix;
    }
}
