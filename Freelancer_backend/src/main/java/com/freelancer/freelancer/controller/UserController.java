package com.freelancer.freelancer.controller;

import com.freelancer.freelancer.constant.Constant;
import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.UserAvatar;
import com.freelancer.freelancer.service.UserService;
import com.freelancer.freelancer.entity.Administrator;
import com.freelancer.freelancer.service.AdministratorService;
import com.freelancer.freelancer.utils.msgutils.Msg;
import com.freelancer.freelancer.utils.msgutils.MsgCode;
import com.freelancer.freelancer.utils.msgutils.MsgUtil;
import com.freelancer.freelancer.utils.sessionutils.SessionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import net.sf.json.JSONObject;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@Api("userController相关api")
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public Msg addUser(@RequestBody Map<String, String> params) {
        String name = params.get(Constant.NAME);
        String password = params.get(Constant.PASSWORD);
        String phone = params.get(Constant.PHONE);
        String email = params.get(Constant.EMAIL);
        String true_name = params.get(Constant.TRUE_NAME);
        String credit_card = params.get(Constant.CREDIT_CARD);

        User newUser = new User();
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setPhone(phone);
        newUser.setE_mail(email);
        newUser.setTrue_name(true_name);
        newUser.setCredit_card(credit_card);
        newUser.setType(0);

        User duplicate = userService.checkDuplicate(name);

        if (duplicate == null) {
            userService.addUser(newUser);

            JSONObject obj = new JSONObject();
            obj.put(Constant.NAME, newUser.getName());
            obj.put(Constant.USER_TYPE, newUser.getType());
            SessionUtil.setSession(obj);

            JSONObject data = JSONObject.fromObject(newUser);
            data.remove(Constant.PASSWORD);

            return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.REGISTER_SUCCESS_MSG, data);
        } else {
            return MsgUtil.makeMsg(MsgCode.DUPLICATE_USER_ERROR);
        }

    }

    @RequestMapping("/logout")
    public Msg logout() {
        Boolean status = SessionUtil.removeSession();

        if (status) {
            return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.LOGOUT_SUCCESS_MSG);
        }
        return MsgUtil.makeMsg(MsgCode.ERROR, MsgUtil.LOGOUT_ERR_MSG);
    }

    @RequestMapping("/checkSession")
    public Msg checkSession() {
        JSONObject auth = SessionUtil.getAuth();

        if (auth == null) {
            return MsgUtil.makeMsg(MsgCode.NOT_LOGGED_IN_ERROR);
        } else {
            return MsgUtil.makeMsg(MsgCode.SUCCESS, MsgUtil.LOGIN_SUCCESS_MSG, auth);
        }
    }

    @RequestMapping("/getUserInfo")
    public User getUserInfo(@RequestBody Map<String, String> params) {
        String name = params.get(Constant.NAME);
        JSONObject auth = SessionUtil.getAuth();
        if (name.equals(auth.getString(Constant.NAME))) {
            User user = userService.findByName(name);
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @RequestMapping("/changeUserStatus")
    public boolean changeUserStatus(@RequestBody Map<String, String> params) {
        Integer u_id = Integer.parseInt(params.get("u_id"));
        Integer status = Integer.parseInt(params.get("status"));
        JSONObject auth = SessionUtil.getAuth();
        String u_session_type = auth.getJSONArray("userType").getJSONObject(0).getString("authority");

        if (u_session_type == null) {
            return false;
        }
        User user_ee = userService.findById(u_id);
        if (u_session_type != "ROLE_ADMIN" || user_ee.getType() == 1) {
            return false;
        }
        return userService.changeUserStatus(u_id, status);
    }

    @RequestMapping("/changeUserType")
    public boolean changeUserType(@RequestBody Map<String, String> params) {
        Integer u_id = Integer.parseInt(params.get("u_id"));
        Integer type = Integer.parseInt(params.get("type"));
        JSONObject auth = SessionUtil.getAuth();
        String u_session_type = auth.getJSONArray("userType").getJSONObject(0).getString("authority");

        if (u_session_type == null) {
            return false;
        }
        User user_ee = userService.findById(u_id);
        if (u_session_type != "ROLE_ADMIN" || user_ee.getType() == 1) {
            return false;
        }
        return userService.changeUserType(u_id, type);
    }

    @RequestMapping("/getUsers")
    public List<User> getUsers(@RequestBody Map<String, String> params) {
        Integer PageNum = Integer.parseInt(params.get("pagenum"));
        Integer PageContentNum = Integer.parseInt(params.get("size"));
        if (PageNum <= 0 || PageContentNum <= 0) {
            PageNum = 1;
            PageContentNum = 20;
        }
        String keyword = params.get("keyword");
        if (keyword == null)
            keyword = "";
        String sort = params.get("sortby");
        Integer sortby = sort != null ? Integer.parseInt(sort) : 0;
        List<User> users;
        if (sortby == 1) {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "u_id"));
            users = userService.getUsers(keyword, pageable).getContent();

        } else {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.DESC, "u_id"));
            users = userService.getUsers(keyword, pageable).getContent();
        }
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            it.next().setPassword(null);
        }
        return users;

    }

    @RequestMapping("/getPostman")
    public List<Map<String, Object>> getPostman(@RequestBody Map<String, String> params) {
        System.out.println("get Post people");
        Integer w_id = Integer.valueOf(params.get("w_id"));
        return userService.getPostedUser(w_id);
    }

    @RequestMapping("/changeInfo")
    public JSONObject changeInfo(@RequestBody Map<String, String> params) {
        System.out.println("change info");
        Integer u_id = Integer.valueOf(params.get("u_id"));
        String name = params.get("name");
        String e_mail = params.get("email");

        String phone = params.get("phone");
        User user = userService.changeInfo(name, phone, e_mail, u_id);
        JSONObject userinfo = new JSONObject();
        userinfo.put("name", user.getName());
        userinfo.put("phone", user.getPhone());
        userinfo.put("e_mail", user.getE_mail());

        return userinfo;
    }

    @RequestMapping("/uploadAvatar")
    public Boolean UpAvatar(@RequestBody Map<String, String> params) {
        System.out.println("upload Avatar");
        Integer u_id = Integer.valueOf(params.get("u_id"));
        String avatar = params.get("avatar");
        UserAvatar u_Avatar = new UserAvatar(u_id, avatar);
        return userService.saveAvatar(u_Avatar);
    }

}
