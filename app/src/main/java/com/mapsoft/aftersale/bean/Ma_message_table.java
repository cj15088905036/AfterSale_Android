package com.mapsoft.aftersale.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/12.
 * <p>
 * java.lang.RuntimeException:
 * Unable to start activity ComponentInfo{com.mapsoft.aftersale/com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity}:
 * org.litepal.exceptions.DatabaseGenerateException: An exception that indicates there was an error with SQL parsing or execution. insert into
 * Ma_message_table(_id, message_type, message_title, message_content, message_sender, message_send_time, message_state) select _id,
 * message_type, message_title, message_content, message_sender, message_send_time, message_state from Ma_message_table_temp
 *
 *
 * 暂时用不到
 */

public class Ma_message_table extends DataSupport {
    private int _id;
    private String message_type;
    private String message_title;
    private String message_preview;
    private String message_content;
    private String message_sender;
    private String message_send_time;
    private String message_state;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage_title() {
        return message_title;
    }

    public void setMessage_title(String message_title) {
        this.message_title = message_title;
    }

    public String getMessage_preview() {
        return message_preview;
    }

    public void setMessage_preview(String message_preview) {
        this.message_preview = message_preview;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getMessage_sender() {
        return message_sender;
    }

    public void setMessage_sender(String message_sender) {
        this.message_sender = message_sender;
    }

    public String getMessage_send_time() {
        return message_send_time;
    }

    public void setMessage_send_time(String message_send_time) {
        this.message_send_time = message_send_time;
    }

    public String getMessage_state() {
        return message_state;
    }

    public void setMessage_state(String message_state) {
        this.message_state = message_state;
    }
}
