package nl.lvanarkel.compilercraft.compiler.datastructures;

public enum TokenType {
    //Variables and values
    INTVAR,
    BOOLVAR,
    INT,
    BOOL,
    VAR,

    //Operators
    ADD,
    SUB,
    MULT,
    DIV,
    EQ,
    NEQ,
    LT,
    LE,
    GE,
    GT,
    OR,
    AND,
    NOT,
    ASSIGN,
    ADDASSIGN,
    SUBASSIGN,
    MULTASSIGN,
    DIVASSIGN,
    ORASSIGN,
    ANDASSIGN,

    //Brackets
    PAROPEN,
    PARCLOSE,
    BROPEN,
    BRCLOSE,

    //Statements
    IF,
    THEN,
    ELSE,
    WHILE,
    OUT

}
