package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McServerPO;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerSearchVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * server数据访问接口
 *
 * @author wzl
 */
public interface McServerMapper {

    int insert(McServerPO serverPO);

    int update(McServerPO serverPO);

    McServerPO getServerPOById(Long id);

    McServerPO getServerPOByIp(String ip);

    List<McServerPO> list(ServerSearchVO searchVO);

    McServerPO getServerPOByIpAndRenterId(@Param("ip") String ip, @Param("renterId") Long renterId);
}
