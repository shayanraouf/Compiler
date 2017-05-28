package com.AST;
import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public abstract class ExprNode extends AST {

    public static final int tINVALID = 0; // invalid expression type
    public static final int tINTEGER = 1; // integer expression type
    public static final int tVECTOR = 2;  // vector expression type
    /** Track expression type (integer or vector) for each expr node.
     *  This is the type of the associated value not the getNodeType()
     *  used by an external visitor to distinguish between nodes. */
    int evalType;

    public int getEvalType() { return evalType; }
    public ExprNode(Token payload) { super(payload); }

    /** ExprNode's know about the type of an expresson, include that */
    public String toString() {
        if ( evalType != tINVALID ) {
            return super.toString()+"<type="+
                    (evalType == tINTEGER ? "tINTEGER" : "tVECTOR")+">";
        }
        return super.toString();
    }
}