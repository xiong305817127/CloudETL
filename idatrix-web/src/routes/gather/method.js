/**
 * 数据集成公共方法
 */


let Timer = null;
/**
 * 通过定时器,执行回调函数
 * @param value    必须传入的字符串
 * @param time      定时器，执行时间
 * @param callback  回调函数
 */

export const setTimer = (value,time,callback)=>{

    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    };

    if(value && value.trim()){
        Timer = setTimeout(()=>{
            callback(value);
        },time?time:300);
    }else{
        Timer = setTimeout(()=>{
            callback();
        },time?time:300);
    }
};

