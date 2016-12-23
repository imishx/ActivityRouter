package com.github.imishx.activityrouter.router;

/**
 * @author 李博
 * @date 2016年12月12日
 * @desc
 */
public class RouterMsg {

    public int cmd;
    public String extra;

    public RouterMsg(int cmd, String extra) {
        this.cmd = cmd;
        this.extra = extra;
    }
}
