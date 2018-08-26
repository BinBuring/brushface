/**  
 * All rights Reserved, Designed By www.trustepay.cn
 * @Title：UfaceToken.java   
 * @Package：com.thinkgem.jeesite.modules.uface.entity   
 * @Description：
 * @author：信逸科技     
 * @date：2018年8月26日   
 * @version  
 * @Copyright：2018 www.trustepay.cn Inc. All rights reserved. 
 * @History：
 */  
package com.thinkgem.jeesite.modules.uface.entity;

import lombok.Data;

/**   
 * @ClassName：UfaceToken   
 * @Description：
 * @author：信逸科技 
 * @date：2018年8月26日
 *     
 * @Copyright：2018 www.trustepay.cn Inc. All rights reserved. 
 * 
 */
@Data
public class UfaceToken {
    private Long id;
	private String token;
	private String state;

}
