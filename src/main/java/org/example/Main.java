import java.util.*;

public class Main {
    private static final List<String> history = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Calculator!");

        while (true) {
            System.out.print("Please enter your arithmetic expression: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("history")) {
                showHistory();
                continue;
            }

            try {
                double result = evaluate(input);
                System.out.println("Result: " + result);
                history.add(input + " = " + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("Do you want to continue? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("n")) {
                System.out.println("Thank you for using the Calculator!");
                break;
            }
        }
        scanner.close();
    }

    private static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No history available.");
        } else {
            System.out.println("Calculation History:");
            for (String entry : history) {
                System.out.println(entry);
            }
        }
    }

    private static double evaluate(String expression) throws Exception {
        expression = expression.replaceAll("\\s", "");
        return parseExpression(expression);
    }

    private static double parseExpression(String expression) throws Exception {
        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    while (true) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    while (true) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) {
                            double divisor = parseFactor();
                            if (divisor == 0) throw new ArithmeticException("Division by zero");
                            x /= divisor;
                        }
                        else if (eat('%')) x %= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') {
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = expression.substring(startPos, this.pos);
                        x = parseFactor();
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("abs")) x = Math.abs(x);
                        else if (func.equals("round")) x = Math.round(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor());

                    return x;
                }
            }.parse();
        } catch (RuntimeException e) {
            throw new Exception("Invalid expression");
        }
    }
}
