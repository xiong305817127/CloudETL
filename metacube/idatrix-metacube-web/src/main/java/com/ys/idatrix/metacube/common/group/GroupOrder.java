package com.ys.idatrix.metacube.common.group;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * 指定分组校验顺序 前面的校验失败后 后面的不再校验
 *
 * @author wzl
 */
@GroupSequence({Update.class, Default.class})
public interface GroupOrder {

}
