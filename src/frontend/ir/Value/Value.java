package frontend.ir.Value;

import frontend.ir.Use;
import frontend.ir.type.Type;

import java.util.ArrayList;
import java.util.LinkedList;

public class Value {
    public static int REG_NUM = 0;
    public static int LOC_NUM = 0;
    public static int GLO_NUM = 0;
    public static int STR_NUM = 0;
    public static int BLOCK_NUM = 0;
    public static int FR_NUM = 0;
    private LinkedList<Use> users;
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEleName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    private Type type;
    private String name;
    
    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
        this.users = new LinkedList<>();
    }
    
    public Value(Type type) {
        this.type = type;
        this.name = "%reg" + REG_NUM++;
        this.users = new LinkedList<>();
    }
    
    public LinkedList<Use> getUsers() {
        return users;
    }
    
    public void addUser(Use use) {
        this.users.add(use);
    }
    
    public void removeUsers(ArrayList<Use> useList) {
        for (Use use : useList) {
            this.getUsers().remove(use);
        }
    }
    
    public void changeAllUse2UseOther(Value other) {
        LinkedList<Use> newUse = new LinkedList<>(users);
        for (Use user : newUse) {
            //边遍历便删除会出问题
            user.getUser().getOperandList().get(user.getPosition()).removeUse(user);
            user.getUser().getOperandList().set(user.getPosition(), other);
            other.addUser(user); //记录被用了
        }
    }
    
    public void removeUse(Use use) {
        this.users.remove(use);
    }
    
    public void remove() {
    
    }
}
