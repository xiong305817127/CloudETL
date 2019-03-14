// 元数据相关工具函数

/**
 * 根据tree的value值取得label
 * @param  {variable} value   可能是id值，也可能是数组（tree结构得到的值）
 * @param  {array}    options 供选择的选项列表 [{ label, value }]
 * @return {string}           返回label
 */
export const getLabelByTreeValue = (value = '', options) => {
  let parseId = value;
  try {
    parseId = JSON.parse(value);
  } catch (err) {}
  const id = Array.isArray(parseId) ? parseId[parseId.length - 1] : parseId;
  const found = options.find(item => item.value == id);
  return found ? found.label : '';
};

/**
 * 获取字段默认长度
 * @param  {string} dataType 字段类型
 */
export const getDefaultLength = (dataType) => {
  let length = '';
  switch(dataType.toUpperCase()) {
    // 整型
    // case 'TINYINT': length = 1; break;
    // case 'SMALLINT': length = 2; break;
    // case 'MEDIUMINT': length = 3; break;
    // case 'INT': length = 4; break;
    // case 'INTEGER': length = 4; break;
    // case 'BIGINT': length = 4; break;
    case 'TINYINT': length = 4; break;
    case 'SMALLINT': length = 6; break;
    case 'MEDIUMINT': length = 9; break;
    case 'INT': length = 11; break;
    case 'INTEGER': length = 11; break;
    case 'BIGINT': length = 20; break;

    // 浮点型
    case 'FLOAT': length = 0; break;
    case 'DOUBLE': length = 0; break;
   // case 'REAL': length = '8,2'; break;
    case 'DECIMAL': length = 10; break;
    case 'NUMERIC': length = 10; break;

    // 日期
    // case 'DATE': length = 3; break;
    //case 'DATE': length = null; break;
    // case 'DATETIME': length = 8; break;
    //case 'DATETIME': length = null; break;
    // case 'TIMESTAMP': length = 4; break;
    //case 'TIMESTAMP': length = null; break;
    // case 'TIME': length = 3; break;
    //case 'TIME': length = null; break;
    // case 'YEAR': length = 1; break;
    case 'YEAR': length = 4; break;

    // 串类型
    case 'BIT': length = 1; break;
    case 'CHAR': length = 1; break;
    case 'VARCHAR': length = 255; break;
    case 'BINARY': length = 1; break;
    default :
      length = 10;
    // blob型
    //case 'TINYBLOB': length = null; break;
    //case 'BLOB': length = null; break;
    //case 'LONGBLOB': length = null; break;
  }
  return length;
};


/**
 * 获取字段默认长度
 * @param  {string} dataType 字段类型
 */
export const getDefaultPrecision = (dataType) => {
  let length = '';
  switch(dataType.toUpperCase()) {
    // 整型
    // case 'TINYINT': length = 1; break;
    // case 'SMALLINT': length = 2; break;
    // case 'MEDIUMINT': length = 3; break;
    // case 'INT': length = 4; break;
    // case 'INTEGER': length = 4; break;
    // case 'BIGINT': length = 4; break;
    case 'DECIMAL':
    case 'NUMERIC': 
    case 'FLOAT':
    case 'DOUBLE': length = 0; break;
    default:
      length = "";
  }
  return length;
};