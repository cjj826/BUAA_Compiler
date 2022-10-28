package frontend.ir.Value.instrs;

public enum Op {
    //binary 二元运算
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    Shl,
    Shr,
    And,
    Or,
    Xor,
    
    Lt,
    Le,
    Ge,
    Gt,
    Eq,
    Ne,
    //Mem
    Alloca,
    Load,
    Store,
    GEP, //get element ptr
    Phi,
    MemPhi,
    LoadDep,
    //vector op
    InsertEle,
    ExtractEle,
    //terminator op
    Br,
    Call,
    Ret,
    //not op
    Not
}
