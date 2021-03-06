package Test;

import GUI.LogFrame;
import parser.*;
import parser.ast.ExitStmt;
import parser.ast.Stmt;
import visitors.evaluation.Eval;

import java.io.InputStreamReader;
import java.io.PrintStream;

public class TestMain {
    public static void main(String [] args){
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_RESET = "\u001B[0m";
        LogFrame lf = new LogFrame();
        Eval eval = new Eval();
        Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(System.in));
        Parser parser = new StreamParser(tokenizer);
        Stmt stmt;
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                lf.setVisible(true);
            }
        });
        System.setErr(new PrintStream(System.err){
            public void println(String s) {
                if (s.contains("[ServerDebug]:")) lf.printServerLog(s.replace("[ServerDebug]:", ""));
                else if (s.contains("[ClientDebug]:")) lf.printClientLog(s.replace("[ClientDebug]:", ""));
                else super.println(s);
            }
        });
        System.setOut(new PrintStream(System.out){
            public void println(String s){
                if(s.contains("[Notification]:")) lf.printDebugLog(s.replace("[Notification]: ", ""));
                else if(s.contains("[RemovedNotification]:")){
                    super.println("\n"+s.replace("[RemovedNotification]: ", ""));
                    eval.setMode(false);
                    super.print(ANSI_BLUE+"["+eval.getPrompt()+"]> "+ANSI_RESET);
                }
                else super.println(s);
            }
        });
            do {
                try{
                    System.err.print(ANSI_BLUE+"["+eval.getPrompt()+"]> "+ANSI_RESET);
                    stmt = ((StreamParser) parser).parseStmt();
                    if(stmt instanceof ExitStmt) break;
                    stmt.accept(eval);
                }
                catch(ParserException pe) {
                    System.err.println("Syntax error: " + pe.getMessage());
                }
            }while(true);
            System.err.println("Exited");
            System.exit(0);
    }
}
