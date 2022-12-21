package midend;

import frontend.ir.Value.BasicBlock;
import frontend.ir.Value.Function;
import frontend.ir.Value.instrs.Branch;
import frontend.ir.Value.instrs.Instr;
import frontend.ir.Value.instrs.Jump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MakeDomTree {
    private ArrayList<Function> functions;
    HashMap<BasicBlock, ArrayList<BasicBlock>> prevMap = new HashMap<>(); //前驱基本块
    HashMap<BasicBlock, ArrayList<BasicBlock>> successMap = new HashMap<>(); //后继基本块
    
    public MakeDomTree(ArrayList<Function> functions, boolean isOnlyRemove) {
        this.functions = functions;
        removeUnreachableBlock(); //删去不可达基本块
        if (!isOnlyRemove) {
            getCFG(); //获取每个基本块的前驱和后继
            getDOM(); //获取基本块之间的支配关系
            getIDom(); // 获取基本块之间的直接支配关系
            getDominanceFrontier(); // 获取支配边界
            getDomTree(); //由直接支配关系获取支配树
        }
    }
    
    public void getCFG() {
        for (Function function : functions) {
            funcGetCFG(function);
        }
    }
    
    public void funcGetCFG(Function function) {
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        getPrevSuc(function);
        //将前驱及后继集合分别写入block
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.setPrevBB(prevMap.get(basicBlock));
            basicBlock.setSucBB(successMap.get(basicBlock));
        }
    }
    
    public void getDomTree() {
        for (Function function : functions) {
            funcGetDomTree(function);
        }
    }
    
    public void funcGetDomTree(Function function) {
        BasicBlock entry = function.getFirstBlock();
        //每个点只可能被一个点直接支配
        buildTree(entry, 1);
    }
    
    public void buildTree(BasicBlock start, int layer) {
        start.setLayerInDomTree(layer);
        for (BasicBlock basicBlock : start.getIdomBB()) {
            basicBlock.setFatherDominator(start); //子节点指向父节点
            buildTree(basicBlock, layer + 1);
        }
    }
    
    public void getDominanceFrontier() {
        for (Function function : functions) {
            funcGetDominanceFrontier(function);
        }
    }
    
    public void funcGetDominanceFrontier(Function function) {
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        for (BasicBlock basicBlock : basicBlocks) {
            ArrayList<BasicBlock> DF = new ArrayList<>();
            for (BasicBlock block : basicBlocks) {
                if (xDFy(basicBlock, block)) {
                    DF.add(block);
                }
            }
            basicBlock.setDfBB(DF);
        }
    }
    
    public boolean xDFy(BasicBlock x, BasicBlock y) {
        //定义：x支配y的前驱但是x不严格支配y
        for (BasicBlock prev : y.getPrevBB()) {
            if (x.getDomBB().contains(prev) && (x.equals(y) || !x.getDomBB().contains(y))) {
                return true;
            }
        }
        return false;
    }
    
    
    public void removeUnreachableBlock() {
        for (Function function : functions) {
            funcRemoveUnreachableBlock(function);
        }
    }
    
    public void getDOM() {
        //获取支配关系
        for (Function function : functions) {
            funcGetDOM(function);
        }
    }
    
    public void getIDom() {
        //获取直接支配关系，从定义出发
        for (Function function : functions) {
            funcGetIDom(function);
        }
    }
    
    public void funcGetIDom(Function function) {
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        for (BasicBlock basicBlock : basicBlocks) {
            ArrayList<BasicBlock> iDominate = new ArrayList<>();
            for (BasicBlock block : basicBlock.getDomBB()) {
                if (AIDomB(basicBlock, block)) {
                    iDominate.add(block);
                }
            }
            basicBlock.setIdomBB(iDominate);
        }
    }
    
    public boolean AIDomB(BasicBlock A, BasicBlock B) {
        //A 直接支配 B 的条件是 A是距离B最近的严格支配B的节点
        ArrayList<BasicBlock> ADom = A.getDomBB();
        if (A.equals(B)) {
            return false;
        }
        if (!ADom.contains(B)) {
            return false;
        }
        for (BasicBlock block : ADom) {
            //如果A不直接支配B，而A又支配B，则A支配的节点中必有一节点严格支配B
            if (!block.equals(A) && !block.equals(B)) {
                if (block.getDomBB().contains(B)) {
                    return false;
                }
            }
        }
        //以上条件都不成立时，A直接支配B
        return true;
    }
    
    public void funcGetDOM(Function function) {
        //获取节点的支配关系
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        BasicBlock entry = function.getFirstBlock();
        
        for (BasicBlock basicBlock : basicBlocks) {
            ArrayList<BasicBlock> dominate = new ArrayList<>();
            HashSet<BasicBlock> visited = new HashSet<>();
            dfs(entry, basicBlock, visited);
            
            for (BasicBlock block : basicBlocks) {
                if (!visited.contains(block)) {
                    //不经过basicBlock就访问不到，说明basicBlock支配block
                    dominate.add(block);
                }
            }
            
            basicBlock.setDomBB(dominate);
        }
    }
    
    public void dfs(BasicBlock start, BasicBlock stop, HashSet<BasicBlock> visited) {
        if (start.equals(stop)) {
            return;
        }
        if (visited.contains(start)) {
            return;
        }
        visited.add(start);
        for (BasicBlock basicBlock : start.getSucBB()) {
            if (!visited.contains(basicBlock) && !basicBlock.equals(stop)) {
                dfs(basicBlock, stop, visited);
            }
        }
    }
    
    public void getPrevSuc(Function function) {
        LinkedList<BasicBlock> basicBlocks = function.getBlocks();
        prevMap = new HashMap<>();
        successMap = new HashMap<>();
        if (function.getName().equals("DoNotCall")) {
            System.out.println("here");
        }
        
        for (BasicBlock basicBlock : basicBlocks) {
            prevMap.put(basicBlock, new ArrayList<>());
            successMap.put(basicBlock, new ArrayList<>());
        }
    
        for (BasicBlock curBlock : basicBlocks) {
            Instr lastInstr = curBlock.getLastInstr();
            if (lastInstr instanceof Branch) {
                BasicBlock trueBB = (BasicBlock) lastInstr.getOperandList().get(1);
                BasicBlock falseBB = (BasicBlock) lastInstr.getOperandList().get(2);
                successMap.get(curBlock).add(trueBB);
                successMap.get(curBlock).add(falseBB);
                prevMap.get(trueBB).add(curBlock);
                prevMap.get(falseBB).add(curBlock);
            } else if (lastInstr instanceof Jump) {
                BasicBlock targetBB = (BasicBlock) lastInstr.getOperandList().get(0);
                successMap.get(curBlock).add(targetBB);
                if (prevMap.get(targetBB) != null) {
                    prevMap.get(targetBB).add(curBlock);
                }
            }
        }
    }
    
    public void funcRemoveUnreachableBlock(Function function) {
        LinkedList<BasicBlock> basicBlocks = new LinkedList<>(function.getBlocks());
        getPrevSuc(function);
        
        //去掉没有前驱的点，算法使用DFS
        HashSet<BasicBlock> visitedBlock = new HashSet<>();
        DFS(function.getFirstBlock(), visitedBlock);
        
        for (BasicBlock basicBlock : basicBlocks) {
            if (!visitedBlock.contains(basicBlock)) {
                basicBlock.remove();
            }
        }
    }
    
    public void DFS(BasicBlock start, HashSet<BasicBlock> visitedBlock) {
        if (visitedBlock.contains(start)) {
            return;
        }
        visitedBlock.add(start);
        for (BasicBlock basicBlock : successMap.get(start)) {
            DFS(basicBlock, visitedBlock);
        }
    }
}
