package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author guann1n9
 * @date 2022/5/5 5:29 PM
 */
@Data
@Builder
public class Response<T> {


    /**
     * 200正常 401 未获取到登录信息  500异常
     */
    public static Integer SUCCESS = 200;

    public static Integer AUTH_ERROR = 401;

    public static Integer SERVER_ERROR = 500;


    /**
     * 响应码  200正常  401 未获取到登录信息   500异常
     */
    @ApiModelProperty("响应码  200正常 401 未获取到登录信息  500异常")
    private Integer code;

    /**
     * 响应消息
     */
    @ApiModelProperty("响应消息")
    private String message;

    /**
     * 业务返回
     */
    @ApiModelProperty("业务返回")
    private T data;


    public static<T> Response<T> success(T data){
        return Response.<T>builder().code(200).message("Success").data(data).build();
    }

    public static<T> Response<T> success(T data, String message){
        return Response.<T>builder().code(200).message(message).data(data).build();
    }

    public static<T> Response<T> error(String message){
        return Response.error(SERVER_ERROR,message);
    }

    public static<T> Response<T> authError(String message){
        return Response.error(AUTH_ERROR,message);
    }

    public static<T> Response<T> error(Integer code, String message){
        return Response.<T>builder().code(code).message(message).data(null).build();
    }




}
