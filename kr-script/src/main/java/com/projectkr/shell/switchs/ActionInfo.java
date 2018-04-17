package com.projectkr.shell.switchs;

import java.util.ArrayList;

public class ActionInfo {
    String title;
    String desc;
    String getState;
    String setState;
    boolean selected;

    String start;
    ActionScript getStateType = ActionScript.SCRIPT;
    ActionScript setStateType = ActionScript.SCRIPT;

    boolean root;
    boolean confirm;

    enum  ActionScript {
        SCRIPT,
        ASSETS_FILE
    }
}
