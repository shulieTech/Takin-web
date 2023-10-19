package io.shulie.takin.web.biz.constant;

public class ScriptCsvCreateTaskState {
    //生成状态(0：生成中，1：排队中，2：已生成，3已取消)
    public static Integer IN_FORMATION  = 0;
    public static Integer BE_QUEUING  = 1;
    public static Integer GENERATED  = 2;
    public static Integer CANCELLED  = 3;

    public static Integer FAIL  = 4;

}
