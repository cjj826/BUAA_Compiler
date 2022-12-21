package frontend.ir;

import frontend.ir.Value.User;
import frontend.ir.Value.Value;

public class Use {
    private Value value;
    private User user;
    private int position;
    
    public Use(User user, int position) {
        this.user = user;
        this.position = position;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
}
