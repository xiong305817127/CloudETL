-- Function queryParentIdsByDeptCode
DELIMITER $$

USE `idatrix_unisecurity`$$

DROP FUNCTION IF EXISTS `queryParentIdsByDeptCode`$$

CREATE DEFINER=`root`@`%` FUNCTION `queryParentIdsByDeptCode`(deptCode VARCHAR(20),renterId INT) RETURNS VARCHAR(4000) CHARSET utf8
BEGIN
DECLARE sTemp VARCHAR(4000);
DECLARE sTempParentIds VARCHAR(4000);
SET sTemp = '$';
SET sTempParentIds = '$';
SELECT GROUP_CONCAT(id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE dept_code =deptCode AND renter_id=renterId;
WHILE sTempParentIds IS NOT NULL DO
SET sTemp = CONCAT(sTemp,',',sTempParentIds);
SELECT GROUP_CONCAT(parent_id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE FIND_IN_SET(id,sTempParentIds)>0;
END WHILE;
RETURN sTemp;
END$$

DELIMITER ;


-- Function queryParentIdsByUnifiedCode

DELIMITER $$

USE `idatrix_unisecurity`$$

DROP FUNCTION IF EXISTS `queryParentIdsByUnifiedCode`;

CREATE DEFINER=`root`@`%` FUNCTION `queryParentIdsByUnifiedCode`(unifiedCode VARCHAR(20),renterId INT) RETURNS VARCHAR(4000) CHARSET utf8
BEGIN
DECLARE sTemp VARCHAR(4000);
DECLARE sTempParentIds VARCHAR(4000);
SET sTemp = '$';
SET sTempParentIds = '$';
SELECT GROUP_CONCAT(id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE unified_credit_code =unifiedCode AND renter_id=renterId;
WHILE sTempParentIds IS NOT NULL DO
SET sTemp = CONCAT(sTemp,',',sTempParentIds);
SELECT GROUP_CONCAT(parent_id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE FIND_IN_SET(id,sTempParentIds)>0;
END WHILE;
RETURN sTemp;
END;

DELIMITER ;