package org.openflow.service;

public class ErrorServiceImp implements ErrorService{

    @Override
    public String getErrorTypeMsg(short ErrorType) {
        // TODO Auto-generated method stub
        String str = null;
        switch(ErrorType){
        case (short) 0 : str = "HELLO协议失败"; break;
        case (short) 1 : str = "无法解读请求"; break;
        case (short) 2 : str = "行动的描述包含错误"; break;
        case (short) 3 : str = "变更流表项时发生问题"; break;
        case (short) 4 : str = "PortMod请求失败"; break;
        case (short) 5 : str = "队列操作失败"; break;
        default : str = "未知错误信息" ; break;
        }
        return str;
    }

    


}
