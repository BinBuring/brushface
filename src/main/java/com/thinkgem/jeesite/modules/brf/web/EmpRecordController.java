/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.brf.web;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.GsonUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.InterfaceUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.brf.entity.DevUser;
import com.thinkgem.jeesite.modules.brf.entity.Device;
import com.thinkgem.jeesite.modules.brf.entity.EmpRecord;
import com.thinkgem.jeesite.modules.brf.service.DevUserService;
import com.thinkgem.jeesite.modules.brf.service.DeviceService;
import com.thinkgem.jeesite.modules.brf.service.EmpRecordService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.uuface.entity.ResultData;
import com.thinkgem.jeesite.modules.uuface.service.InterfaceService;

/**
 * 员工记录Controller
 * @author 张斌
 * @version 2018-08-28
 */
@Controller
@RequestMapping(value = "${adminPath}/brf/empRecord")
public class EmpRecordController extends BaseController {

	@Autowired
	private EmpRecordService empRecordService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private DevUserService devUserService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private InterfaceService interfaceService;
	
	@ModelAttribute
	public EmpRecord get(@RequestParam(required=false) String id) {
		EmpRecord entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = empRecordService.get(id);
		}
		if (entity == null){
			entity = new EmpRecord();
		}
		return entity;
	}
	
	@RequestMapping(value = "empEWM")
	public String empEWM() {
		return "modules/brf/empEWMform";
	}
	@RequestMapping(value = {"list", ""})
	public String list(EmpRecord empRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<EmpRecord> page = empRecordService.findPage(new Page<EmpRecord>(request, response), empRecord); 
		model.addAttribute("page", page);
		return "modules/brf/empRecordList";
	}
	/**
	 * 跳转到手机上传照片页面
	 * @return
	 */
	@RequestMapping(value = "upfrom")
	public String upfrom() {
		
		return "modules/sys/upload";
	}

	@RequestMapping(value = "save")
	public String save(EmpRecord empRecord, Model model, HttpServletRequest request,RedirectAttributes redirectAttributes) {
		//接收参数
		String personGuid = request.getParameter("personGuid");
		String showTime = request.getParameter("showTime");
		String photoUrl = request.getParameter("photoUrl");
		String data = request.getParameter("data");
		String type = request.getParameter("type");
		String recMode = request.getParameter("recMode");
		String idCardInfo = request.getParameter("idCardInfo");
		String devKey = request.getParameter("deviceKey");
		//设置实体属性
		empRecord.setEmp(UserUtils.get(personGuid));
		empRecord.setPhotourl(photoUrl);
		empRecord.setData(data);
		empRecord.setType(type);
		empRecord.setIdcardinfo(idCardInfo);
		empRecord.setRecmode(recMode);
		empRecord.setDevKey(devKey);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date showDate = new Date();
        try {
        	showDate = sdf.parse(showTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		empRecord.setShowtime(showDate);
		if (empRecordService.getByDate(showDate)) {
			empRecordService.save(empRecord);
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "1");
		map.put("success", "true");
		return GsonUtils.getJsonFromObject(map);
	}
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	//@RequestMapping(value = "upload",method = RequestMethod.POST)
	@RequestMapping(value = "upload")
	public String form(MultipartFile file,String type,String user,HttpServletRequest request) {
		String no = request.getParameter("user");
		User us = new User();
		us = systemService.getByNo(no);
		String taskType = "F";
        String featureType = type;
        String userOpt = user;
        if (file.isEmpty()) {
            return "上传文件失败,请检查上传的文件";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        LOG.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        LOG.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        String path = request.getSession().getServletContext().getRealPath("/");
        //String path = request.getContextPath();
        String filePath = path + "/userfiles/1/images/" + fileName;
        us.setPhoto(request.getContextPath() + "/userfiles/1/images/"+fileName);
        us.setIssh("2");
        us.setAuthPhone("2");
        us.setStatus("1");
        systemService.saveUser(us);
        //interfaceService.createEmp(us);
        shenHe(us, request);
        File dest = new File(filePath);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            Long res = 0L;
            if (res != 0) {
                return "任务失败！";
            } else {
            	LOG.error("TaskId插入失败");
                return "TaskId插入失败,请联系管理员！";
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            LOG.error(e.toString(),e);
            return "上传文件失败,请检查上传的文件,IllegalStateException";
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e.toString(),e);
            return "上传文件失败,请检查上传的文件,IOException";
        }
	}
	public void shenHe(User user,HttpServletRequest request){   //预制用户上传照片后审核，授权
		ResultData data = interfaceService.createEmp(user);  //如果guid为null，说明云端上没有信息，创建员工
		Map<String, String> map = InterfaceUtils.getStringToMap(data.getData().toString());
		user.setGuid(map.get("guid"));
		systemService.saveUser(user);
		interfaceService.empimageUrl(user, request);
		if (StringUtils.isNotEmpty(user.getSqDev())) {  //如果用户已经有了授权设备，直接授权，然后返回
			String[] devs = user.getSqDev().split(",");
			for (int i = 0; i < devs.length; i++) { //将授权信息插入到授权表
				Device dev = deviceService.getBySeq(devs[i]);
				DevUser devUser = new DevUser();
				devUser.setUser(user);
				devUser.setDevId(dev.getId());
				devUser.setIsNewRecord(true);
				devUser.setId(IdGen.uuid());
				List<DevUser> list = devUserService.findList(devUser);  //查询表中数据，看是否插入
				if (list.size() <= 0) {
					devUser.setIsNewRecord(true);
					devUser.setId(IdGen.uuid());
					devUserService.save(devUser);
				}
			}
			interfaceService.empDev(user, user.getSqDev());
		}
	}
	@RequestMapping(value = "delete")
	public String delete(EmpRecord empRecord, RedirectAttributes redirectAttributes) {
		empRecordService.delete(empRecord);
		addMessage(redirectAttributes, "删除记录成功");
		return "redirect:"+Global.getAdminPath()+"/brf/empRecord/?repage";
	}

}
