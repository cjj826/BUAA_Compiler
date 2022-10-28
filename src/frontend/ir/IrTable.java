package frontend.ir;

import java.util.HashMap;

public class IrTable {
    private final HashMap<String, IrTableItem> irMap;
    private final IrTable fatherIrMap; //指向父符号表
    
    public IrTable(IrTable fatherIrMap) {
        this.irMap = new HashMap<>();
        this.fatherIrMap = fatherIrMap;
    }
    
    public void addItem(IrTableItem item) {
        this.irMap.put(item.getName(), item);
    }
    
    public IrTableItem findItem(String name) {
        if (irMap.containsKey(name)) {
            return irMap.get(name);
        } else {
            if (fatherIrMap != null) {
                return fatherIrMap.findItem(name);
            } else {
                return null;
            }
        }
    }
}
