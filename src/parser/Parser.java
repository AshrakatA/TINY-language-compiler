package parser;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {

    static ArrayList<String> Tokens = new ArrayList<>();
    static ArrayList<String> Values = new ArrayList<>();
    static ArrayList<node> Treenode = new ArrayList<>();
    static boolean rw = false;
    static int C = 0;
    static boolean e = false;
    static int space = 2;

    static class node {

        ArrayList<node> Sibling = new ArrayList<>();
        node leftchild;
        node rightchild;
        node sibling;
        String value;
        String type;
        String siblingType = "v";
        int level = 0;
        int x = 1;
        int y = 1;

        public void addSibling(node n) {
            Sibling.add(n);
        }

        public node getSibling(int i) {
            return this.Sibling.get(i);
        }

        public boolean hasSiblings() {
            if (Sibling.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }

        public String printsibling() {
            String str = "";
            for (int i = 0; i < Sibling.size(); i++) {

                str += "Sibling " + (i + 1) + " is " + Sibling.get(i).value + " " + Sibling.get(i).siblingType + "\n";
            }
            return str;
        }

    }

    static class parser {

        FileWriter fw;
        ArrayList<String> T;
        int Counter;
        String place;
        ArrayList<node> nodes;
        int space = 2;

        public void setnodes(node n) {
            if (n.leftchild == null && n.rightchild == null && n.sibling == null) {
                return;
            }
            if (n.leftchild != null) {
                n.leftchild.x = n.x - 1;
                n.leftchild.y = n.y - 2;
                if (n.rightchild == null) {
                    n.leftchild.x = n.x;
                }

                if (n.leftchild.type.equals("stmt")) {
                    n.leftchild.x = n.x - 3;
                }

                setnodes(n.leftchild);
            }

            if (n.rightchild != null) {
                n.rightchild.x = n.x + 1;
                n.rightchild.y = n.y - 2;
                if (n.leftchild == null) {
                    n.rightchild.x = n.x;
                }
                if (n.leftchild != null && n.leftchild.sibling != null) {
                    n.rightchild.x = n.leftchild.sibling.x + 2;
                }
                setnodes(n.rightchild);
            }

            if (n.sibling != null) {
                n.sibling.x = n.x + 4;
                n.sibling.y = n.y;
                setnodes(n.sibling);
            }

        }

        public int getnode(String value) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).value.equals(value)) {
                    return i;
                }
            }
            return -1;
        }

        parser() {
            this.T = Tokens;
            this.Counter = C;
            this.nodes = Treenode;
            try {
                this.fw = new FileWriter("C:\\Users\\ahmed\\Desktop\\parser_output.txt");
            } catch (IOException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean match(String expectedToken) throws IOException {
            if (T.get(Counter).equals(expectedToken)) {
                fw.write(expectedToken);
                fw.append("\r\n");
                fw.flush();
                Counter++;
                return true;
            }
            fw.write("Error, expected (" + expectedToken + ") in " + this.place);
            fw.append("\r\n");
            fw.flush();
            return false;

        }

        public void program() throws IOException {
            fw.append("program found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n = stmt_seq();
            setnodes(n);
            for (int i = 0; i < nodes.size(); i++) {
                System.out.println("node is " + nodes.get(i).value + " lvl: " + nodes.get(i).level + " \nand has " + nodes.get(i).printsibling());
            }
        }

        public node stmt_seq() throws IOException {
            fw.write("stmt_seq found");
            fw.append("\r\n");
            fw.flush();
            node n;
            n = stmt();
            node temp = n;
            while (Counter < T.size() && match(";")) {
                node s = stmt();
                temp.addSibling(temp.sibling = s);
                s.siblingType = "h";
                temp = temp.getSibling(temp.Sibling.size() - 1);
            }
            return n;
        }

        public node stmt() throws IOException {
            fw.write("stmt found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.type = "stmt";
            switch (T.get(Counter)) {
                case "if":
                    n = if_stmt();
                    nodes.add(n);
                    break;
                case "repeat":
                    n = repeat_stmt();
                    nodes.add(n);
                    break;
                case "Identifier":
                    n = assign_stmt();
                    nodes.add(n);
                    break;
                case "read":
                    n = read_stmt();
                    nodes.add(n);
                    break;
                case "write":
                    n = write_stmt();
                    nodes.add(n);
                    break;
                default:
                    break;
            }
            return n;
        }

        public node assign_stmt() throws IOException {
            fw.write("assign-stmt found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.value = "assign-stmt\n" + "(" + Values.get(Counter) + ")";
            n.type = "stmt";
            this.place = "assignm-stmt";
            match("Identifier");
            match(":=");
            n.addSibling(n.leftchild = exp());
            return n;
        }

        public node read_stmt() throws IOException {
            fw.write("read-stmt found");
            fw.append("\r\n");
            fw.flush();
            this.place = "read-stmt";
            node n = new node();
            match("read");
            n.type = "stmt";
            n.value = "read-stmt\n" + "(" + Values.get(Counter) + ")";
            match("Identifier");
            return n;
        }

        public node write_stmt() throws IOException {
            fw.write("write-stmt found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.value = "write-stmt\n";
            n.type = "stmt";
            this.place = "write-stmt";
            match("write");
            n.addSibling(n.rightchild = exp());
            return n;
        }

        public node repeat_stmt() throws IOException {
            fw.write("repeat-stmt found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.value = "repeat-stmt\n";
            n.type = "stmt";
            this.place = "repeat-stmt";
            match("repeat");
            n.addSibling(n.leftchild = stmt_seq());
            match("until");
            n.addSibling(n.rightchild = exp());
            return n;
        }

        public node if_stmt() throws IOException {
            fw.write("if-stmt found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.value = "if-stmt\n";
            n.type = "stmt";
            this.place = "if-stmt";
            match("if");
            n.addSibling(n.leftchild = exp());
            match("then");
            n.addSibling(n.rightchild = stmt_seq());
            if (T.get(Counter).equals("else")) {
                match("else");
                n.addSibling(n.rightchild = stmt_seq());
            }
            match("end");
            return n;
        }

        public node exp() throws IOException {
            fw.write("exp found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.type = "expression";
            n = simple_exp();
            while (T.get(Counter).equals("<") || T.get(Counter).equals("=")) {
                node ns = new node();
                ns.value = "op\n" + "(" + T.get(Counter) + ")";
                ns.type = "expression";
                match(T.get(Counter));
                ns.addSibling(ns.leftchild = n);
                ns.addSibling(ns.rightchild = simple_exp());
                n = ns;
                nodes.add(n);
            }
            return n;
        }

        public node simple_exp() throws IOException {
            fw.write("simple-exp found");
            fw.append("\r\n");
            fw.flush();
            node n = new node();
            n.type = "expression";
            n = term();
            while (T.get(Counter).equals("+") || T.get(Counter).equals("-")) {
                node temp = new node();
                temp.type = "expression";
                temp.value = "op\n(" + T.get(Counter) + ")";
                match(T.get(Counter));
                temp.addSibling(temp.leftchild = n);
                temp.addSibling(temp.rightchild = term());
                n = temp;
                nodes.add(n);
            }

            return n;
        }

        node term() throws IOException {
            fw.write("term found");
            fw.append("\r\n");
            fw.flush();
            node temp, newtemp = new node();
            temp = factor();
            while (T.get(Counter).equals("*") || T.get(Counter).equals("/")) {
                newtemp.value = "op\n (" + T.get(Counter) + ")";
                newtemp.type = "expression";
                nodes.add(newtemp);
                match(T.get(Counter));
                newtemp.addSibling(newtemp.leftchild = temp);
                newtemp.addSibling(newtemp.rightchild = factor());
                temp = newtemp;
            }

            return temp;
        }

        node factor() throws IOException {
            fw.write("factor found");
            fw.append("\r\n");
            fw.flush();
            node temp = new node();
            switch (T.get(Counter)) {
                case "(":
                    match("(");
                    temp = exp();
                    match(")");
                    break;
                case "number":
                    temp.value = "const\n(" + Values.get(Counter) + ")";
                    match("number");
                    temp.type = "expression";
                    nodes.add(temp);
                    break;
                case "Identifier":
                    temp.value = "id\n (" + Values.get(Counter) + ")";
                    temp.type = "expression";
                    match("Identifier");
                    nodes.add(temp);
                    break;
                default:
                    break;
            }
            return temp;
        }

        static public void fill() {
            try {
                String output = "";
                File f = new File("C:\\Users\\ahmed\\Desktop\\test.txt");
                BufferedReader br = new BufferedReader(new FileReader(f));
                String str;
                while ((str = br.readLine()) != null) {
                    output += str;
                }
                output += " ";
                getTokens(output);
                parser p = new parser();
                p.program();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void getTokens(String str) {
        ArrayList<String> ReservedWords = new ArrayList<>(Arrays.asList("if", "then", "else", "end", "repeat", "until", "read", "write"));
        ArrayList<Character> Symbols = new ArrayList<>(Arrays.asList('*', '-', '+', '/', '=', '<', '(', ')', ';', ':'));
        Character c;
        Character temp;
        int j;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (Symbols.contains(c)) {
                if (c.equals(':')) {
                    Character t = str.charAt(i + 1);
                    if (t.equals('=')) {
                        //Tokens.add("assignment");
                        Tokens.add(":=");
                        Values.add(":=");
                        System.out.println(c + "= : assignment");
                        i++;
                    }

                } else {
                    // Tokens.add("Symbol");
                    Tokens.add("" + c);
                    Values.add("" + c);
                    System.out.println(c + " : Symbol");
                }
            } else if (c.equals('{')) {
                //Tokens.add("Comment");
                temp = c;
                String comment = "";
                j = i;
                while (!temp.equals('}')) {
                    comment += temp;
                    temp = str.charAt(++j);
                }
                System.out.println(comment + temp + " : Comment");
                i = j - 1;
            } else if (Character.isDigit(c)) {
                Tokens.add("number");
                temp = c;
                String number = "";
                j = i;
                while (Character.isDigit(temp)) {
                    number += temp;
                    temp = str.charAt(++j);
                }
                System.out.println(number + " : number");
                Values.add(number + "");
                i = j - 1;
            } else if (Character.isLetter(c)) {
                temp = c;
                String word = "";
                j = i;
                while (Character.isLetter(temp)) {
                    rw = false;
                    word += temp;
                    j++;
                    if (j < str.length()) {
                        temp = str.charAt(j);
                    } else {
                        break;
                    }
                    if (ReservedWords.contains(word)) {
                        Tokens.add(word);
                        Values.add(word);
                        System.out.println(word + " : Reserved Word");
                        rw = true;
                        break;
                    }
                }
                if (!rw) {
                    Tokens.add("Identifier");
                    Values.add(word);
                    System.out.println(word + " : Identifier");
                    rw = false;
                }
                i = j - 1;
            }

        }
        //for (int i = 0; i < Values.size(); i++) {
        //System.out.println("" + Values.get(i));
        //}
    }

    public static void main(String[] args) throws IOException {
        parser p = new parser();
        p.fill();
    }

}
