/**
 * 文件中定义质量分析中所有的常量
 * @author pwj 2018/09/27
 */

//表格默认的每页请求条数
export const DEFAULT_PAGESIZE = 10;
//表格默认的请求页
export const DEFAULT_PAGE = 1;
//报错的提示语
export const ERROR_POINT = "请求异常，请刷新页面后重试！";

//分析执行的状态 分4类

//初始状态
export const initStatus = new Map([
    ["Waiting", "等待执行"],
    ["Undefined", "等待执行"]
]);
//执行完成
export const finishStatus = new Map([
    ["Finished", "完成"]
]);


//运行状态
export const runStatus = new Map([
    ["Running", "执行中"],
    ["Preparing executing", "准备执行"],
    ["Initializing", "执行初始化"]
]);
//暂停状态
export const pauseStatus = new Map([
    ["Paused", "暂停"]
]);


//终止状态
export const stopStatus = new Map([
    ["Finished (with errors)", "完成（有错误)"],
    ["Stopped", "终止"],
    ["Halting", "挂起"]
]);


//错误状态
export const errorStatus = new Map([
    ["TimeOut", "超时"],
    ["Failed", "失败"],
    ["Unknown", "未知异常"]
]);

//部分组件存在功能需禁用

//下列组件存在  禁用trans引擎
export const transArgs = ["AccessInput", "CsvInput", "JsonInput", "ExcelInput", "TextFileInput", "GetFileNames", "Flattener", "InsertUpdate", "ScriptValueMod",
    "DBLookup", "SetVariable", "SystemInfo", "MergeJoin", "JoinRows", "SortedMerge", "GetVariable", "MultiwayMergeJoin", "RowGenerator", "MergeRows", "Dummy", "FilterRows", "FuzzyMatch", "DBProc", "Validator",
    "ConcatFields", "Rest", "WebServiceLookup", "HTTPPOST"];

//下列组件存在   禁用中断恢复,本地执行
export const disabledArgs = ["JsonOutput", "ClosureGenerator", "SortRows", "StreamLookup", "UniqueRowsByHashSet", "JoinRows"];

//worktools页面展示分类
export const worktoolsType = new Map([
    ["质量分析", "12"],
    ['输入', '0']
]);


export const getScreenSize = () => {
    let obj = {};
    let height = document.body.clientHeight;
    let width = document.body.clientWidth;
    switch (SITE_THEME) {
        case 'government':
            obj.moveX = 220;
            obj.moveY = 100;
            return obj;
        default:
            if (height >= 900 && width >= 1440) {
                obj.moveX = 0;
                obj.moveY = 120;
            } else {
                obj.moveX = 0;
                obj.moveY = 0
            }
            return obj;
    }
};
