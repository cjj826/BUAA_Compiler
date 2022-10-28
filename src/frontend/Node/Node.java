package frontend.Node;

import frontend.error.ErrorItem;

import java.util.ArrayList;
import java.util.HashMap;

public interface Node {
    ArrayList<ErrorItem> error = new ArrayList<>();
    HashMap<String, Integer> circleDepth = new HashMap<>();
}
